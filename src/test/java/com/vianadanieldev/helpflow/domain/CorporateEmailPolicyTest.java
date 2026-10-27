package com.vianadanieldev.helpflow.domain;

import com.vianadanieldev.helpflow.domain.exception.InvalidCorporateEmailException;
import com.vianadanieldev.helpflow.domain.model.CorporateEmailPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorporateEmailPolicyTest {

    private final CorporateEmailPolicy policy = new CorporateEmailPolicy(List.of("helpflow.com"));

    @Test
    void normalizesValidCorporateEmail() {
        assertThat(policy.validate("  Ana.Perez@HelpFlow.com ")).isEqualTo("ana.perez@helpflow.com");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"not-an-email", "ana@gmail.com", "ana@helpflow", "@helpflow.com"})
    void rejectsInvalidOrNonCorporateEmail(String email) {
        assertThatThrownBy(() -> policy.validate(email)).isInstanceOf(InvalidCorporateEmailException.class);
    }
}
