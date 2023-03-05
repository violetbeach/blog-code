## SpringBoot - zip 압축 및 다운 로드 구현하기!

Spring 프로젝트에서 zip 다운로드를 구현한 내용을 공유한다.

요구사항은 아래와 같다.

![img_1.png](img_1.png)

사용자로 부터 GET 요청을 받으면 파일 시스템 내의 특정 폴더의 파일들을 zip 형태로 내려준다.

## Content-Type / Accept

API를 설계하면서 Content-Type과 수용 가능한 Accept 설계가 필요하다.

아래의 두 가지 중 고민이 있었다.

- application/zip
- application/octet-stream

아래 부분 참고했을 때 application/zip이 MIME 표준으로 적합한 솔루션이라고 한다.
- [참고 - stackoverflow](https://stackoverflow.com/questions/4411757/zip-mime-types-when-to-pick-which-one)

추가로 아래의 이유로 application/zip을 선택했습니다.
- zip 이외의 다운로드가 추가될 수 있음 (end-point: /mails/:mailNo/attachments)
- 타 레퍼런스 참조 (메일 솔루션)
    - Naver mail는 application/zip
    - Gmail은 application/x-zip-compressed

하지만 (Java의 MediaType에서는 `application/zip`을 지원하지 않음)
- static 변수 처리

## Zip 로직 위치

Zip으로 만드는 기능을 표현(Presentation) 계층에 둘 지 응용 서비스(Application) 계층에 둘 지 고민이 있었다.

해당 부분의 경우 Controller(Presentation)에서 담당하는 것이 적합하다고 생각했으며, 나중에 보겠지만 Response를 활용해서 바로 Zip을 스트리밍 형식으로 전달하기 때문에 Presentation이 더 적합했다.

추가로 Zip으로 만들어서 응답을 내려주는 것은 Controller에서 하지만, Zip으로 만드는 것 자체는 재활용이 가능하도록 별도의 모듈 형태로 분리하는 것이 좋을 것 같다.

나머지는 차차 언급하기로 하고 구현에 대해 살펴보자.

## 구현 (최초)

구현 부분은 설명을 위해 불필요한 로직은 생략했다.

아래는 최초 구현이다.

```java

@RestController
@RequiredArgsConstructor
@RequestMapping("/mails/{mailId}/attachments")
public class MailAttachmentController {

    private final MailAttachmentService mailAttachmentService;

    @GetMapping(produces = APPLICATION_ZIP)
    public ResponseEntity<StreamingResponseBody> getZip(@AuthenticationPrincipal SecurityUser user, @PathVariable long mailId,
                                                        HttpServletResponse response) {
        List<MailAttachment> attachments = mailAttachmentService.getAttachments(user.getId(),  mailId);

        responseZipFromAttachments(response.getOutputStream(), attachments);

        ContentDisposition contentDisposition = ContentDisposition
                .attachment()
                .filename(generateFileName(user.getId()))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(contentDisposition);
        headers.setContentType(MediaType.valueOf(APPLICATION_ZIP));

        return ResponseEntity.ok()
                .headers(headers)
                .build();
    }
    
    private void responseZipFromAttachments(OutputStream os, List <MailAttachments> attachments) {
        try(ZipOutputStream zos = new ZipOutputStream(os)) {
            for (MailAttachmentDto.Detail attachment: attachments) {
                ZipEntry zipEntry = new ZipEntry(attachment.getFilename());
                zos.putNextEntry(zipEntry);
                zos.write(attachment.getBytes());
                zos.closeEntry();
            }
        } catch(IOException e) {
            throw new StorageIOException(e);
        }
    }

}

```

### MediaType

produces(Accept)와 Content-type에 application/zip을 사용하기로 했다.

그런데 org.springframework.http.MediaType에서는 static 변수로 application/zip을 지원하지 않는다.

그래서 아래와 같이 static 변수를 선언하였다.

```java
public class CustomMediaTypes {
    public static final String APPLICATION_ZIP = "application/zip";
}
```

### ZipOutputStream

ZipOutputStream을 구현한 부분이다.

```java
private void responseZipFromAttachments(OutputStream os, List <MailAttachments> attachments) {
    try(ZipOutputStream zos = new ZipOutputStream(os)) {
        for (MailAttachmentDto.Detail attachment: attachments) {
            ZipEntry zipEntry = new ZipEntry(attachment.getFilename());
            zos.putNextEntry(zipEntry);
            zos.write(attachment.getBytes());
            zos.closeEntry();
        }
    } catch(IOException e) {
        throw new StorageIOException(e); // Custom Exception
    }
}
```

간략하게 소개해주면 ZipEntry(zip 구성 요소)에 파일명을 하나씩 명시한 후 ZipOutputStream에 넣고 데이터를 넣는 작업을 반복한다.
- stream을 사용하지 않은 이유는 내부에서 예외처리를 하면 코드가 복잡해지고, 순서 보장이 중요하기 때문이다! (한번 시도해보는 것도 추천한다.)

(아실 분은 아시겠지만) 해당 코드에는 몇가지 문제가 있었다.

### 1. Memory 낭비

위와 같이 Response의 OutputStream을 바로 ZipOutputStream으로 변환해서 사용한 이유는 메모리 낭비를 줄이기 위해서이다.

가령 ZipOutputStream을 ByteArrayInputStream에다가 옮겨 닮은 후에 InputStreamResource로 내려주는 방법도 생각할 수 있었다.
- 해당 경우에는 zip 파일(ByteArrayInputStream)이 메모리에 모두 올라가게 된다.

그러한 메모리 낭비를 줄이기 위해 아래 그림과 같이 파일을 메모리에 전부 올리지 않고 데이터를 조금씩 지속적으로 읽으면서 응답하길 원했다.

![img_2.png](img_2.png)

그런데 attachment.getBytes()의 내부 구현을 보니까 모든 byte를 읽어서 메모리에 올리고 있었다.

```java
public byte[] getBytes() {
    try {
       return inputStream.readAllBytes();
    } catch (IOException e) {
        throw new StorageIOException(e.getMessage());
    }
}
```

즉, 폴더의 전체 파일이 메모리로 올라가진 않았지만, 파일을 하나씩 통째로 메모리에 올리고 있었다.

그래서 해당 부분을 변경했다.

```java
// zos.write(attachment.getBytes()); <- 삭제
InputStream is = attachment.getInputStream()
try {
    StreamUtils.copy(is, zos);
} finally {
    is.close();
}
```

StreamUtils를 사용하면 InputStream의 내용을 OutputStream으로 buffer를 사용해서 write할 수 있다.

(InputStream의 close를 직접해야 한다는 불편함이 있는데, 다른 Util은 없을까..?) 

FileCopyUtils를 사용하면 close까지도 직접 해준다.

![img_3.png](img_3.png)

문제는 ZipOutputStream이 닫히면 다음 Entry를 쓸 수 없다. 그래서 해당 처리가 없는 StreamUtils를 활용해서 데이터를 옮긴 다음 InputStream만 닫아주는 처리가 필요하다.

이제 메모리 문제는 해결되었다.

### 2. 헤더 적용

위 Controller 코드를 다시 보면 OutputStream에서 write를 한 다음 ResponseEntity를 만들어서 반환하고 있다.

![img_4.png](img_4.png)

여기서 아래 부분은 동작하지 않는다. 즉, 헤더가 설정되지 않은 채로 응답을 보내게 된다.

zip파일을 쓰는 데 사용했던 HttpServletResponse는 javax.sevlet에서 지원하는 객체이다. 반면 ResponseEntity는 org.springframework.http에 있는 Spring에서 제공하는 추상화된 응답 객체이다.

![img_5.png](img_5.png)

HttpEntityMethodProcessor를 보면 Spring에서는 ResponseEntity를 HttpServletResponse 형태로 다시 변경해주는 방식이다.

해당 부분이 동작하지 않았던 이유는 os.write()로 이미 응답을 모두 내려주고 OutputStream을 닫았기 때문에 더 이상 응답을 내려주지 않았다.

이를 해결하기 위해서는 두 가지 방법이 있었다.

#### StreamingResponseBody


```java
@GetMapping(produces = APPLICATION_ZIP)
public ResponseEntity<StreamingResponseBody> getZip(@AuthenticationPrincipal SecurityUser user, @PathVariable long mailId,
        HttpServletResponse response) {
        List<MailAttachment> attachments = mailAttachmentService.getAttachments(user.getId(),  mailId);

        StreamingResponseBody streamingResponseBody = out -> responseZipFromAttachments(out, attachments);

        ContentDisposition contentDisposition = ContentDisposition
                .attachment()
                .filename(generateFileName(user.getId()))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(contentDisposition);
        headers.setContentType(MediaType.valueOf(APPLICATION_ZIP));

        return ResponseEntity.ok()
                .headers(headers)
                .body(streamingResponseBody);
}
```

위와 같이 StreamingResponseBody를 사용하면 헤더를 세팅한 후에 OutputStream을 쓰는 것이 가능했다.

아래는 StreamingResponseBodyReturnValueHandler의 일부이다.

![img_6.png](img_6.png)

내용은 비동기 스레드를 활용해서 헤더부터 다 세팅한 다음 OutputStream에 데이터를 출력하는 방식이라고 이해하면 된다.

해당 부분은 예외를 콜백받을 수는 있지만, Thread를 추가로 사용하게 되었다.

Spring.io(공식 문서)에 StreamingResponseBody 설명을 보면 TaskExecutor를 명시적으로 구성하는 것이 좋다고 한다.
- https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/mvc/method/annotation/StreamingResponseBody.html

문제는 해당 end-point밖에 비동기 출력을 사용하지 않는데, AsyncMvcExecutor를 구현하면 스레드 풀의 스레드 수를 어떻게 잡을 지 등 애매한 부분이 있었다.
- 추가적인 스레드를 사용하는 점도 다소 애매했다. (메인 기능이 아니라, 부 기능이기 때문)

#### javax.servlet.http.HttpServletResponse

애플리케이션이 OutputStream을 응답에 직접 쓸 수 있게 Spring 4.2부터 StreamingResponseBody를 지원한다.

StreamingResponseBody를 사용하지 않고도 이를 처리할 수 있는데, javax.servlet.http.HttpServletResponse에서 헤더를 먼저 세팅하면 된다.

즉, 해당 부분을 처리하려면 zip 파일을 쓰기(`os.write`) 전에 아래의 처리가 필요합니다.
```java
response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\";");
response.setHeader("Content-Type", contentType);
```

해당 부분을 구현한 결과는 아래와 같다.

이 부분을 사용하지 않았던 이유는 Spring에서 지원하는 추상화된 객체들을 사용할 수 없다는 점이다.
- HttpHeaders, ResponseEntity 등

의미 없이 스레드가 추가되고, executor가 추가되어 시스템이 복잡해지는 것보다는 낫다고 판단하였다. (+ 해당 기능이 주 기능이 아니라, 부가 기능인 점)

아래는 최종적인 구현이다.

```java
@GetMapping(produces = APPLICATION_ZIP)
public ResponseEntity<StreamingResponseBody> getZip(@AuthenticationPrincipal SecurityUser user, @PathVariable long mailId,
        HttpServletResponse response) {
        List<MailAttachment> attachments = mailAttachmentService.getAttachments(user.getId(),  mailId);

        response.setHeader("Content-Disposition", "attachment; filename=\"" + generateFileName(hiworksUser.getUserId()) + "\";");
        response.setHeader("Content-Type", APPLICATION_ZIP);

        responseZipFromAttachments(response, attachments);
}
```

### 모듈화

Zip을 응답으로 내려주는 코드 구현이 완료되었지만, 다른 곳에서도 해당 기능이 추가된다면 동일한 구현을 또 하게 되어 DRY 원칙(반복하지 마라)에 위배될 수 있다.

그래서 아래와 같이 Zip 모듈을 추가하게 되었다.

```java
public static void toZip(OutputStream os, ZipRequest request) {
    Map<String, Integer> fileNames = new HashMap<>();

    try(ZipOutputStream zos = new ZipOutputStream(os)) {
        for(ZipRequest.ZipEntry entry : request.getEntries()) {
            InputStream is = entry.getInputStream();

            String fileName = entry.getFileName();
            fileNames.put(fileName, fileNames.getOrDefault(fileName, 0) + 1);

            int duplicatedCount = fileNames.get(fileName);
            if(duplicatedCount > 1) {
                fileName = String.format("%s_%s_%s", FileNameUtils.getBaseName(fileName), duplicatedCount, FileNameUtils.getExtension(fileName));
            }

            ZipEntry zipEntry = new ZipEntry(fileName);
            zos.putNextEntry(zipEntry);
            try {
                StreamUtils.copy(entry.getInputStream(), zos);
            } finally {
                is.close();
            }
            zos.closeEntry();
        }
    } catch (IOException e) {
        throw new StorageIOException(e.getMessage());
    }

}
```

Map으로 filenames를 저장하는 이유는 ZipOutputStream에서는 ZipEntry의 파일 명을 Hash로 하여 구분한다.

즉, 동일한 파일 명을 가진 Entry가 있으면 하나로 인식하고 1개의 Entry에 덮어써지는 문제가 발생한다.

그래서 IU.png가 2개가 있더라도, 하나는 IU_2.png가 될 수 있도록 처리하였다.

Request 객체는 아래와 같다.

```java
@Getter
public class ZipRequest {

    private final List<ZipEntry> entries;

    public ZipRequest() {
        this.entries = new ArrayList<>();
    }

    public void addEntry(InputStream is, String fileName) {
        entries.add(new ZipEntry(is, fileName));
    }

    @Getter
    @AllArgsConstructor
    static class ZipEntry {
        private final InputStream inputStream;
        private final String fileName;
    }

}
```

그래서 Controller에서는 아래와 같이 공통화된 모듈이 필요로 하는 객체를 만들어서 모듈의 메서드를 호출하는 방식으로 변경하였다.

```java
private void responseZipFromAttachments(HttpServletResponse response, List<MailAttachmentDto.Detail> noCidAttachments) {
    try (OutputStream os = response.getOutputStream()) {
        ZipRequest zipRequest = new ZipRequest();
        for (MailAttachmentDto.Detail attachment : noCidAttachments) {
            zipRequest.addEntry(attachment.getDataSource().getInputStream(), attachment.getFilename());
        }
        ZipModule.toZip(os, zipRequest);
    } catch (IOException e) {
        throw new StorageIOException(e.getMessage());
    }
}
```

## 참고

- https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/mvc/method/annotation/StreamingResponseBody.html
- https://madplay.github.io/post/java-file-zip
- https://stackoverflow.com/questions/53885467/java-zip-files-from-streams-instantly-without-using-byte
- https://velog.io/@dailylifecoding/file-zip-and-download