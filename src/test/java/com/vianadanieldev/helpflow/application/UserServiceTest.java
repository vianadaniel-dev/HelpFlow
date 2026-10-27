package com.vianadanieldev.helpflow.application;

import com.vianadanieldev.helpflow.application.port.in.ManageUserUseCase.RegisterUserCommand;
import com.vianadanieldev.helpflow.application.port.out.UserRepositoryPort;
import com.vianadanieldev.helpflow.application.service.UserService;
import com.vianadanieldev.helpflow.domain.exception.DuplicateResourceException;
import com.vianadanieldev.helpflow.domain.exception.InvalidCorporateEmailException;
import com.vianadanieldev.helpflow.domain.exception.ResourceNotFoundException;
import com.vianadanieldev.helpflow.domain.model.CorporateEmailPolicy;
import com.vianadanieldev.helpflow.domain.model.EntityStatus;
import com.vianadanieldev.helpflow.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.vianadanieldev.helpflow.support.TestData.activeUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepositoryPort users;
    UserService service;

    private final RegisterUserCommand command =
            new RegisterUserCommand("1001", "Ana Perez", "Ana.Perez@helpflow.com", "BOG", "Finance");

    @BeforeEach
    void setUp() {
        service = new UserService(users, new CorporateEmailPolicy(List.of("helpflow.com")));
    }

    @Test
    void registersActiveUserWithNormalizedEmail() {
        User user = service.register(command);

        assertThat(user.getStatus()).isEqualTo(EntityStatus.ACTIVE);
        assertThat(user.getCorporateEmail()).isEqualTo("ana.perez@helpflow.com");
        verify(users).save(user);
    }

    @Test
    void rejectsDuplicateDocument() {
        when(users.existsByDocument("1001")).thenReturn(true);

        assertThatThrownBy(() -> service.register(command)).isInstanceOf(DuplicateResourceException.class);
        verify(users, never()).save(any());
    }

    @Test
    void rejectsDuplicateEmail() {
        when(users.existsByCorporateEmail("ana.perez@helpflow.com")).thenReturn(true);

        assertThatThrownBy(() -> service.register(command)).isInstanceOf(DuplicateResourceException.class);
        verify(users, never()).save(any());
    }

    @Test
    void rejectsNonCorporateEmailBeforeTouchingTheRepository() {
        var bad = new RegisterUserCommand("1001", "Ana", "ana@gmail.com", "BOG", "Finance");

        assertThatThrownBy(() -> service.register(bad)).isInstanceOf(InvalidCorporateEmailException.class);
        verifyNoInteractions(users);
    }

    @Test
    void deactivatesUser() {
        User user = activeUser();
        when(users.findById(user.getId())).thenReturn(Optional.of(user));

        service.changeStatus(user.getId(), EntityStatus.INACTIVE);

        assertThat(user.isActive()).isFalse();
        verify(users).save(user);
    }

    @Test
    void failsWhenUserDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(users.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeStatus(id, EntityStatus.ACTIVE))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
