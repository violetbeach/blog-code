## ON DUPLICATE KEY UPDATE로 인한 데드락 해결

## 문제 쿼리

아래는 기존의 존재하던 PHP 구문이다.

```php
public function update_process($fk_pull_id){
    $server_name = gethostname
    $this->_db_connect();
    // Begin transaction
    $this->open_db->trans_begin
    // Lock the rows to be updated
    $sql_lock = "SELECT * FROM `external_mail`.`pop3_pull_processes` WHERE `fk_pull_id` = ? FOR UPDATE";
    $this->open_db->query($sql_lock, array($fk_pull_id
    // Check if rows were found
    if ($this->open_db->affected_rows() > 0) {
        // Rows were found, update the records
        $sql_update = "UPDATE `external_mail`.`pop3_pull_processes` SET `server_name` = ?, `update_date` = NOW() WHERE `fk_pull_id` = ?";
        $this->open_db->query($sql_update, array($server_name, $fk_pull_id));
    } else {
        // Rows were not found, insert a new record
        $sql_insert = "INSERT INTO `external_mail`.`pop3_pull_processes` (server_name, fk_pull_id, start_date, update_date) VALUES(?, ?, NOW(), NOW())";
        $this->open_db->query($sql_insert, array($server_name, $fk_pull_id));
    
    // Commit the transaction
    $this->open_db->trans_commit();
}
```

해당 구문을 SQL로 구성하면 아래와 같다. 
```mysql
START TRANSACTION;
SELECT * FROM `external_mail`.`pop3_pull_processes`
WHERE `fk_pull_id` = :pullId FOR UPDATE;

# 존재한다면
UPDATE `external_mail`.`pop3_pull_processes` SET `update_date` = NOW() WHERE `fk_pull_id` = :pullId;
WHERE

# 존재하지 않는다면
INSERT INTO `external_mail`.`pop3_pull_processes` (fk_pull_id, start_date, update_date)
VALUES(:pullId, NOW(), NOW());

COMMIT;
```

해당 쿼리를 수행하는 서버를 2대 돌리고 있고, 아래의 예외가 터졌다.

```
Query error: Deadlock found when trying to get lock; try restarting transaction - Invalid query: INSERT INTO `external_mail`.`pop3_pull_processes` (server_name, fk_pull_id, start_date, update_date)
VALUES(?, ?, NOW(), NOW());
```

pop3_pull_processes 테이블을 사용하는 곳은 해당 서버가 유일하다.

데드락이 대체 왜 발생하는 걸까..?

(미리 말하지만 Index 문제는 아니다!)

## 디버깅

MySQL의 5.7에서 Lock을 확인하려면 아래의 복잡한 SQL을 수행해야 한다.
- MySQL 8.0은 SELECT * FROM performance_schema.data_locks; 한줄 이면 된다..

```sql
SELECT
    straight_join w.trx_mysql_thread_id waiting_thread,
        w.trx_id waiting_trx_id,
        w.trx_query waiting_query,
        b.trx_mysql_thread_id blocking_thread,
        b.trx_id blocking_trx_id,
        b.trx_query blocking_query,
        bl.lock_id blocking_lock_id,
        bl.lock_mode blocking_lock_mode,
        bl.lock_type blocking_lock_type,
        bl.lock_table blocking_lock_table,
        bl.lock_index blocking_lock_index,
        wl.lock_id waiting_lock_id,
        wl.lock_mode waiting_lock_mode,
        wl.lock_type waiting_lock_type,
        wl.lock_table waiting_lock_table,
        wl.lock_index waiting_lock_index
FROM
    information_schema.INNODB_LOCK_WAITS ilw
    INNER JOIN information_schema.INNODB_TRX b ON b.trx_id = ilw.blocking_trx_id
    INNER JOIN information_schema.INNODB_TRX w ON w.trx_id = ilw.requesting_trx_id
    INNER JOIN information_schema.INNODB_LOCKS bl ON bl.lock_id = ilw.blocking_lock_id
    INNER JOIN information_schema.INNODB_LOCKS wl ON wl.lock_id = ilw.requested_lock_id;
```

MySQL 5.7에서는 경합 상황이 아니면 락이 조회되지 않는 다는 점을 주의하자!! (이거 떄문에 삽질했다..)

해당 쿼리로 DataGrip에서 콘솔 여러개를 켜서 이래저래 시나리오를 돌린 결과 아래의 사실을 찾을 수 있었다.

## 원인

세션 1에서 아래의 쿼리를 수행한다.
```sql
START TRANSACTION;
SELECT * FROM `external_mail`.`pop3_pull_processes`
WHERE `fk_pull_id` = 1111 FOR UPDATE;
```

세션 2에서 아래의 쿼리를 수행한다.
```sql
INSERT INTO `external_mail`.`pop3_pull_processes` (server_name, fk_pop3_pull_list_no, start_date, update_date)
 VALUES('test', 2222, NOW(), NOW());
```

결과는 어떻게 될까..?

