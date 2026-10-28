package com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.entity;

import com.vianadanieldev.helpflow.domain.model.EntityStatus;
import com.vianadanieldev.helpflow.domain.model.Specialty;
import com.vianadanieldev.helpflow.domain.model.SupportProfile;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "technicians")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnicianEntity {
    @Id
    private UUID id;
    private String document;
    @Column(name = "corporate_email")
    private String corporateEmail;

    private String password; // Agregado para Spring Security

    private String name;
    private String headquarters;
    @Enumerated(EnumType.STRING)
    private Specialty specialty;
    @Enumerated(EnumType.STRING)
    @Column(name = "support_profile")
    private SupportProfile supportProfile;
    @Enumerated(EnumType.STRING)
    private EntityStatus status;
}