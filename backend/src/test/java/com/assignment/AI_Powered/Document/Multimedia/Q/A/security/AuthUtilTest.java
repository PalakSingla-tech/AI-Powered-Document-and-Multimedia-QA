package com.assignment.AI_Powered.Document.Multimedia.Q.A.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class AuthUtilTest {

    private AuthUtil authUtil;

    @BeforeEach
    void setUp() {
        authUtil = new AuthUtil();
        ReflectionTestUtils.setField(authUtil, "jwtSecretKey", "this-is-a-very-long-secret-key-that-is-at-least-32-chars");
    }

    @Test
    void testGenerateAndValidateToken() {
        User user = new User("testuser", "password", Collections.emptyList());
        String token = authUtil.generateAccessToken(user);
        
        assertNotNull(token);
        assertTrue(authUtil.validate(token));
        assertEquals("testuser", authUtil.getUsernameFromToken(token));
    }

    @Test
    void testValidateInvalidToken() {
        assertFalse(authUtil.validate("invalid-token"));
    }

    @Test
    void testValidate_Exception() {
        assertFalse(authUtil.validate(null));
    }
}
