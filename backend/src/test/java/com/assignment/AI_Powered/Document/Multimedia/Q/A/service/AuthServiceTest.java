package com.assignment.AI_Powered.Document.Multimedia.Q.A.service;

import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.LoginRequestDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.LoginResponseDTO;

import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.SignUpRequestDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.entity.User;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.mapper.UserMapper;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.repository.UserRepository;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.security.AuthService;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.security.AuthUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthUtil authUtil;
    @Mock private AuthenticationManager authenticationManager;
    @InjectMocks private AuthService authService;
    @Mock private UserMapper userMapper;

    @Test
    void testSignup_Success()
    {
        SignUpRequestDTO dto = new SignUpRequestDTO();
        dto.setEmail("test@gmail.com");
        dto.setUsername("test");
        dto.setPassword("pass");

        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userMapper.mapToEntity(any())).thenReturn(new User());
        when(passwordEncoder.encode(any())).thenReturn("hashed");

        String result = authService.signup(dto);
        assertEquals("User registered successfully", result);
    }

    @Test
    void testLogin_Success()
    {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsername("testuser");
        dto.setPassword("pass");

        User user = new User();
        user.setUsername("testuser");
        user.setUserId(1L);

        Authentication auth = mock(Authentication.class);

        when(auth.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(authUtil.generateAccessToken(any())).thenReturn("mockJWT");

        LoginResponseDTO loginResponseDTO = authService.login(dto);
        assertEquals("mockJWT", loginResponseDTO.getJwt());
    }

    @Test
    void testSignup_Failure_DuplicateUser() {
        SignUpRequestDTO dto = new SignUpRequestDTO();
        dto.setUsername("test");
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(new User()));
        
        try {
            authService.signup(dto);
        } catch (IllegalArgumentException e) {
            assertEquals("User already exists with username test", e.getMessage());
        }
    }


}
