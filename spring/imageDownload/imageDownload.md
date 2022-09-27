## Spring - Image 조회를 효율적으로 구현하기!

기존에 Image를 조회하는 Controller Method가 있었다. 예시를 살펴보자.

## byte[]

```java
@GetMapping("/attachments/{no}")
@ResponseStatus(HttpStatus.OK)
public byte[] getImage(..., @PathVariable long no) {
    // 생략
    return attachmentStorage.getImageContent(no);
}
```
위와 같이 Controller method에서 byte[]로 이미지의 binary data를 반환해주면,

사용하는 쪽에서 `<img src="api-endpoint" />`와 같은 방식으로 이미지를 렌더링할 수 있다.

문제는 byte[]를 반환하기 위해서는 해당 파일을 앱서버에서 모두 읽어서 메모리에 보관한 이후에 클라이언트에 반환할 수 있다는 점이다.
- 즉 서버의 메모리가 낭비될 수 있다.

반면 아래의 코드를 보자.

## InputStreamResource

```java
@GetMapping
@ResponseStatus(HttpStatus.OK)
public Resource getImage(..., @PathVariable long no) throws IOException {
    Resource resource = attachmentStorage.getImageContent(no);

    return Resource;
}
```

```java
public Resource getImageContent(..., long no) {
    Path path = Paths.get(..., no);
    try {
        return new InputStreamResource(new FileInputStream(path.toFile()));
    } catch(IOException e) {
        throw new StorageIOException(e.getMessage());
    }
}
```
해당 AttachmentStorage.getImageContent(no)는 InputStreamResource의 구현체를 반환한다.

InputStreamResource를 반환하면 <mark>청크 전송 인코딩</mark>을 사용하기에 Resource를 스트리밍 방식으로 조금씩 읽어서 전송한다.

결과적으로 서버의 Resource를 아낄 수 있다.

추가로 아래의 단점도 고려해야 한다.
- 파일을 다운로드 받기 전에는 용량 크기를 알 수 없다.
- Controller에서 InputStream을 읽는 책임을 가지게 된다.
  - Controller method에서 예외를 던져야 한다.

## 추가

아래는 생략이 없는 결과 코드이다.
```java
@GetMapping
public ResponseEntity<Resource> getImage(@RequestParam(value = "file_name") @NotBlank String fileName) throws IOException {
    Resource resource = attachmentStorage.getContent(fileName);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName + ".jpg")
        .contentType(MediaType.IMAGE_JPEG)
        .body(resource);
}
```
Header를 추가한 이유는 Content_Disposition을 추가해야 해당 이미지를 다운로드할 때 파일명과 확장자를 받을 수 있다.

추가로 url을 통한 직접 조회가 가능하도록 Content_type을 JPEG로 고정했다.

## 참고
- https://move02.github.io/articles/2020-07/Spring-File-Download