package com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence;

import com.vianadanieldev.helpflow.application.port.out.TechnicianRepositoryPort;
import com.vianadanieldev.helpflow.domain.model.Technician;
import com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.mapper.TechnicianMapper;
import com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.repository.JpaTechnicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TechnicianPersistenceAdapter implements TechnicianRepositoryPort {

    private final JpaTechnicianRepository repository;
    private final TechnicianMapper mapper;

    @Override
    public void save(Technician technician) {
        repository.save(mapper.toEntity(technician));
    }

    @Override
    public Optional<Technician> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByDocument(String document) {
        return repository.existsByDocument(document);
    }

    @Override
    public boolean existsByCorporateEmail(String corporateEmail) {
        return repository.existsByCorporateEmail(corporateEmail);
    }
}