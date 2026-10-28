package com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.repository;

import com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {
    boolean existsByDocument(String document);
    boolean existsByCorporateEmail(String corporateEmail);
    Optional<UserEntity> findByCorporateEmail(String corporateEmail); // Requerido por AuthenticationAdapter
}