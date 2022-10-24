package study.datajpa.service;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MemberResourceService {

    public void upload(MultipartFile image) {
        System.out.println("이미지 업로드가 완료되었습니다.");
    }

}
