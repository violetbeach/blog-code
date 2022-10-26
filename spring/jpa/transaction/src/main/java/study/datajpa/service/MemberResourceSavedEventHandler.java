package study.datajpa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service

@RequiredArgsConstructor
public class MemberResourceSavedEventHandler {

    private final MemberResourceService resourceService;

    @Async
    @TransactionalEventListener(
            classes = MemberResourceSavedEvent.class,
            phase = TransactionPhase.AFTER_ROLLBACK
    )
    public void handle(MemberResourceSavedEvent event) {
        resourceService.delete(event.getResources());
    }



}
