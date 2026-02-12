package com.gohealth.issuetracker.model;

import java.time.Instant;
import java.util.UUID;

public record Issue(
        String id,
        String description,
        String parentId,
        IssueStatus status,
        Instant createdDate,
        Instant updatedDate
) {
    public static Issue create(String description, String parentId) {
        Instant now = Instant.now();

        return new Issue(
                UUID.randomUUID().toString(),
                description,
                parentId,
                IssueStatus.OPEN,
                now,
                now
        );
    }

    public Issue withStatus(IssueStatus status) {
        return new Issue(id, description, parentId, status, createdDate, Instant.now());
    }
}
