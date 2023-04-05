## Java - Metaspace 영역 사이즈 조절하기!

JDK 8부터는 Java Heap에서 PermGen이라는 영역이 Metaspace라는 영역으로 변경되어 Native Memory로 이동했다.
- 참고: https://jaehoney.tistory.com/177

개발 중에 `OutOfMemoryError: Metaspace` 문제에 직면한 적이 있다.

이는 왜 발생하는 것일까..?

## Metaspace

Metaspace는 클래스의 메타데이터를 담는 공간이다.
- 클래스명
- static 필드
- ...

즉, 클래스를 많이 사용할 수록 Metaspace를 많이 사용하게 된다.

해당 영역은 JDK나 OS 버전 별로 기본값이 다르다.

클래스가 많아지거나 동적으로 클래스를 생성하면 OOM이 발생하는 것이다.
- 일반적으로 어디선가 **클래스를 동적으로 만들고 있고, 정리가 안되고 있는 것**이다.
- 근본적으로 원인이 되는 코드를 찾아서 해결해야 한다.

해당 부분을 해결하지 않으면, 서버가 죽기 때문에 반드시 해결해야 한다.
- 기본 값으로 제한된 크기를 가지고 있지 않기 때문
- k8s를 사용하고 있다면, 동일 컨테이너의 메모리를 최대한 빌려와서 다른 Pod에 영향을 줄 수 있다.

## 최대값 설정

그때 필요한 것이 아래와 같이 최대값을 설정해야 한다.

`-XX:MetaspaceSize=N`

이후에는 Metaspace 영역이 차오르면 GC를 수행하게 된다. 추가로 

