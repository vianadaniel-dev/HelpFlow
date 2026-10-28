package com.vianadanieldev.helpflow.application.port.in;

import com.vianadanieldev.helpflow.domain.model.EntityStatus;
import com.vianadanieldev.helpflow.domain.model.Specialty;
import com.vianadanieldev.helpflow.domain.model.SupportProfile;
import com.vianadanieldev.helpflow.domain.model.Technician;

import java.util.UUID;

public interface ManageTechnicianUseCase {

    Technician register(RegisterTechnicianCommand command);

    Technician update(UUID id, UpdateTechnicianCommand command);

    Technician changeStatus(UUID id, EntityStatus status);

    record RegisterTechnicianCommand(String document, String name, String corporateEmail, String headquarters,
                                     Specialty specialty, SupportProfile supportProfile) {}

    record UpdateTechnicianCommand(String name, String headquarters,
                                   Specialty specialty, SupportProfile supportProfile) {}
}
