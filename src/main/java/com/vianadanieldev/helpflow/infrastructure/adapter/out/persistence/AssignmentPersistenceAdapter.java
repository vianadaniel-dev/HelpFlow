package com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence;

import com.vianadanieldev.helpflow.application.port.out.AssignmentRepositoryPort;
import com.vianadanieldev.helpflow.domain.model.Assignment;
import com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.mapper.AssignmentMapper;
import com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.repository.JpaAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AssignmentPersistenceAdapter implements AssignmentRepositoryPort {

    private final JpaAssignmentRepository repository;
    private final AssignmentMapper mapper;

    @Override
    public void save(Assignment assignment) {
        repository.save(mapper.toEntity(assignment));
    }

    @Override
    public Optional<Assignment> findActiveByTicketId(UUID ticketId) {
        return repository.findByTicketIdAndActiveTrue(ticketId).map(mapper::toDomain);
    }

    @Override
    public int countActiveByTechnicianId(UUID technicianId) {
        return repository.countByTechnicianIdAndActiveTrue(technicianId);
    }
}