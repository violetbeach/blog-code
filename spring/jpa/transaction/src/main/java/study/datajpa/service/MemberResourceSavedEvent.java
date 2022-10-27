package study.datajpa.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class MemberResourceSavedEvent {

    private MultipartFile resources;

}
