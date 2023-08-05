## SharedInputStream

`Java Mail API` provides a mechanism to save memory usage.

![img_4.png](img_4.png)

If you use `SharedByteInputStream`, you can use it as a **Call by Reference** without assigning a new byte[] of the factor.

### Before

Memory snapshot for the test below.

```java
@Test
void memoryTest() throws Exception {
    FileInputStream fileInputStream = new FileInputStream("/test/oom.eml");
    byte[] bytes = fileInputStream.readAllBytes();
    Session session = Session.getInstance(System.getProperties());
    MimeMessage mimeMessage = new MimeMessage(session, new ByteArrayInputStream(bytes));
}
```

![img_1.png](img_1.png)

### After

Memory snapshot for the test below.

```java
@Test
void memoryTest() throws Exception {
    FileInputStream fileInputStream = new FileInputStream("/test/oom.eml");
    byte[] bytes = fileInputStream.readAllBytes();
    Session session = Session.getInstance(System.getProperties());
    MimeMessage mimeMessage = new MimeMessage(session, new SharedByteArrayInputStream(bytes));
}
```

![img_3.png](img_3.png)

Thanks.