# JUnit5 - TestEntityManager를 활용한 Repository 테스트

Repository 테스트를 작성할 때 결과를 검증하기 위해서 어떤 도구를 사용해야 할까?

일반적으로 Repository.save() 기능을 테스트한다고 하면, Repository.find_을 사용해서 검증하기 쉽다.

하지만 이는 적절치 않다.

가령, userRepository를 테스트하는 코드에서 userRepository를 사용하면 First 원칙 중 I(Isolated) 고립성이 깨지게 된다.

그래서 나는 JdbcTemplate을 사용했다.

## 기존의 코드
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountRepositoryTest {
    
    private AccountRepository accountRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void findByLoginId() {
        // given
        String loginId = "violetbeach13";
        jdbcTemplate.update("INSERT INTO account (login_id, password, name, status, allow_to_marketing_notification) VALUES" +
                "(?, '$2a$10$JtZ/Qb4VjL1KIvLMgNFKGOSWU.4LSFDpJZkqYOB4tM2A/wg1N/Vse', 'member', 'ACTIVE', false)", loginId);

        // when
        Optional<Account> account = accountRepository.findByLoginId(loginId);

        // then
        assertThat(account).isPresent();
    }
    
    @Test
    void save() {
        // given
        String loginId = "violetbeach13";
        Account account = new Account(loginId,
                "password1234",
                "nickname12",
                "01012345678",
                AccountStatus.ACTIVE,
                null,
                false
                );

        // when
        accountRepository.save(account);

        // then
        SqlRowSet rsAccount = jdbcTemplate.queryForRowSet(
                "select * from account where id = ?",
                account.getId());
        assertThat(rsAccount.next()).isTrue();
        assertThat(rsAccount.getString("login_id")).isEqualTo(loginId);
    }
}
```
해당 코드는 AccountRepository에 대해 독립적으로 테스트를 수행할 수 있다.

반면 단점이 있는데, 첫 번째는 Native SQL을 사용해야 한다는 점이다. DBMS 구조가 변경되면 해당 코드를 반드시 수정해야 한다. 두 번째로 읽기가 어렵다. 우리는 순수한 자바코드를 원한다. 복잡한 SQL을 자바 코드와 함께 읽는 것은 쉬운 것이 아니다.

아래에서 수정된 코드를 보자.

## 수정된 코드 (with TestEntityManager)
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountRepositoryTest {
    
    private AccountRepository accountRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void findByLoginId() {
        // given
        Account newAccount = new Account("violetBeach13", "password13", "honey", "01012345678", AccountStatus.ACTIVE, null, false);
        em.persistAndFlush(newAccount);

        // when
        Optional<Account> account = accountRepository.findByLoginId(newAccount.getLoginId());

        // then
        assertThat(account).isPresent();
    }
    
    @Test
    void save() {
        // given
        String loginId = "violetbeach13";
        Account account = new Account(loginId,
                "password1234",
                "nickname12",
                "01012345678",
                AccountStatus.ACTIVE,
                null,
                false
                );

        // when
        accountRepository.save(account);
        em.flush();

        // then
        Account result = em.find(Account.class, account.getId());
        assertThat(result).isNotNull();
    }
}
```
@DataJpaTest 애노테이션을 사용하면 TestEntityManager를 제공한다.

TestEntityManager 인스턴스를 주입받아서 테스트 데이터를 생성, 조회, 삭제 기능을 사용하면 JdbcTemplate 없이도 Repository 테스트를 작성할 수 있다.

실제로 보기가 더 깔끔해졌다.