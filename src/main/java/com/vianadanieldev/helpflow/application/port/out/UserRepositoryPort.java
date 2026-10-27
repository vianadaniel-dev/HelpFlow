package com.vianadanieldev.helpflow.application.port.out;

import com.vianadanieldev.helpflow.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    void save(User user);
    Optional<User> findById(UUID id);
    boolean existsByDocument(String document);
    boolean existsByCorporateEmail(String corporateEmail);
}
