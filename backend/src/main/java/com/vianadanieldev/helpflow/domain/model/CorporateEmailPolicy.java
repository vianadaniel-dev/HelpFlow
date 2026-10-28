package com.vianadanieldev.helpflow.domain.model;

import com.vianadanieldev.helpflow.domain.exception.InvalidCorporateEmailException;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CorporateEmailPolicy {

    private static final Pattern FORMAT = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final Set<String> allowedDomains;

    public CorporateEmailPolicy(Collection<String> allowedDomains) {
        this.allowedDomains = allowedDomains.stream()
                .map(d -> d.trim().toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
    }

    /** Returns the normalized (trimmed, lower-case) email or throws if it is not a valid corporate email. */
    public String validate(String email) {
        if (email == null || !FORMAT.matcher(email.trim()).matches()) {
            throw new InvalidCorporateEmailException(email);
        }
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        String domain = normalized.substring(normalized.indexOf('@') + 1);
        if (!allowedDomains.contains(domain)) {
            throw new InvalidCorporateEmailException(email);
        }
        return normalized;
    }
}
