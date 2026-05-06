package com.assignment.AI_Powered.Document.Multimedia.Q.A.service;

import com.assignment.AI_Powered.Document.Multimedia.Q.A.entity.FileMetaData;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.repository.FileMetaDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {

    @Mock private FileMetaDataRepository fileMetaDataRepository;
    @Mock private RestTemplate restTemplate;
    @InjectMocks private ChatService chatService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(chatService, "apiKey", "test-key");
    }

    @Test
    void testGetChatResponse_Success() {
        FileMetaData file = new FileMetaData();
        file.setExtractedText("This is the file content.");
        when(fileMetaDataRepository.findById(1L)).thenReturn(Optional.of(file));

        Map<String, Object> mockResponse = Map.of(
            "choices", List.of(Map.of("message", Map.of("content", "AI Answer")))
        );
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
            .thenReturn(ResponseEntity.ok(mockResponse));

        String result = chatService.getChatResponse(1L, "What's in it?");
        assertEquals("AI Answer", result);
    }

    @Test
    void testGetChatResponse_FileNotFound() {
        when(fileMetaDataRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> chatService.getChatResponse(1L, "message"));
    }

    @Test
    void testGetChatResponse_NoContext() {
        FileMetaData file = new FileMetaData();
        file.setExtractedText("");
        when(fileMetaDataRepository.findById(1L)).thenReturn(Optional.of(file));

        String result = chatService.getChatResponse(1L, "message");
        assertTrue(result.contains("couldn't find any transcribed content"));
    }

    @Test
    void testGetChatResponse_ApiError() {
        FileMetaData file = new FileMetaData();
        file.setExtractedText("Context");
        when(fileMetaDataRepository.findById(1L)).thenReturn(Optional.of(file));

        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
            .thenThrow(new RuntimeException("Groq Error"));

        String result = chatService.getChatResponse(1L, "message");
        assertTrue(result.contains("encountered an error"));
    }
}
