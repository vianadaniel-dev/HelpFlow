package com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.entity;

import com.vianadanieldev.helpflow.domain.model.TicketCategory;
import com.vianadanieldev.helpflow.domain.model.TicketPriority;
import com.vianadanieldev.helpflow.domain.model.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketEntity {
    @Id
    private UUID id;
    @Column(name = "requester_id")
    private UUID requesterId;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private TicketCategory category;
    @Enumerated(EnumType.STRING)
    private TicketPriority priority;
    @Enumerated(EnumType.STRING)
    private TicketStatus status;
    private String headquarters;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "sla_deadline")
    private Instant slaDeadline;
    @Column(name = "assigned_technician_id")
    private UUID assignedTechnicianId;
    @Column(name = "escalation_count")
    private int escalationCount;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ticket_assets", joinColumns = @JoinColumn(name = "ticket_id"))
    @Column(name = "asset")
    private List<String> affectedAssets;
}