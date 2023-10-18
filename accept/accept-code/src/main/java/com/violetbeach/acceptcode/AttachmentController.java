package com.violetbeach.acceptcode;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/attachments")
class AttachmentController {

    @GetMapping("/{attachmentId}")
    public ResponseEntity<Attachment> getAttachmentMeta(@PathVariable String attachmentId) {
        if(attachmentId.equals("1")) {
            throw new RuntimeException();
        }
        Attachment attachment = new Attachment(attachmentId, "test.img", "/path");
        return ResponseEntity.ok(attachment);
    }

    @GetMapping(value = "/{attachmentId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getAttachmentData(@PathVariable String attachmentId) throws IOException {
        if(attachmentId.equals("1")) {
            throw new RuntimeException();
        }
        InputStream is = new FileInputStream(getFile(attachmentId));
        Resource resource = new InputStreamResource(is);
        return ResponseEntity.ok(resource);
    }

    public File getFile(String id) {
        return new File("/Users/user/Documents/blog-code/accept/accept-code/src/main/resources/static/file.txt");
    }

}
