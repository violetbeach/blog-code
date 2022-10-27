package study.datajpa.service;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import study.datajpa.common.Events;

@Component
public class MemberResourceService {

    public void upload(MultipartFile image) {
        Events.raise(new MemberResourceSavedEvent(image));
        System.out.println("이미지 업로드가 완료되었습니다.");
    }

    public void delete(MultipartFile file) {
        System.out.println("이미지 파일 삭제되었습니다.");
    }

}
