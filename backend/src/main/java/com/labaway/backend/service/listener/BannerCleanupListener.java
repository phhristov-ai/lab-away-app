package com.labaway.backend.service.listener;

import com.labaway.backend.event.BannerReplacedEvent;
import com.labaway.backend.service.storage.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BannerCleanupListener {

    private final S3Service s3Service;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(BannerReplacedEvent event) {
        s3Service.deleteFile(event.oldUrl());
    }
}