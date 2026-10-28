package com.vianadanieldev.helpflow.application.port.out;

import com.vianadanieldev.helpflow.domain.model.Role;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/** Outbound port for authentication (JWT issuing/validation lives in infrastructure.security). */
public interface AuthenticationPort {

    /** @return a signed access token, or empty when credentials are wrong */
    Optional<String> authenticate(String corporateEmail, String rawPassword);

    /** The caller of the current request, if any. */
    Optional<AuthenticatedUser> currentUser();

    record AuthenticatedUser(UUID userId, String corporateEmail, Set<Role> roles) {}
}
