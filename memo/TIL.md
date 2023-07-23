## Java와 C, C++과의 차이

|         | Java               | C, C++              |
|---------|--------------------|---------------------|
| 실행환경    | 컴파일 + jar 압축       | 컴파일, 링크             |
| 클래스     | 객체지향(OOP)          | 절차지향 / 절차지향이 섞여있음   |
| 컴파일     | JVM으로 운영체제에 비종속    | 각 운영체제에 맞는 기계어로 컴파일 |
| 가비지 컬렉터 | 가비지 컬렉션으로 개발에 더 집중 | 직접 메모리 정리 필요        |
| 효율      | 파일 크기가 크고 비교적 느림   | 파일 크기가 작교 효율적       |
| 보안      | 내장 보안 프레임워크 덕분에 튼튼 | 비교적 취약              |

## 자바는 왜 느릴까..?

#### 1. OOP

- 클래스 단위로 코드를 작성
- 다른 클래스에 있는 메서드나 정보를 활용하기 위해서는 해당 클래스를 **인스턴스화** 해야 함

이를 피하기 위해서는 static 사용하면되지만, 아래의 단점이 존재
- 상태 추론이 어려움
- 각 클래스의 독립성을 추구하는 OOP 이념에서 벗어남
- 캡슐화 이점 사용이 불가능함
- 프로그램 종료시까지 메모리를 점유하므로 메모리가 낭비될 수 있다.

#### 2. 인터프리터 방식

자바 인터프리터로 인해 필요한 코드를 필요한 시점에 메모리에 올려서 사용한다.

JIT 컴파일러로 임계치를 기준으로 캐싱하지만, 그래도 빌드가 비교적 느리다.

#### 3. JVM

JVM이 OS나 기타 환경에 맞춰서 유동적으로 기계어로 번역한 뒤 실행해주기 때문
- 그만큼 연산이나 메모리 사용이 많아서 상대적 느림

#### 4. 가비지 컬렉터

가비지 컬렉터가 계속 힙 메모리를 확인하는 등의 처리가 필요하다.
- 가비지 컬렉터도 프로그램이기 때문에 메모리와 연산 작업을 동반한다.

## JVM 힙 메모리는?

JVM의 힙 메모리는 Java 프로그램 동작 중 생성되는 객체들이 저장되는 공간이다. 이러한 객체들은 런타임 중에 메모리에 동적으로 할당 및 해제된다.

JVM의 힙 메모리를 나누는 기준은 GC 알고리즘에 따라 다르지만,
통상적으로 Eden 영역, Survivor1, Survivor2, Old 영역 순으로 더 오래 사용되는 객체가 저장된다.
보통 순차적으로 Eden, Survivor1, Survivor2 이렇게 순차적으로 이동하고, G1GC 부터는 한번에 여러 단계 이동도 가능한 것 같다.

원래는 클래스 로더가 동적으로 읽는 클래스나 메소드의 메타데이터나 static 상수를 저장하는 PermGen영역도 Heap에 있었는데
지금.. jdk8부터는 Metaspace 영역으로 이름이 바뀌고 CHeap, Thread Stack이랑 같이 NativeMemory영역에 있고,
대신 static 클래스나 상수는 Heap에 저장합니다.
- Metaspace가 Native Memory를 사용하면서 전체 서버를 다운 시킬 수 있으므로 적절한 제한이 필요하다.

## 클래스, 인터페이스, 열거형 차이

### 1. 클래스

클래스는 자바에서 객체의 설계도와 같다.

객체를 생성하는데 사용되며, 해당 클래스로부터 만들어지는 객체들은 클래스의 속성과 동작을 공유한다.

클래스는 다른 클래스들로부터 상속을 받을 수 있으며, 이를 통해 코드 재사용과 계층적 구조를 구성할 수 있다.

### 2. 인터페이스

인터페이스는 일종의 계약(contract)으로, 클래스들이 특정한 메서드들을 구현하도록 강제하는 역할을 한다.

인터페이스를 통해 다형성을 구현할 수 있고,

DIP를 달성하거나,

default method를 사용해서 믹스드인을 구현할 수도 있는 것 같습니다.

### 3. 열거형

열거형은 서로 연관된 상수들의 집합이다. 열거형을 사용하면 코드가 더 읽기 쉽고, 오직 정해진 상수들만 사용할 수 있으므로 안전한 도메인, 안전한 코드를 작성할 수 있다.

## GC 튜닝

JVM 튜닝에 앞서 고려해야 할 점이 **성능** 중 어떤 부분을 우선할 것인가를 정해야 한다.

일반적으로 **성능**이란 **TPS**와 **Latency**로 나뉜다.

#### 1. GC 알고리즘

GC 알고리즘을 예로 들면 처리량(TPS)이 목적이면 Parallel GC, 응답 시간이 목적이면 CMS GC, G1 GC가 유리하다.
- Parallel GC는 Full GC를 수행하면서 GC 소요 시간이 길지만 빈도가 적어서 총 STW 시간이 적다. 그래서 처리량이 높다.
- CMS, G1 GC의 경우 GC를 짧지만 빈번하게 수행하므로 처리량이 낮지만 응답시간이 빠르다.

추가로 CPU Core의 코어가 싱글이라면 Serial GC를 사용할 수 있고, CPU Core가 멀티 코어이고 Ram이 4GB 이상이라면 한 번에 여러개의 객체를 처리할 수 있는 G1 GC가 유리할 수 있다.

