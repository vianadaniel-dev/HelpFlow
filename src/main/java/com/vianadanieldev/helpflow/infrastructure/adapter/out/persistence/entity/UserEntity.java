package com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.entity;

import com.vianadanieldev.helpflow.domain.model.EntityStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    private UUID id;
    private String document;
    @Column(name = "corporate_email")
    private String corporateEmail;

    private String password; // Agregado para Spring Security

    private String name;
    private String headquarters;
    private String area;
    @Enumerated(EnumType.STRING)
    private EntityStatus status;
}