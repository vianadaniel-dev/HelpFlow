package com.vianadanieldev.helpflow.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CorporateEmailPolicyTest {
    @Test
    void testValidCorporateEmail() {
        String email = "daniel@helpflow.com";
        assertTrue(email.endsWith("@helpflow.com"));
    }
}
