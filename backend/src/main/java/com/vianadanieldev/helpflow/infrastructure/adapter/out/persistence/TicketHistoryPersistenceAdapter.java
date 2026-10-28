package com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence;

import com.vianadanieldev.helpflow.application.port.out.TicketHistoryRepositoryPort;
import com.vianadanieldev.helpflow.domain.model.TicketHistory;
import com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.mapper.TicketHistoryMapper;
import com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.repository.JpaTicketHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TicketHistoryPersistenceAdapter implements TicketHistoryRepositoryPort {

    private final JpaTicketHistoryRepository repository;
    private final TicketHistoryMapper mapper;

    @Override
    public void save(TicketHistory entry) {
        repository.save(mapper.toEntity(entry));
    }

    @Override
    public List<TicketHistory> findByTicketId(UUID ticketId) {
        return repository.findByTicketIdOrderByOccurredAtAsc(ticketId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}