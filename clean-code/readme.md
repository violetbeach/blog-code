### 깨끗한 클래스

해당 포스팅은 **클래스**에 대해 다룬다. **깨끗한 클래스**란 무엇인가에 대해 알아보자.

### 클래스 체계

깨끗한 클래스를 정의할 때 따르는 표준 자바 관례가 있다.

클래스의 내부 요소를 정의할 때 가장 먼저 변수 목록을 정의한다. 변수 중에서는 가장 먼저 static public 상수를 정의하고 다음으로 static private 변수를 정의한다. 그 다음 private 인스턴스 변수를 정의한다.

변수 목록 다음에 public 메소드를 정의하고 그 다음 private 메소드를 정의한다.

클래스가 내부에서 추상화 단계가 순차적으로 내려가는 관례를 지키면서 신문 기사처럼 읽히는 효과를 가진다.

### 캡슐화

변수와 유틸리티 함수는 가능한 공개하지 않는 것이 좋지만 반드시 숨겨야 한다는 법칙도 없다.

내가 속한 팀도 TS 코드를 짜면서 'JPA 처럼 변경 감지도 없는데 굳이 Getter와 Setter를 만들어서 생산성을 저하시켜야 되나?' 하고 필드를 그대로 사용하는 경우도 많다. 

그럼에도 캡슐화는 아래의 좋은 장점들을 가진다.

- 사용자가 불필요한 접근을 못하게 하며 객체의 오용을 방지할 수 있다.
- 사용자가 내부 동작을 몰라도 된다.
- 객체의 내부가 바뀌어도 인터페이스는 바뀌지 않는다.
- 객체간의 결합도가 낮아진다. (정해진 인터페이스로만 소통한다.)
- 시스템 규모가 크더라도 컴포넌트 별로 작게 분리하여 개발이 가능하다. 이로 인하여 결국에는 시스템 개발 속도를 높이고 성능을 최적화하기 쉽게된다.

그래서 캡슐화는 좋은 시스템 구조를 위해서 가능한 구현하는 것이 바람직하다.

### 클래스의 크기

너무나 당연하지만 클래스는 작아야한다. 코드 길이 뿐만 아니라 맡은 책임을 기준으로 작아야 한다. SOLID의 초두에 오는 것도 단일 책임의 원칙이다.

클래스의 크기가 적당한 지 판별할 때는 **코드의 길이가 아닌 역할을 단위로 판별**한다,

```java
public class SuperDashboard extends JFrame implements MetaDataUser {
    public Component getLastFocusedComponent()
    public void setLastFocused(Component lastFocused)
    public int getMajorVersionNumber()
    public int getMinorVersionNumber()
    public int getBuildNumber() 
}
```

클래스 하나에 메소드 5개 정도면 양호한 것 아닌가? **아니다!** SuperDashbboard는 메서드 수가 작음에도 불구하고 책임이 너무 많다. 이는 클래스 이름만 봐도 파악할 수 있다.

클래스 이름에는 해당 클래스의 책임을 기술해야 한다. 클래스의 크기를 줄이는 첫 번째 방법이 작명을 잘하는 것이다. 가령, Processor, Manager, Super 등과 같이 모호한 단어가 있다면 클래스에다 여러 책임을 떠안겼다는 증거다.

클래스 설명은 if(만약), and(그리고), or(또는), but(하지만)을 사용하지 않고 25단어 이내로 설명이 가능해야 한다. SuperDashboard에 대한 설명을 보자.

> SuperDashboard 클래스는 마지막으로 포커스를 얻었던 컴포넌트에 접근하는 방법을 제공\*\*하며\*\*, 버전과 빌드 번호를 추적하는 매커니즘을 제공한다.

클래스 설명에 **하며(and)**라는 단어가 들어간다. 이는 책임이 너무 많다는 증거다.

### 변경하기 쉬운 클래스

대다수의 시스템은 지속적으로 변경된다. 문제는 변경될 때마다 시스템이 의도대로 동작하지 않을 수 있다. 사이드 이펙트로 다른 메소드의 결과가 달라질 수도 있다.

깨끗한 클래스는 클래스를 체계적으로 정리해서 변경에 수반하는 위험을 낮춘다.

아래는 메타 자료로 적절한 SQL 문자열을 만들어주는 Sql 클래스이다.

```java
public class Sql {
    public Sql(String table, Column[] columns)
    public String create()
    public String insert(Object[] fields)
    public String selectAll()
    public String findByKey(String keyColumn, String keyValue)
    public String select(Column column, String pattern)
    public String select(Criteria criteria)
    public String preparedInsert()
    private String columnList(Column[] columns)
    private String valuesList(Object[] fields, final Column[] columns) 
    private String selectWithCriteria(String criteria)
    private String placeholderList(Column[] columns)
}
```

Sql 클래스는 변경할 이유를 두 가지이므로 SRP(단일 책임 원칙)을 위반한다.

- 새로운 SQL 문을 지원하려면 Sql 클래스를 수정해야 한다.
- 기존 SQL문 하나를 수정할 때도 Sql 클래스를 수정해야 한다.

