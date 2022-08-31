# Java - Comparable을 잘 구현하는 방법!

## Comparable

Java로 알고리즘 공부를 하는 사람이라면 Comparable을 매우 자주 사용하게 된다.

Comparable을 구현하면 인스턴스들의 순서를 손쉽게 정할 수 있다.

문제는 Comparable을 구현하는 방법을 잊어먹는 경우가 많다.

이는 Comporable 구현이 어렵기 때문이다.

## Comparable
일반적으로 Comparable을 사용하는 예시를 살펴보자.
```java
class Point implements Comparable<Point> {
    int x, y, z;

    @Override
    public int compareTo(Point p) {
        if(this.x > p.x) {
            return 1;
        }
        else if(this.x == p.x) {
            if(this.y > p.y) {
                return 1;
            } else if(this.y == p.y) {
                if(this.z > p.z) {
                    return 1;
                } else if (this.z == p.z) {
                    return 0;
                }
            }
        }
        return -1;
    }
}
```
대충 봐도 복잡하다. 해당 방법은 x, y, z 순으로 오름차순 정렬을 하는 결과를 가진다.

그럼 다음의 코드와 비교해보자.
```java
class Point implements Comparable<Point> {
    int x, y, z;

    @Override
    public int compareTo(Point p) {
        int result = Integer.compare(x, p.x);
        if (result == 0) {
            result = Integer.compare(y, p.y);
            if (result == 0) {
                result = Integer.compare(z, p.z);
            }
        }
        return result;
    }
}
```
위 방법은 훨씬 간단하고 실수의 여지가 적다.

compareTo 메서드에서 관계 연산자 `<`, `>`를 사용하는 방식은 거추장스럽고 오류를 유발할 수 있다.

Java 7부터 Wrapper 클래스에 추가된 정적 메서드 compare를 이용하면 쉽게 가능하다.

## Comparator
Comparator를 사용하는 방법도 있다. Comparator는 메서드가 하나 뿐인 함수형 인터페이스를 구현하기 때문에 람다 함수로 대체가 가능하다.
```java
Collection.sort(points, (a, b) -> {
    int result = Integer.compare(a.x, b.x);
    if (result == 0) {
        result = Integer.compare(a.y, b.y);
        if(result == 0) {
            result = Integer.compare(a.z, b.z);
        }
    }
    return result;
});
```


## 추가 고려 사항

compareTo를 정의할 때 오름차순과 내림차순에 대한 구현 방법을 헷갈릴 수 있다.

- 양수를 반환하면 해당 인스턴스가 순서의 뒤쪽으로 가는 것을 의미한다.
- 음수를 반환하면 해당 인스턴스가 순서의 앞쪽으로 오는 것을 의미한다.

(결과가 크면 양수인 1이 반환되는 compare 메서드를 생각하면 된다.)


## 참고
- https://www.daleseo.com/java-comparable-comparator/