package com.assignment.AI_Powered.Document.Multimedia.Q.A.util;

import com.assignment.AI_Powered.Document.Multimedia.Q.A.entity.User;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.security.AuthUtil;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class AuthUtilTest {
    private final AuthUtil authUtil = new AuthUtil();
    @Test
    void testJwtLogic() {
        ReflectionTestUtils.setField(authUtil, "jwtSecretKey", "my-very-long-secret-key-for-testing-purposes-123456");
        User user = new User();
        user.setUsername("test");

        String token = authUtil.generateAccessToken(user);
        assertNotNull(token);
        assertEquals("test", authUtil.getUsernameFromToken(token));
        assertTrue(authUtil.validate(token));
    }
}
