package com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.entity;

import com.vianadanieldev.helpflow.domain.model.HistoryAction;
import com.vianadanieldev.helpflow.domain.model.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ticket_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketHistoryEntity {
    @Id
    private UUID id;
    @Column(name = "ticket_id")
    private UUID ticketId;
    @Enumerated(EnumType.STRING)
    private HistoryAction action;
    @Enumerated(EnumType.STRING)
    @Column(name = "from_status")
    private TicketStatus fromStatus;
    @Enumerated(EnumType.STRING)
    @Column(name = "to_status")
    private TicketStatus toStatus;
    private String observation;
    @Column(name = "performed_by")
    private UUID performedBy;
    @Column(name = "occurred_at")
    private Instant occurredAt;
}