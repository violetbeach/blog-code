## 코루틴에서 트랜잭션을 사용하는 방법

코루틴에서 트랜잭션을 적용하는 데 어려움을 겪는 케이스가 많다. 실제로 많이 어렵다고 알려져 있다.

Sprign I/O 2024 강연에서도 해당 내용이 나왔는데, 간단하고 오피셜하게 해결할 수 있는 방법을 알아보자.

## Why Problem

JPA의 Transaction에서는 ThreadLocal에 트랜잭션의 커넥션 정보를 저장한다.

이로 인해 여러가지 문제가 발생한다.

#### 1. Transactional 미동작

아래 코드를 봅시다.

```kotlin
@Service
class OrderService(
    private val orderRepository: OrderRepository,
) {
    @Transactional
    suspend fun submit(
        id: Long,
        throwException: Boolean = false,
    ) {
        val order = orderRepository.findById(id).get()
        order.submit()
        orderRepository.save(order)
        if (throwException) {
            throw IllegalStateException("테스트 위한 에러")
        }
    }

    @Transactional
    fun submitNotSuspend(
        id: Long,
        throwException: Boolean = false,
    ) {
        val order = orderRepository.findById(id).get()
        order.submit()
        orderRepository.save(order)
        if (throwException) {
            throw IllegalStateException("테스트 위한 에러")
        }
    }
}
```

throwException 파라미터에 true가 들어오면 예외를 발생할 것이고, 지난 변경사항을 롤백할 것입니다.

아래는 테스트 코드입니다.

```kotlin
@SpringBootTest
class OrderServiceTest {
    @Autowired
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @BeforeEach
    fun setup() {
        orderRepository.deleteAll()
    }

    @Test
    fun `정상 저장 테스트`() {
        // given
        val order =
            orderRepository.save(
                Order(
                    id = 1L,
                    status = OrderStatus.READY,
                ),
            )

        // when
        runBlocking(Dispatchers.IO) {
            orderService.submit(order.id)
        }

        // then
        val result = orderRepository.findById(order.id).get()
        assertThat(result.status).isEqualTo(OrderStatus.SUBMITTED)
    }

    @Test
    fun `롤백 동작 테스트`() {
        // given
        val order =
            orderRepository.save(
                Order(
                    id = 1L,
                    status = OrderStatus.READY,
                ),
            )

        // when
        runCatching {
            runBlocking(Dispatchers.IO) {
                orderService.submit(order.id, true)
            }
        }

        // then
        val result = orderRepository.findById(order.id).get()
        assertThat(result.status).isEqualTo(OrderStatus.READY)
    }
}
```

테스트를 실행해보면 롤백 동작 테스트가 깨진다. 즉, Exception이 발생해도 롤백이 되지 않는다.

![img.png](img.png)

suspend 메서드가 아닌 일반 메서드를 호출하면 테스트가 정상적으로 성공한다.

![img_1.png](img_1.png)

JPA는 코루틴에서의 Transaction을 지원하지 않는다. 코루틴의 Transaction은 MongoDB, R2DBC 등 Reactive 모듈에서만 제공한다.



[//]: # (코루틴은 내부적으로 Suspend 함수를 Continuation이라는 객체 형식으로 바꿔서 AOP가 적용된 프록시 객체를 사용하지 않기 때문에 AOP가 정상적으로 동작할 수 있다.)

#### 2. 원하지 않는 범위의 롤백

문제는 코루틴 쓰레드는 1개의 요청만을 전담해서 처리하지 않는다.

코루틴의 쓰레드는 여러 요청의 Job을 수행하기 때문에 롤백될 때 원하지 않는 것들까지 롤백될 수 있다.





## 참고

- https://www.youtube.com/watch?v=FXHv8ROsc-o