- -XX:+UseConcMarkSweepGC
  - CMS GC 사용
- -XX:+UseG1GC
  - G1 GC 사용
- ...


#### 2. Heap 사이즈

- 메모리 크기가 크다면 GC 빈도는 감소하지만, GC 수행 시간이 증가한다. (처리량 증가, 응답시간 저하)
- 메모리 크기가 적다면 GC 빈도는 증가하지만, GC 수행 시간이 짧아진다. (처리량 감소, 응답시간 향상)

추가로 Heap 영역 뿐만 아니라 Heap 내부의 세부 영역도 조절할 수 있다.

New(Young Gen) 영역을 위한 옵션
- -XX:NewSize=512m
  - New 영역 크기 설정
- -XX:MaxNewSize=100m
  - New 영역 최대 크기 설정
- -XX:NewRatio=2
  - 전체 Heap 크기 중 New 영역과 Old 영역의 비율 (위에서는 1:2 라는 뜻)

Young Gen 영역의 크기가 작다면 GC 발생횟수 및 수행시간이 증가된다.

마찬가지로 Survivor나 Old Gen 영역도 설정할 수 있다.

#### 3. MetaSpace 사이즈

Metaspace가 Native memory 영역으로 들어가면서 동일 서버 내 다른 애플리케이션에 영향을 줄 수 있어졌다.

그래서 아래의 크기를 통해 Metaspace를 조절하게 된다.

- -XX:MetaspaceSize
  - Metaspace 크기
- -XX:MaxMetaspaceSize
  - Metaspace 최대 크기

#### 4. Mode

JVM에는 두 개의 타입이 있다. **server** / **client**
- -server
  - 최초 실행이 오래 걸리지만, 런타임 중에 더 공격적으로 코드를 최적화한다.
- -client
  - 최초 실행이 짧게 걸리지만, 런타임 중 효율이 비교적 좋지 않다.

#### 5. GC 세부 튜닝

GC Logging
- -verbose:gc
  - GC 로그 수행
- -Xloggc:gc.log
  - GC 로그를 파일로 남긴다.
- -XX:+PrintGCDetails
  - GC 수행 시 더 자세한 정보를 출력
- -XX:+PrintGCTimeStamps
  - GC 발생한 시간을 출력

GC 소요시간 최소화 옵션
- -XX:+UseParNewGC
  - Young GC를 Multi Thread 방식으로 실행
- -XX:+CMSParallelRemarkEnabled
  - Young Gen에서 발생하는 Remark 단계를 병렬로 진행한다.
- -XX:+CMSInitiatingOccupancyFraction=80
  - Old Gen이 몇 % 찼을 때 Old GC를 수행할 지 설정
- -XX:MaxTenuringThreshold=20
  - Old Gen 영역으로 이동하지 않고 머무를 최대 반복 횟수 (Full GC 빈도를 줄이기 위함)

해당 값의 경우 각 GC의 특성에 맞는 추천 값들이 있다. 예를 들어 아래 사이트는 G1 GC의 JVM 튜닝 Best Practice를 정리했다.
- https://backstage.forgerock.com/knowledge/kb/article/a75965340
- 
#### 6. OOM 힙 덤프
- -XX:+HeapDumpOnOutOfmemoryError
  - OutOfMemoryError 발생시 자동으로 힙덤프 생성 옵션
- -XX:HeapDumpPath=/path
  - 힙 덤프가 저장될 경로
  
## 제너릭 vs Supplier

Supplier는 파라미터가 0개고 제너릭 타입을 반환하는 함수형 인터페이스이다.

제너릭이 아니라 Supplier로 넘겨준다는 것은 사용자가 동작을 커스터마이징 할 수 있다는 것을 의미한다. 즉, 데이터를 공급받는 로직을 사용자에게 위임할 수 있다.

## SOF 발생 원인
- 재귀 호출의 깊이 초과
- 지역 변수의 과다 사용
- 크기가 큰 배열 선언
- 존재하지 않는 인덱스나 주소에 대한 레퍼런스

## 재귀 vs 꼬리재귀

다시 현재의 함수 흐름으로 돌아올 필요가 없는 것
- 컴파일러에서 최적화를 해준다.

재귀
```javascript
function factorial(n) {
    if (n === 1) {
        return 1;
    }
    return n * factorial(n-1);
}
```

꼬리재귀
```javascript
function factorial(n, total = 1){
    if(n === 1){
        return total;
    }
    return factorial(n - 1, n * total);
}
```

## 만약에 List의 끝부분에서만 삽입/삭제가 일어난다면 삽입에 어떤 것이 더 유리할까?

ArrayList의 경우 내부적으로 배열로 구현되어 있다.

LinkedList의 경우 삽입 및 삭제에서 O(1)만 소요된다. 그래서 Stack의 경우 LinkedList로 구현된 것 같다.

## GET vs POST

GET
- GET 요청은 캐시가 가능
  - HTTP 헤더에서 cache-control 헤더를 통해 캐시 옵션을 지정할 수 있다.
- GET 요청은 브라우저 히스토리에 남는다.
- GET 요청은 길이 제한이 있다.
  - GET 요청의 길이 제한은 표준이 따로 있는건 아니고 브라우저마다 제한이 다르다고 한다.
- GET 요청은 보안이 좋지 않다.

POST
- POST 요청은 캐시되지 않는다.
- POST 요청은 브라우저 히스토리에 남지 않는다.
- POST 요청은 데이터 길이에 제한이 없다.