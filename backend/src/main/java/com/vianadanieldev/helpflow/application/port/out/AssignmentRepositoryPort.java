package com.vianadanieldev.helpflow.application.port.out;

import com.vianadanieldev.helpflow.domain.model.Assignment;

import java.util.Optional;
import java.util.UUID;

public interface AssignmentRepositoryPort {
    void save(Assignment assignment);
    Optional<Assignment> findActiveByTicketId(UUID ticketId);
    int countActiveByTechnicianId(UUID technicianId);
}
