package com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.repository;

import com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.entity.AssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaAssignmentRepository extends JpaRepository<AssignmentEntity, UUID> {
    Optional<AssignmentEntity> findByTicketIdAndActiveTrue(UUID ticketId);
    int countByTechnicianIdAndActiveTrue(UUID technicianId);
}