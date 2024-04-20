## MongoDB driver

MongoDB 회사 에서 공식적으로 2가지 드라이버를 제공한다.
- Java Driver (Sync)
- Reactive Streams Driver (Async)

#### Java driver (Sync)

Java Driver는 동기로 동작하는 애플리케이션을 위한 드라이버이다.

![img.png](img.png)

클라이언트는 요청을 보내면 응답이 돌아오기 전까지 쓰레드가 Blocking된다.

코드가 직관적이고 작성 난이도가 낮지만, 처리량이 비교적 높지 않다.

## Reactive Streams Driver

Reactive Streams driver는 비동기로 동작하는 애플리케이션을 위한 MongoDB 드라이버이다.

![img_1.png](img_1.png)

해당 드라이버에서는 먼저 쿼리가 잘 전달되었다는 응답을 받는다. 실제 결과 응답은 이후 시점에서 받게 된다.