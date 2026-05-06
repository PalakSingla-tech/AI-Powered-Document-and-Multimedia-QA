package com.assignment.AI_Powered.Document.Multimedia.Q.A.security;

import com.assignment.AI_Powered.Document.Multimedia.Q.A.entity.User;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private CustomUserDetailsService customUserDetailsService;

    @Test
    void testLoadUserByUsername_Success() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        
        UserDetails result = customUserDetailsService.loadUserByUsername("testuser");
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testLoadUserByUsername_NotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("unknown"));
    }
}
