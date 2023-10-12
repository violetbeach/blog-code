아래는 이펙티브 자바의 내용 중 **동시성**에 대한 부분을 정리한 것이다.

Effective Java는 동시성을 사용할 때의 몇가지 주의사항과 가이드라인을 제시한다.

## 동기화 메서드 작성

**동기화된 메서드**를 작성할 때 중요한 것은 **재정의할 수 있는 메서드**를 호출해선 안되고 클라이언트가 넘겨준 **함수 객체**도 사용하면 안된다는 것이다.

```java
public class ObservableSet<E> extends ForwardingSet<E> {

    public void addObserver(SetObserver<E> observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    public boolean removeObserver(SetObserver<E> observer) {
        synchronized (observers) {
            return observers.remove(observer);
        }
    }

    private void notifyElementAdded(E element) {
        synchronized (observers) {
            for(SetObserver<E> observer : observers) {
                observer.added(this, element);
            }
        }
    }

    @Override
    public boolean add(E element) {
        boolean added = super.add(element);
        if(added) {
            notifyElementAdded(element);
        }
        return added;
    }
    
}
```

해당 코드를 보면 `add()` 내부적으로 `notifyElementAdded()` 메서드를 호출하고 있다.

여기서 `add()` 입장에서 보면 `notifyElementAdd()`는 바깥 세상에서 온 외계인 영역이다. 그 메서드가 무슨 일을 할 지도 모르고 통제할 방법도 없다.

외부 메서드는 동기화된 영역의 일관성을 해치거나 교착상태에 빠지게할 수 있다.

```java
public static void main(String[] args) {
	ObservableSet<Integer> set = new ObservableSet<>(New HashSet<>());
	
	set.addObserver(new SetObserver<Integer>() {
		public void added(ObservableSet<Integer> s, Integer e) {
			System.out.println(e);
			if (e == 23) s.removeObserver(this);
		}
	});
}
```

가령 위 함수를 실행할 경우 `ConcurrentModificationException`이 발생한다. `added()`를 호출한 시점이 `notifyElementAdded`가 `observers`를 순회하고 있는 지점이기 때문이다.

## TODO

Tab to 4 indent