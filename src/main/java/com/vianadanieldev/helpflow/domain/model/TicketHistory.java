package com.vianadanieldev.helpflow.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/** Append-only audit trail. performedBy == null means the system acted (assignment, SLA escalation). */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TicketHistory {

    private final UUID id;
    private final UUID ticketId;
    private final HistoryAction action;
    private final TicketStatus fromStatus;
    private final TicketStatus toStatus;
    private final String observation;
    private final UUID performedBy;
    private final Instant occurredAt;

    public static TicketHistory record(UUID ticketId, HistoryAction action, TicketStatus from, TicketStatus to,
                                       String observation, UUID performedBy, Instant occurredAt) {
        return TicketHistory.builder()
                .id(UUID.randomUUID())
                .ticketId(ticketId)
                .action(action)
                .fromStatus(from)
                .toStatus(to)
                .observation(observation)
                .performedBy(performedBy)
                .occurredAt(occurredAt)
                .build();
    }
}
