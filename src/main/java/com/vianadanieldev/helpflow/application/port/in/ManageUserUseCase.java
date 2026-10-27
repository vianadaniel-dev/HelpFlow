package com.vianadanieldev.helpflow.application.port.in;

import com.vianadanieldev.helpflow.domain.model.EntityStatus;
import com.vianadanieldev.helpflow.domain.model.User;

import java.util.UUID;

public interface ManageUserUseCase {

    User register(RegisterUserCommand command);

    User updateBasicInfo(UUID id, UpdateUserCommand command);

    User changeStatus(UUID id, EntityStatus status);

    record RegisterUserCommand(String document, String name, String corporateEmail,
                               String headquarters, String area) {}

    record UpdateUserCommand(String name, String headquarters, String area) {}
}
