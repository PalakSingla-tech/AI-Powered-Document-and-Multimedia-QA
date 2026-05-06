package com.assignment.AI_Powered.Document.Multimedia.Q.A.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthFilterTest {

    @Mock private CustomUserDetailsService customUserDetailsService;
    @Mock private AuthUtil authUtil;
    @Mock private FilterChain filterChain;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;

    @InjectMocks private JwtAuthFilter jwtAuthFilter;

    @Test
    void testDoFilterInternal_NoToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        jwtAuthFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_ValidToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(authUtil.getUsernameFromToken("valid-token")).thenReturn("testuser");
        lenient().when(customUserDetailsService.loadUserByUsername("testuser")).thenReturn(mock(UserDetails.class));

        jwtAuthFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(authUtil.getUsernameFromToken("invalid-token")).thenThrow(new RuntimeException("Invalid"));

        jwtAuthFilter.doFilterInternal(request, response, filterChain);
        verify(response).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED), anyString());
    }
}
