package com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence;

import com.vianadanieldev.helpflow.application.port.out.TicketRepositoryPort;
import com.vianadanieldev.helpflow.domain.model.Ticket;
import com.vianadanieldev.helpflow.domain.model.TicketStatus;
import com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.mapper.TicketMapper;
import com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.repository.JpaTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TicketPersistenceAdapter implements TicketRepositoryPort {

    private final JpaTicketRepository repository;
    private final TicketMapper mapper;

    @Override
    public void save(Ticket ticket) {
        repository.save(mapper.toEntity(ticket));
    }

    @Override
    public Optional<Ticket> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Ticket> findSlaBreached(Instant now) {
        List<TicketStatus> tracked = List.of(TicketStatus.OPEN, TicketStatus.ASSIGNED, TicketStatus.EN_PROGRESS);
        return repository.findSlaBreached(now, tracked).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}