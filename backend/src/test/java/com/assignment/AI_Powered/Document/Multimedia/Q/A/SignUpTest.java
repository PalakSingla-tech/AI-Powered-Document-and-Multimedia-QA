package com.assignment.AI_Powered.Document.Multimedia.Q.A;

import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.SignUpRequestDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.security.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SignUpTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AuthService authService;
    @Test
    void testSignup_Endpoint() throws Exception {
        SignUpRequestDTO dto = new SignUpRequestDTO();
        dto.setUsername("newuser");
        dto.setPassword("pass");
        dto.setEmail("new@mail.com");
        when(authService.signup(any())).thenReturn("User registered successfully");
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}
