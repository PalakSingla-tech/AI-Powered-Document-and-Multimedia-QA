package com.assignment.AI_Powered.Document.Multimedia.Q.A.controller;

import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.ChatRequestDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ChatControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private ChatService chatService;

    @Test
    @WithMockUser
    public void testChat_Success() throws Exception {
        ChatRequestDTO request = new ChatRequestDTO();
        request.setFileId(1L);
        request.setMessage("What is in this file?");

        when(chatService.getChatResponse(anyLong(), anyString())).thenReturn("This is the AI response.");

        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("This is the AI response."));
    }

    @Test
    public void testChat_Unauthorized() throws Exception {
        ChatRequestDTO request = new ChatRequestDTO();
        request.setFileId(1L);
        request.setMessage("What is in this file?");

        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
