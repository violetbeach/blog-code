![img.png](images/img.png)

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

GC 알고리즘 관련해서는 이전 포스팅에서 정리했으니 참고하자.
- 참고: https://jaehoney.tistory.com/301

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

일회성으로 실행하는 코드라면 **client** 모드가 적절하고, 운영 중에 계속 서버를 유지할 목적이라면 **server** 모드가 적절할 수 있다.

#### 5. GC 관련 세부 튜닝

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

GC 알고리즘을 선택했지만 세부 옵션들이 남았다.

- -XX:+UseParNewGC
    - Young GC를 Multi Thread 방식으로 실행
- -XX:+CMSParallelRemarkEnabled
    - Young Gen에서 발생하는 Remark를 병렬로 진행한다.
- -XX:+CMSInitiatingOccupancyFraction=80
    - Old Gen이 몇 % 찼을 때 Old GC를 수행할 지 설정
- -XX:MaxTenuringThreshold=20
    - Old Gen 영역으로 이동하지 않고 머무를 최대 반복 횟수 (Full GC 빈도를 줄이기 위함)

각 GC의 특성에 맞는 추천 값들이 있다. 예를 들어 아래 사이트는 G1 GC의 JVM 튜닝 Best Practice를 정리했다.
- https://backstage.forgerock.com/knowledge/kb/article/a75965340

#### 6. OOM 힙 덤프
- -XX:+HeapDumpOnOutOfmemoryError
    - OutOfMemoryError 발생시 자동으로 힙덤프 생성 옵션
- -XX:HeapDumpPath=/path
    - 힙 덤프가 저장될 경로

이외에도 DNS 캐시 옵션, JIT 컴파일러 옵션 등이 있는데 해당 부분의 경우 Oracle 공식 문서를 참고하자.
- https://www.oracle.com/java/technologies/javase/vmoptions-jsp.html

## 참고
- https://docs.oracle.com/cd/E19957-01/820-0524/abzdr/index.html
- https://sharplee7.tistory.com/61
- https://nesoy.github.io/articles/2019-08/JVM-Options
