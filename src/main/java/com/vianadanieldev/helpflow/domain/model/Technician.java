package com.vianadanieldev.helpflow.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Technician {

    private final UUID id;
    private final String document;
    private final String corporateEmail;
    private String name;
    private String headquarters;
    private Specialty specialty;
    private SupportProfile supportProfile;
    private EntityStatus status;

    public static Technician register(String document, String name, String corporateEmail,
                                      String headquarters, Specialty specialty, SupportProfile supportProfile) {
        return Technician.builder()
                .id(UUID.randomUUID())
                .document(Guard.notBlank(document, "document"))
                .name(Guard.notBlank(name, "name"))
                .corporateEmail(Guard.notBlank(corporateEmail, "corporateEmail"))
                .headquarters(Guard.notBlank(headquarters, "headquarters"))
                .specialty(Guard.notNull(specialty, "specialty"))
                .supportProfile(Guard.notNull(supportProfile, "supportProfile"))
                .status(EntityStatus.ACTIVE)
                .build();
    }

    public void update(String name, String headquarters, Specialty specialty, SupportProfile supportProfile) {
        this.name = Guard.notBlank(name, "name");
        this.headquarters = Guard.notBlank(headquarters, "headquarters");
        this.specialty = Guard.notNull(specialty, "specialty");
        this.supportProfile = Guard.notNull(supportProfile, "supportProfile");
    }

    public void activate() { this.status = EntityStatus.ACTIVE; }

    public void deactivate() { this.status = EntityStatus.INACTIVE; }

    public boolean isActive() { return status == EntityStatus.ACTIVE; }
}
