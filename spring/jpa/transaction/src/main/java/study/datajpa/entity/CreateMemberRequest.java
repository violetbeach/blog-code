package study.datajpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class CreateMemberRequest {

    private final String username;
    private final MultipartFile userImage;

}
