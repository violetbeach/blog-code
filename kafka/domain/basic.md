## Job

배치 계층에서 가장 상위에 있는 개념으로 하나의 작업 자체를 의미한다.

Job Configuration을 통해 생성되고, 하나의 작업을 어떻게 구성하고 실행할 것인지 전체적으로 명세한다.

여러 개의 Step으로 구성되기 때문에 최소한 1개 이상의 Step을 구성해야 한다.

Job의 경우 빈으로 등록한 후 수동으로 실행시킬 수도 있고, SpringBoot의 BatchAutoConfiguration에서 기본으로 빈으로 등록한 Job을 수행한다.

#### Job 구현체

SimpleJob
- 순차적으로 Step을 실행시키는 Job
- 모든 Job에서 유용하게 사용할 수 있는 표준 기능을 가지고 있다.

FlowJob
- 특정한 조건과 흐름에 따라 Step을 구성하여 실행시키는 Job
- Flow 객체를 실행시켜서 작업을 진행한다.

