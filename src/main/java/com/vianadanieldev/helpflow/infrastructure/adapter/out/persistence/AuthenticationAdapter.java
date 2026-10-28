package com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence;

import com.vianadanieldev.helpflow.application.port.out.AuthenticationPort;
import com.vianadanieldev.helpflow.domain.model.Role;
import com.vianadanieldev.helpflow.infrastructure.adapter.in.web.security.JwtTokenProvider;
import com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.repository.JpaTechnicianRepository;
import com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthenticationAdapter implements AuthenticationPort {

    private final JpaUserRepository userRepository;
    private final JpaTechnicianRepository technicianRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Optional<String> authenticate(String corporateEmail, String rawPassword) {
        var userOpt = userRepository.findByCorporateEmail(corporateEmail);
        if (userOpt.isPresent()) {
            if (passwordEncoder.matches(rawPassword, userOpt.get().getPassword())) {
                return Optional.of(jwtTokenProvider.createToken(corporateEmail, List.of("ROLE_USUARIO")));
            }
        }

        var techOpt = technicianRepository.findByCorporateEmail(corporateEmail);
        if (techOpt.isPresent()) {
            if (passwordEncoder.matches(rawPassword, techOpt.get().getPassword())) {
                return Optional.of(jwtTokenProvider.createToken(corporateEmail, List.of("ROLE_TECNICO")));
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<AuthenticatedUser> currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }

        String email = (String) auth.getPrincipal();
        Set<Role> roles = auth.getAuthorities().stream()
                .map(a -> Role.valueOf(a.getAuthority().replace("ROLE_", "")))
                .collect(Collectors.toSet());

        UUID userId = userRepository.findByCorporateEmail(email)
                .map(u -> u.getId())
                .orElseGet(() -> technicianRepository.findByCorporateEmail(email)
                        .map(t -> t.getId())
                        .orElse(null));

        if (userId == null) return Optional.empty();

        return Optional.of(new AuthenticatedUser(userId, email, roles));
    }
}