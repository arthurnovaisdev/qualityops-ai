package com.arthurnovaisdev.qualityops.service;

import com.arthurnovaisdev.qualityops.event.ComplaintChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class ComplaintVectorSyncService {

    private final ComplaintVectorService complaintVectorService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleComplaintChanged(
            ComplaintChangedEvent event
    ) {

        complaintVectorService.indexComplaint(
                event.complaintId()
        );
    }
}