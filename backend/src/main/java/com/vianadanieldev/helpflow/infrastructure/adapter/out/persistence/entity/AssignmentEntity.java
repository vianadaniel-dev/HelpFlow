package com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentEntity {
    @Id
    private UUID id;
    @Column(name = "ticket_id")
    private UUID ticketId;
    @Column(name = "technician_id")
    private UUID technicianId;
    @Column(name = "estimated_minutes")
    private int estimatedMinutes;
    @Column(name = "assigned_at")
    private Instant assignedAt;
    private boolean active;
    @Column(name = "released_at")
    private Instant releasedAt;
}