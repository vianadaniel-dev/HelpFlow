package com.vianadanieldev.helpflow.application.service;

import com.vianadanieldev.helpflow.application.port.in.ManageUserUseCase;
import com.vianadanieldev.helpflow.application.port.out.UserRepositoryPort;
import com.vianadanieldev.helpflow.domain.exception.DuplicateResourceException;
import com.vianadanieldev.helpflow.domain.exception.ResourceNotFoundException;
import com.vianadanieldev.helpflow.domain.model.CorporateEmailPolicy;
import com.vianadanieldev.helpflow.domain.model.EntityStatus;
import com.vianadanieldev.helpflow.domain.model.Guard;
import com.vianadanieldev.helpflow.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements ManageUserUseCase {

    private final UserRepositoryPort users;
    private final CorporateEmailPolicy emailPolicy;

    @Override
    public User register(RegisterUserCommand c) {
        String email = emailPolicy.validate(c.corporateEmail());
        String document = Guard.notBlank(c.document(), "document");
        if (users.existsByDocument(document)) {
            throw new DuplicateResourceException("User", "document", document);
        }
        if (users.existsByCorporateEmail(email)) {
            throw new DuplicateResourceException("User", "corporateEmail", email);
        }
        User user = User.register(document, c.name(), email, c.headquarters(), c.area());
        users.save(user);
        return user;
    }

    @Override
    public User updateBasicInfo(UUID id, UpdateUserCommand c) {
        User user = find(id);
        user.updateBasicInfo(c.name(), c.headquarters(), c.area());
        users.save(user);
        return user;
    }

    @Override
    public User changeStatus(UUID id, EntityStatus status) {
        User user = find(id);
        if (status == EntityStatus.ACTIVE) user.activate(); else user.deactivate();
        users.save(user);
        return user;
    }

    private User find(UUID id) {
        return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
