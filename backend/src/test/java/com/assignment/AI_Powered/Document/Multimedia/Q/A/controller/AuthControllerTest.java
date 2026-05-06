package com.assignment.AI_Powered.Document.Multimedia.Q.A.controller;


import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.LoginRequestDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.LoginResponseDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.entity.User;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.security.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private AuthService authService;

    @Test
    public void testLogin_Endpoint() throws Exception {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsername("test");
        dto.setPassword("pass");

        LoginResponseDTO mockResponse = new LoginResponseDTO("mockToken", 1L);
        when(authService.login(any())).thenReturn(mockResponse);
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto))).andExpect(status().isOk());
    }

}
