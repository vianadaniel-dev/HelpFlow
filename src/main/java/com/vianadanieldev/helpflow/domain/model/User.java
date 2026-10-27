package com.vianadanieldev.helpflow.domain.model;

import com.vianadanieldev.helpflow.domain.exception.InactiveUserException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/** Requester of IT tickets. Document and corporate email never change once registered. */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    private final UUID id;
    private final String document;
    private final String corporateEmail;
    private String name;
    private String headquarters;
    private String area;
    private EntityStatus status;

    /** Email must already be validated/normalized by CorporateEmailPolicy. */
    public static User register(String document, String name, String corporateEmail,
                                String headquarters, String area) {
        return User.builder()
                .id(UUID.randomUUID())
                .document(Guard.notBlank(document, "document"))
                .name(Guard.notBlank(name, "name"))
                .corporateEmail(Guard.notBlank(corporateEmail, "corporateEmail"))
                .headquarters(Guard.notBlank(headquarters, "headquarters"))
                .area(Guard.notBlank(area, "area"))
                .status(EntityStatus.ACTIVE)
                .build();
    }

    public void updateBasicInfo(String name, String headquarters, String area) {
        this.name = Guard.notBlank(name, "name");
        this.headquarters = Guard.notBlank(headquarters, "headquarters");
        this.area = Guard.notBlank(area, "area");
    }

    public void activate() { this.status = EntityStatus.ACTIVE; }

    public void deactivate() { this.status = EntityStatus.INACTIVE; }

    public boolean isActive() { return status == EntityStatus.ACTIVE; }

    public void ensureCanRegisterTickets() {
        if (!isActive()) {
            throw new InactiveUserException(id);
        }
    }
}
