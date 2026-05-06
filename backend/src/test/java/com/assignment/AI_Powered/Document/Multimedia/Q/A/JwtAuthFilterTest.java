package com.assignment.AI_Powered.Document.Multimedia.Q.A;

import com.assignment.AI_Powered.Document.Multimedia.Q.A.security.AuthUtil;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.security.CustomUserDetailsService;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.security.JwtAuthFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtAuthFilterTest {
    @Mock
    private AuthUtil authUtil;
    @Mock
    private CustomUserDetailsService userDetailsService;
    @Mock
    private FilterChain filterChain;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private UserDetails userDetails;
    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;
    @Test
    void testDoFilterInternal_WithValidToken() throws Exception {

        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(authUtil.getUsernameFromToken("valid-token")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(new ArrayList<>());

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
