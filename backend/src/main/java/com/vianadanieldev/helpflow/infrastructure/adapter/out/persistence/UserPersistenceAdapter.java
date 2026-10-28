package com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence;

import com.vianadanieldev.helpflow.application.port.out.UserRepositoryPort;
import com.vianadanieldev.helpflow.domain.model.User;
import com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.mapper.UserMapper;
import com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final JpaUserRepository repository;
    private final UserMapper mapper;

    @Override
    public void save(User user) {
        repository.save(mapper.toEntity(user));
    }

    @Override
    public Optional<User> findById(UUID id) {
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