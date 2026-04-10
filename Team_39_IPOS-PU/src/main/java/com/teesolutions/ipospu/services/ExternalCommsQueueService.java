package com.teesolutions.ipospu.services;

import com.teesolutions.ipospu.dto.ExternalCommsQueueEntry;
import com.teesolutions.ipospu.repositories.CommsRepository;
import com.teesolutions.ipospu.repositories.ExternalCommsQueueRepository;

import java.util.List;


public class ExternalCommsQueueService {

    private final ExternalCommsQueueRepository queueRepository = new ExternalCommsQueueRepository();
    private final CommsRepository commsRepository = new CommsRepository();

    
    public int drainPendingToOutbox(int batchSize) {
        List<ExternalCommsQueueEntry> pending = queueRepository.fetchPending(batchSize);
        int moved = 0;
        for (ExternalCommsQueueEntry row : pending) {
            try {
                if (commsRepository.saveOutboundEmail(
                        row.recipientEmail(),
                        row.subject(),
                        augmentBodyWithSource(row),
                        row.purpose()).insertedIntoOutbox()) {
                    queueRepository.markConsumed(row.id());
                    moved++;
                }
            } catch (RuntimeException ex) {
                System.err.println("[PU] external comms drain failed for queue id=" + row.id() + ": " + ex.getMessage());
            }
        }
        return moved;
    }

    private static String augmentBodyWithSource(ExternalCommsQueueEntry row) {
        String src = row.sourceSystem() == null ? "UNKNOWN" : row.sourceSystem().trim();
        String footer = "\n\n---\n(Source: " + src;
        if (row.referenceKey() != null && !row.referenceKey().isBlank()) {
            footer += ", ref: " + row.referenceKey().trim();
        }
        footer += ")\n";
        return row.body() + footer;
    }
}
