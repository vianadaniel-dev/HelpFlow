package com.vianadanieldev.helpflow.application.service;

import com.vianadanieldev.helpflow.application.port.in.ManageTechnicianUseCase;
import com.vianadanieldev.helpflow.application.port.out.TechnicianRepositoryPort;
import com.vianadanieldev.helpflow.domain.exception.DuplicateResourceException;
import com.vianadanieldev.helpflow.domain.exception.ResourceNotFoundException;
import com.vianadanieldev.helpflow.domain.model.CorporateEmailPolicy;
import com.vianadanieldev.helpflow.domain.model.EntityStatus;
import com.vianadanieldev.helpflow.domain.model.Guard;
import com.vianadanieldev.helpflow.domain.model.Technician;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TechnicianService implements ManageTechnicianUseCase {

    private final TechnicianRepositoryPort technicians;
    private final CorporateEmailPolicy emailPolicy;

    @Override
    public Technician register(RegisterTechnicianCommand c) {
        String email = emailPolicy.validate(c.corporateEmail());
        String document = Guard.notBlank(c.document(), "document");
        if (technicians.existsByDocument(document)) {
            throw new DuplicateResourceException("Technician", "document", document);
        }
        if (technicians.existsByCorporateEmail(email)) {
            throw new DuplicateResourceException("Technician", "corporateEmail", email);
        }
        Technician technician = Technician.register(document, c.name(), email, c.headquarters(),
                c.specialty(), c.supportProfile());
        technicians.save(technician);
        return technician;
    }

    @Override
    public Technician update(UUID id, UpdateTechnicianCommand c) {
        Technician technician = find(id);
        technician.update(c.name(), c.headquarters(), c.specialty(), c.supportProfile());
        technicians.save(technician);
        return technician;
    }

    @Override
    public Technician changeStatus(UUID id, EntityStatus status) {
        Technician technician = find(id);
        if (status == EntityStatus.ACTIVE) technician.activate(); else technician.deactivate();
        technicians.save(technician);
        return technician;
    }

    private Technician find(UUID id) {
        return technicians.findById(id).orElseThrow(() -> new ResourceNotFoundException("Technician", id));
    }
}