정답은 성공할 수도 있고, 실패할 수도 있다. (MySQL에서는 Index를 잠그기 때문)

Index는 `constraint fk_pop3_auto_pull_list_no unique (fk_pull_id)`를 사용하고 있고, SELECT로 조회 시 반드시 해당 인덱스를 탄다.

#### 그런데!

해당 데이터가 있을 때는 아래와 같이 당연히 Index를 탄다.
![img_1.png](img_1.png)

테이블에 데이터가 없을 때는 아래와 같이 이상한 실행 계획을 볼 수 있다.
![img.png](img.png)

해당 부분의 경우 const 조회를 했지만, 데이터가 존재하지 않아서 Key를 사용하지 않았다는 내용이다.

ChatGPT한테 해당 내용을 물어봤었는데 아예 락을 걸지 않았다고 했다. 그런데 그게 아니고 테이블 단위의 갭 락이 걸린거였다.
- 다른 Key 컬럼으로 해서 INSERT를 할 수 없다.

Holy..!

```
참고로 해당 부분은 테이블 안에 다른 데이터가 5~10개만 생겨도 데드락이 발생하지 않는다.
- 이때는 Record 락이 걸리는 것 같다.

즉, 테이블 내에 데이터가 거의 없을 경우에만 발생한다.

(
해당 도메인에서는 process를 DB에 쌓은 다음, 처리 후 데이터를 삭제한다.
그래서 일반적인 경우 데이터가 0건~20건 정도만 왔다갔다한다... 그래서 발생했다 ㅠ
)
```

### 해결 방법

해결 방법은 아래와 같이 강제로 INDEX를 지정하는 것이다.

```php
SELECT * FROM `external_mail`.`pop3_pull_processes`
FORCE INDEX (fk_pop3_auto_pull_list_no)
WHERE `fk_pop3_pull_list_no` = 12421 FOR UPDATE;
```

이상한 점은 실행 계획은 동일하게 보인다.

![img_2.png](img_2.png)

그렇지만 실제로는 동일하지 않았고 해당 부분으로 문제를 해결할 수 있었다.

그래서 어떤 차이가 있는 지 확인하려고 MySQL 8.0을 도커로 띄웠다..! ㅠ

```sql
START TRANSACTION;
EXPLAIN SELECT * FROM `external_mail`.`pop3_pull_processes`
FORCE INDEX (fk_pop3_auto_pull_list_no)
WHERE `fk_pop3_pull_list_no` = 2362 FOR UPDATE;

# 존재한다면
UPDATE `external_mail`.`pop3_pull_processes` SET `update_date` = NOW() WHERE `fk_pop3_pull_list_no` = {}
WHERE

# 존재하지 않는다면
INSERT INTO `external_mail`.`pop3_pull_processes`
(server_name, fk_pop3_pull_list_no, start_date, update_date)
 VALUES('test', 2362, NOW(), NOW());

commit;

rollback;

select * from information_schema.INNODB_LOCKS;
select * from performance_schema.metadata_locks

SELECT * FROM performance_schema.data_locks;

 SELECT
   straight_join
   w.trx_mysql_thread_id waiting_thread,w.trx_id waiting_trx_id,w.trx_query waiting_query,b.trx_mysql_thread_id blocking_thread,
   b.trx_id blocking_trx_id,b.trx_query blocking_query,bl.lock_id blocking_lock_id,bl.lock_mode blocking_lock_mode,
   bl.lock_type blocking_lock_type,bl.lock_table blocking_lock_table,bl.lock_index blocking_lock_index,wl.lock_id waiting_lock_id,
   wl.lock_mode waiting_lock_mode,wl.lock_type waiting_lock_type,wl.lock_table waiting_lock_table,wl.lock_index waiting_lock_index
 FROM information_schema.INNODB_LOCK_WAITS ilw
 INNER JOIN information_schema.INNODB_TRX b ON b.trx_id = ilw.blocking_trx_id
 INNER JOIN information_schema.INNODB_TRX w ON w.trx_id = ilw.requesting_trx_id
 INNER JOIN information_schema.INNODB_LOCKS bl ON bl.lock_id = ilw.blocking_lock_id
 INNER JOIN information_schema.INNODB_LOCKS wl ON wl.lock_id = ilw.requested_lock_id;

SELECT * FROM information_schema.INNODB_LOCKS;
SELECT * FROM performance_schema.metadata_locks;

-- auto-generated definition
create table pop3_pull_processes
(
    no                   int(11) unsigned auto_increment
        primary key,
    server_name          varchar(50) not null,
    fk_pop3_pull_list_no int         not null,
    start_date           datetime    not null,
    update_date          datetime    not null,
    constraint fk_pop3_auto_pull_list_no
        unique (fk_pop3_pull_list_no)
);

create index server_name
    on pop3_pull_processes (server_name);


```


## 참고
- https://kimdubi.github.io/mysql/insert_on_duplicate_lock/
- https://dba.stackexchange.com/questions/257217/why-am-i-getting-a-deadlock-for-a-single-update-query