package com.assignment.AI_Powered.Document.Multimedia.Q.A.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TranscriptionUtilTest {

    @Mock private RestTemplate restTemplate;
    @InjectMocks private TranscriptionUtil transcriptionUtil;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(transcriptionUtil, "apiKey", "test-key");
        ReflectionTestUtils.setField(transcriptionUtil, "apiKey1", "test-key-1");
    }

    @Test
    void testTranscribe_ReturnsFormattedTimestamps() throws IOException {
        Map<String, Object> mockResponse = Map.of(
            "results", Map.of(
                "channels", List.of(Map.of("alternatives", List.of(Map.of("transcript", "hello")))),
                "utterances", List.of(Map.of("start", 10.0, "end", 15.0, "transcript", "hello world"))
            )
        );

        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
            .thenReturn(ResponseEntity.ok(mockResponse));

        Path tempFile = Files.createTempFile("test", ".mp3");
        try {
            String result = transcriptionUtil.transcribe(tempFile.toFile());

            assertNotNull(result);
            assertTrue(result.contains("[00:10-00:15]"));
            assertTrue(result.contains("hello world"));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void testTranscribe_NullUtterances_ReturnsTranscript() throws IOException {
        Map<String, Object> mockResponse = Map.of(
            "results", Map.of(
                "channels", List.of(Map.of("alternatives", List.of(Map.of("transcript", "just text"))))
            )
        );

        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
            .thenReturn(ResponseEntity.ok(mockResponse));

        Path tempFile = Files.createTempFile("test", ".mp3");
        try {
            String result = transcriptionUtil.transcribe(tempFile.toFile());
            assertEquals("just text", result);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void testTranscribe_ApiError_ReturnsNull() throws IOException {
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
            .thenThrow(new RuntimeException("API Down"));

        Path tempFile = Files.createTempFile("test", ".mp3");
        try {
            String result = transcriptionUtil.transcribe(tempFile.toFile());
            assertNull(result);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void testGetSummary_ReturnsText() {
        Map<String, Object> mockResponse = Map.of(
            "choices", List.of(Map.of("message", Map.of("content", "This is a summary")))
        );

        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
            .thenReturn(ResponseEntity.ok(mockResponse));

        String summary = transcriptionUtil.getSummary("Some transcript");

        assertEquals("This is a summary", summary);
    }

    @Test
    void testGetSummary_EmptyText_ReturnsMessage() {
        String summary = transcriptionUtil.getSummary("");
        assertTrue(summary.contains("couldn't find any transcribed content"));
    }

    @Test
    void testGetSummary_ApiError_ReturnsErrorMessage() {
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
            .thenThrow(new RuntimeException("Groq Error"));

        String summary = transcriptionUtil.getSummary("Some transcript");
        assertTrue(summary.contains("encountered an error"));
    }
}
