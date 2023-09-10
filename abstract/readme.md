## 추상화

해당 강연에서는 아래의 상황을 가정한다.
- 원화/달러 입출금을 관리한느 시스템을 구축해야 한다.

요구하는 기능
- 원화 입금
- 원화 출금
- 달러 입금
- 달러 출금

### 추상화

여기서 아래와 같이 추상화할 수 있다.

```kotlin
interface TrxTypeService {
    fun create(account: String, amount: Long)
    fun accept(id: Long)
}
```

달러 입출금은 미국지사에서 트리거를 해야되는 상황이라고 가정해보자.

인터페이스의 `accept`가 필요가 없어지고 결국 코드를 변경해야 하는 상황이 된다.

## 축

추상화할 때 주의할 점은 **인터페이스가 변경되는 것**이다.

위 인터페이스에서는 **타입**과 **행위**가 있다.
- 타입: 원화, 달러
- 행위: 입금, 출

여기서 **타입**이 확장되는 것에는 위 추상화가 유리하다. 그런데 **행위**가 확장된 경우에는 유리하지 않았다.

소프트웨어가 타입과 행위뿐 아니라 훨씬 더 많은 축 중 어떤 방향으로 확장될 지 예측하기 어렵다.

## 추상화 범위

훨씬 더 작은 범위를 추상화했다면 아래의 모습이 된다.

```kotlin
interface TrxRequestProcessor {
    fun process(account: String, amount: BigDecimal)
}

class TrxRequestService {
    fun accept(id: Long, processor: TrxRequestProcessor) {
        val trxRequest = getTrxRequest(id)
        trixRequest.accept()
        processor.process(trxRequest.account, trxRequest.amount)
    }
}
```

여기서 미국 지사의 경우 승인 기능이 필요 없다면 아래와 같이 코드가 변경될 수 있다.

```kotlin
class TrxRequestService {
    fun accept(id: Long, processor: TrxRequestProcessor) {
        val trxRequest = getTrxRequest(id)
        trixRequest.accept()
        
        if(trxRequest.type.isUsed()) {
            return
        }
        
        processor.process(trxRequest.account, trxRequest.amount)
    }
}
```

즉, 요구사항을 수용할 수 있게 되었다. 너무 넓은 범위를 추상화할 경우 인터페이스가 변경되는 위험이 커진다.

## 요약

- 추상화는 비용이 있고 잘못된 추상화는 혜택없이 비용만 감당될 수 있다.
- 추상화는 충분히 코드가 생겼고, 도메인에 대한 이해가 생긴 이후에 하는 것이 좋을 수 있다.
  - 먼저 함수를 추출하는 것부터 시작해서 점진적으로 추상화하는 것이 좋을 수 있다.
- 추상화는 방향, 범위, 시기 세 가지를 모두 고려해야 한다.
