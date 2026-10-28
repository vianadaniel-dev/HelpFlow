package com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.repository;

import com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.entity.TicketHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaTicketHistoryRepository extends JpaRepository<TicketHistoryEntity, UUID> {
    List<TicketHistoryEntity> findByTicketIdOrderByOccurredAtAsc(UUID ticketId);
}