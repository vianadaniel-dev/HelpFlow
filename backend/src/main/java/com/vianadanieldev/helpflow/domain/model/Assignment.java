package com.vianadanieldev.helpflow.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/** Links a ticket to a technician. Only active assignments count toward the technician's load. */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Assignment {

    private final UUID id;
    private final UUID ticketId;
    private final UUID technicianId;
    private final int estimatedMinutes;
    private final Instant assignedAt;
    private boolean active;
    private Instant releasedAt;

    public static Assignment create(UUID ticketId, UUID technicianId, int estimatedMinutes, Instant now) {
        return Assignment.builder()
                .id(UUID.randomUUID())
                .ticketId(ticketId)
                .technicianId(technicianId)
                .estimatedMinutes(estimatedMinutes)
                .assignedAt(now)
                .active(true)
                .build();
    }

    public void release(Instant now) {
        this.active = false;
        this.releasedAt = now;
    }
}
