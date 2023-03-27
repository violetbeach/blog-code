## ON DUPLICATE KEY UPDATE로 인한 데드락 해결

## 문제 쿼리

```mysql
INSERT INTO `external_mail`.`pop3_pull_processes` (server_name, fk_pop3_pull_list_no, start_date, update_date)
    VALUES(?, ?, NOW(), NOW()) ON DUPLICATE KEY UPDATE update_date = NOW();
```

해당 쿼리를 실행하면서 아래의 예외가 터졌다.

```
Query error: Deadlock found when trying to get lock; try restarting transaction - Invalid query: INSERT INTO `external_mail`.`pop3_pull_processes` (server_name, fk_pop3_pull_list_no, start_date, update_date)
VALUES(?, ?, NOW(), NOW()) ON DUPLICATE KEY UPDATE update_date = NOW();
```

해당 에러가 터진 원인은 뭘까?

## 에러 원인

ON DUPLICATE KEY UPDATE문은 Insert문을 수행하되 중복된 Key가 존재하면 Update문을 실행하는 쿼리이다.

트랜잭션도 없는 데 어떻게 데드락이 발생할까? 하는 의문도 있어서 찾아보니까, MySQL을 비롯한 다양한 DB에서 내부적으로 트랜잭션을 관리한다고 한다.


## Index

```mysql
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