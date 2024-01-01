## Java NIO - busy-wait

자바 NIO에서는 아래의 문제가 있었다.
- main 쓰레드에서 accept가 완료되었는 지 주기적으로 확인 필요
- 각 쓰레드에서 read가 가능한 지 주기적으로 확인 필요
- 채널 상태를 수동으로 관리, 코드 복잡성 증가
- 동시 요청 시 연결 처리가 순차적으로 발생하여 성능이 감소

아래는 코드는 예시이다.

```java
while(true) {
    var clientSocket = serverChannel.accept();
    if(clientSocket == null) {
        Thread.sleep(100);
        continue;
    }
}
```

해당 코드는 루프를 통해 자원을 획득할 때까지 확인을 하는 구조를 가진다.

![img.png](img.png)

해당 문제를 주로 busy-wait 이라고 하고, 주로 동기 Non-Blocking에서 주로 발생한다. busy-wait은 아래 문제를 야기한다.
- 확인을 위해 CPU 자원이 지속적으로 낭비
- 확인하는 주기가 길다면 응답 시간 지연이 발생

이 문제를 해결하기 위한 패턴을 알아보자.

## Selector

Selector를 사용하면 busy-wait 문제를 일부 해결할 수 있다.

**Selector** 클래스는 `java.nio.channels` 패키지에 등록되어 있다.

여러 Channel의 이벤트를 등록하고 준비된 이벤트를 모아서 조회할 수 있는 기능을 제공한다. 

![img_1.png](img_1.png)

준비가 완료된 Event는 Thread에 전달한다.

`SelectableChannel`은 `regeister()`로 Selector와 `ops`라는 관심있는 이벤트 목록 등록할 수 있다.

![img_2.png](img_2.png)

ops의 종류는 다음과 같다.
- OP_READ: channel의 읽기 준비가 완료
- OP_WRITE: channel에 쓸 준비가 완료
- OP_ACCEPT: serverSocketChannel에서 accept 준비가 완료
- OP_CONNECT: socketChannel에서 connect 준비가 완료

Selector를 사용해서 대기하는 로직은 아래와 같이 작성할 수 있다.

```java
while(true) {
    selector.select();
    
    var selectedKeys = selector.selectedKeys().iterator();
    
    while(selectedKeys.hasNext()) {
        var key = selectedKeys.next();
        // 작업 처리
        selectedKeys.remove();
    }
}
```

결과적으로 여러 개의 채널을 하나의 Selector로 통합할 수 있게 된다.

이를 I/O Multiplexing이라고 표현한다.


#### 기존 코드

아래는 Selector를 활용하지 않은 코드이다.

```java
public class Main {
    
    public static void main(String[] args) {
        try (ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
            serverSocket.bind(new InetSocketAddress("localhost", 8080));
            serverSocket.configureBlocking(false);

            while (true) {
                SocketChannel clientSocket = serverSocket.accept();
                if (clientSocket == null) {
                    Thread.sleep(100);
                    continue;
                }

                String requestBody = handleRequest(clientSocket);
                sendResponse(clientSocket, requestBody);
            }
        }
    }
    
    private String handleRequest(SocketChannel clientSocket) {
        // busy-wait
        while (clientSocket.read(requestByteBuffer) == 0) {}
        
        ByteBuffer requestByteBuffer = ByteBuffer.allocateDirect(1024);
        
        requestByteBuffer.flip();
        return StandardCharsets.UTF_8.decode(requestByteBuffer).toString();
    }

    private static void sendResponse(SocketChannel clientSocket, String requestBody) {
        // 외부 API 지연을 가정
        Thread.sleep(50);
        
        String content = "received: " + requestBody;
        ByteBuffer responeByteBuffer = ByteBuffer.wrap(content.getBytes());
        clientSocket.write(responeByteBuffer);
        clientSocket.close();
    }
}
```

`handleRequest()`에 있던 `clientSocket.read()`에서 반복문을 돌면서 체크를 했어야 했다.

#### 수정 코드

```java
public class Main {
    
    public static void main(String[] args) {
        try (ServerSocketChannel serverSocket = ServerSocketChannel.open();
             Selector selector = Selector.open();
        ) {
            serverSocket.bind(new InetSocketAddress("localhost", 8080));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();
                Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();

                while (selectedKeys.hasNext()) {
                    SelectionKey key = selectedKeys.next();
                    selectedKeys.remove();

                    if (key.isAcceptable()) {
                        SocketChannel clientSocket = ((ServerSocketChannel)key.channel()).accept();
                        clientSocket.configureBlocking(false);
                        clientSocket.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        SocketChannel clientSocket = (SocketChannel) key.channel();

                        String requestBody = handleRequest(clientSocket);
                        sendResponse(clientSocket, requestBody);
                    }
                }
            }
        }
    }
    
    private static String handleRequest(SocketChannel clientSocket) {
        ByteBuffer requestByteBuffer = ByteBuffer.allocateDirect(1024);
        clientSocket.read(requestByteBuffer);

        requestByteBuffer.flip();
        String requestBody = StandardCharsets.UTF_8.decode(requestByteBuffer).toString();
        return requestBody;
    }
    
    private static void sendResponse(SocketChannel clientSocket, String requestBody) {
        // 외부 API 지연을 가정
        Thread.sleep(50);

        String content = "received: " + requestBody;
        ByteBuffer responeByteBuffer = ByteBuffer.wrap(content.getBytes());
        clientSocket.write(responeByteBuffer);
        clientSocket.close();
    }
}
```

수정된 코드에서는 `handleRequest()`에서 while 문을 사용하지 않고, 이미 이벤트를 받은 상황이기 때문에 clientSocket이 null이 아닌 것을 보장할 수 있게 된다.

결과적으로 각 NIO 작업에서 busy-wait을 하지 않아도 된다.





