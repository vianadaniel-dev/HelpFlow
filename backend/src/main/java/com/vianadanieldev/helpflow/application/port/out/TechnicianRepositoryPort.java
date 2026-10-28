package com.vianadanieldev.helpflow.application.port.out;

import com.vianadanieldev.helpflow.domain.model.Technician;

import java.util.Optional;
import java.util.UUID;

public interface TechnicianRepositoryPort {
    void save(Technician technician);
    Optional<Technician> findById(UUID id);
    boolean existsByDocument(String document);
    boolean existsByCorporateEmail(String corporateEmail);
}
