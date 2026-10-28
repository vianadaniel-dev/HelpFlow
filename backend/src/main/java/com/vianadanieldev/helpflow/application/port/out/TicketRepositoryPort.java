package com.vianadanieldev.helpflow.application.port.out;

import com.vianadanieldev.helpflow.domain.model.Ticket;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepositoryPort {
    void save(Ticket ticket);
    Optional<Ticket> findById(UUID id);

    /** Tickets in OPEN, ASSIGNED or EN_PROGRESS whose SLA deadline is before {@code now}. */
    List<Ticket> findSlaBreached(Instant now);
}
