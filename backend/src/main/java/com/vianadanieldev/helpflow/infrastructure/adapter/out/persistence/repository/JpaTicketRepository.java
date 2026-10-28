package com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.repository;

import com.vianadanieldev.helpflow.domain.model.TicketStatus;
import com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface JpaTicketRepository extends JpaRepository<TicketEntity, UUID> {
    @Query("SELECT t FROM TicketEntity t WHERE t.status IN :trackedStatuses AND t.slaDeadline < :now")
    List<TicketEntity> findSlaBreached(@Param("now") Instant now, @Param("trackedStatuses") List<TicketStatus> trackedStatuses);
}