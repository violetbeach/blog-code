## Network - Proxy와 Gateway의 차이!

**프록시(Proxy)**와 **게이트웨이(Gateway)**는 클라이언트와 목적지 서버 간의 중계하는 역할을 한다는 점에서 매우 유사하다. 

## 프록시

프록시가 있는 서버에서는 아래와 같이 요청이 전달된다.

1. 클라이언트 -> 프록시
2. 프록시 -> 서버

프록시는 중간에서 요청을 받아서 전달하고, 응답도 받아서 클라이언트에 전달해야 하므로 커넥션을 적절히 다룰 수 있어야 한다.

프록시를 사용하는 목적은 아래와 같다.
- 네트워크 캐싱
- 보안점 역할 (방화벽)

나는 무중단 배포를 위해 Reverse Proxy(클라이언트 -> 프록시 서버 -> 내부 서버 구조의 Proxy)를 사용한 적이 있다.
- 참고: https://jojoldu.tistory.com/267


## 게이트웨이(Gateway)

게이트웨이(Gateway)도 프록시랑 아주 유사하다. 특정 네트워크에서 다른 네트워크로 이동하기 위해 거쳐야 하는 필수 지점이다.

게이트웨이는 웹 페이지에서 많은 리소스를 빨리 띄우기 위해 여러 애플리케이션에서 받아오기 위해 사용한다.

1. 어플리케이션 -> 게이트웨이
2. 게이트웨이 -> 리소스

게이트웨이는 **리소스**와 **어플리케이션**을 연결한다. 여기서 2의 과정은 HTTP가 아니라, HTTP 트래픽을 변환해서 다른 프로토콜로 통신한다.

예를 들어 실시간, 스트리밍 동영상의 경우에는 HTTP 프로토콜을 RTP 프로토콜로 변환해서 리소스를 받아온다.

추가로 Proxy 서버는 허용된 네트워크만 통과할 수 있지만, 게이트웨이에서는 필터링을 거치지 않는다.

## 참고
- https://swimjiy.github.io/2020-04-11-web-gateway
- https://codedatasotrage.tistory.com/48






