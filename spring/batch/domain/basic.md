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

## JobInstance

Job이 실행될 때 생성되는 job의 논리적 실행 단위 객체로서 고유하게 식별 가능한 작업을 실행한다.

하루에 한번씩 배치 Job이 실행된다면 매일 실행되는 각각의 Job을 JobInstance라고 표현한다.
- 처음 시작하는 Job + JobParameter일 경우 새로운 JobInstance를 생성
- 이전과 동일한 Job + JobParameter로 실행할 경우 이미 존재하는 JobInstance 리턴
  - 내부적으로 JobName + JobKey(jobParameters의 해시값)을 가지고 JobInstance 객체를 얻는다.
- Job과 1:M 관계

JobInstance는 JobRepository에 의해 BATCH_JOB_INSTANCE 테이블에 저장된다.

### JobRepository
JobLauncher로 Job을 실행시킬 때 아래의 코드를 구성할 수 있다.
```java
@Component
@RequiredArgsConstructor
public class JobRunner implements ApplicationRunner {

    private final JobLauncher jobLauncher;
    private final Job job;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("userId", "violetbeach")
                .toJobParameters();
        jobLauncher.run(job, jobParameters);
    }

}
```
![img.png](Documents/blog-code/spring/batch/domain/img.png)

내부적으로 JobRepository가 현재 실행중인 Job이 처음 실행하는 것인지, 이미 실행된 (존재하는) JobInstance인지 확인한다.

존재 여부에 따라 기존의 JobInstance를 반환하거나 새로운 JobInstance를 생성한다.
- 기존의 JobInstance가 완료가 된 상태라면 예외를 발생한다.