추가적으로 메서드를 살펴보면 selectWithCrieteria라는 비공개 메서드가 있는데, 이 메서드는 select 문을 처리할 때만 사용한다. **클래스 일부에서만 사용되는 비공개 메서드는 코드를 개선할 여지가 있을 수 있다는 것**을 의미한다.

Sql 클래스에서 파생하는 각각의 클래스로 만들었다. valuesList()와 같은 비공개 메서드는 사용되는 파생 클래스로 옮겼고, 공통으로 사용하는 비공개 메서드는 Where과 ColumnList라는 두 유틸리티 클래스에 넣었다.

```java
public abstract class Sql {
    public Sql(String table, Column[] columns)
    abstract public String generate();
}

public class CreateSql extends Sql {
    public CreateSql(String table, Column[] columns)
    @Override public String generate()
}

public class SelectSql extends Sql {
    public SelectSql(String table, Column[] columns)
    @Override public String generate()
}

public class InsertSql extends Sql {
    public InsertSql(String table, Column[] columns)
    @Override public String generate()
    private String valuesList(Object[] fields, final Column[] columns)
}

public class PreparedInsertSql extends Sql {
    public PreparedInsertSql(String table, Column[] columns)
    @Override public String generate()
    private String placeHolderList(Column[] columns)
}

public class Where {
    public Where(String criteria)
    public String generate()
}
```

분리하고 나니까 아래의 장점이 생겼다.
- 클래스가 단순해서 단번에 이해할 수 있게 되었다.
- 함수 하나를 수정할 때 다른 함수에 대한 결과가 달라질 위험이 사라졌다.
- 테스트가 용이해졌다.
- 새로운 SQL 문을 추가할 때 기존 클래스를 변경할 필요도 사라졌다.

새로운 요구사항이 반영되서 SQL 문이 하나 생기면 그저 Sql 파생 클래스를 바꿔 끼워 넣기만 하면 된다. OCP(개방 폐쇄 원칙)도 만족하게 된 것이다.

새 기능을 수정하거나 기존 기능을 변경할 때 건드릴 코드가 최소인 시스템 구조가 바람직하다. 이상적인 시스템이라면 새 기능을 추가할 때 시스템을 확장할 뿐 기존 코드를 변경하지 않는다.

### 변경으로부터 격리

객체지향 프로그래밍에는 구체 클래스(Concrete class)와 추상 클래스(Abstract class)가 있다. 

상세한 구현에 의존하는 클라이언트 클래스는 구현이 바뀌면 위험에 빠진다. 그래서 인터페이스와 추상 클래스를 사용해 구현이 미치는 영향을 격리한다. 구체 클래스는 상세한 구현(코드)를 포함하며 추상 클래스는 개념만 포함한다.

우리가 메서드를 잘 분리했는지, 클래스 설계가 잘 되어 있는지 지표를 주는 것이 테스트 코드이다 (TDD 원칙). 상세한 구현에 의존하는 코드는 테스트가 어렵다.

Portfolio라는 클래스를 만든다고 가정하자. Portfolio 클래스는 외부 TokyoStockExchangeAPI를 사용해서 포트폴리오 값을 계산한다. 따라서 우리 테스트 코드는 시세 변화에 영향을 받는다. 이 때 테스트 코드를 작성하기란 쉽지 않다.

먼저 porfolio 클래스에서 TokyoStockExchange API를 직접 호출하지 않고 StockExchange라는 인터페이스를 하나 생성하고 구현체로 TokyoStockExchange 클래스를 구현한다.

```java
public interface StockExchange {
    Money currentPrice(String symbol);
}
```

그리고 Porfolio에서 StockExchange를 의존한다.

```java
public class Portfolio {
    private StockExchange exchange;
    public Portfolio(StockExchange exchange) {
        this.exchange = exchange;
    }
    // ... 생략
}
```

이제 TokyoStockExchange의 Mock 클래스를 만들 수 있다. 테스트를 위한 클래스 FixedStockExchangeStub은 StockExchange 인터페이스를 구현하면서 고정된 주가를 반환한다.

```java
public class PortfolioTest {
    private FixedStockExchangeStub exchange;
    private Portfolio portfolio; 
    
    @Before
    protected void setUp() throws Exception {
        exchange = new FixedStockExchangeStub();
        exchange.fix("MSFT", 100);
        portfolio = new Portfolio(exchange);
    }  
    
    @Test
    public void GivenFiveMSFTTotalShouldBe500() throws Exception {
        portfolio.add(5, "MSFT");
        Assert.assertEquals(500, portfolio.value());
    }
}
```

중간에 인터페이스를 하나 두고 인터페이스를 의존하는 DIP(의존 역전의 원칙)을 구현함으로써 테스트용 구현체를 만들 수 있었다.
외부 API에서 책임지는 가격이 계속 변하는 부분은 제외하고 테스트를 할 수 있다.

테스트가 가능한 코드는 시스템의 결합도가 낮아서 유연성과 재사용성이 높아진다. 결합도 낮다는 것은 각 시스템 요소가 다른 요소로부터, 변경으로부터 잘 격리되어 있음을 의미한다.

---

### Reference

- http://amazingguni.github.io/blog/2016/06/Clean-Code-10-클래스