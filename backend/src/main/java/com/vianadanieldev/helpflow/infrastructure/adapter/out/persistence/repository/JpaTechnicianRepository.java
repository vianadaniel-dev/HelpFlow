package com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.repository;

import com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.entity.TechnicianEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaTechnicianRepository extends JpaRepository<TechnicianEntity, UUID> {
    boolean existsByDocument(String document);
    boolean existsByCorporateEmail(String corporateEmail);
    Optional<TechnicianEntity> findByCorporateEmail(String corporateEmail); // Requerido por AuthenticationAdapter
}