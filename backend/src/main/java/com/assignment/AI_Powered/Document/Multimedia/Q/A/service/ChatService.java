package com.assignment.AI_Powered.Document.Multimedia.Q.A.service;

import com.assignment.AI_Powered.Document.Multimedia.Q.A.entity.FileMetaData;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.repository.FileMetaDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final FileMetaDataRepository fileMetaDataRepository;
    private final RestTemplate restTemplate;
    
    @Value("${groq.api.key}")
    private String apiKey;

    private final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";

    public String getChatResponse(Long fileId, String userMessage) {
        FileMetaData fileData = fileMetaDataRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        String context = fileData.getExtractedText();
        if (context == null || context.isEmpty()) {
            return "I couldn't find any transcribed content for this file. Please ensure the file was processed correctly.";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey.trim());

        Map<String, Object> requestBody = Map.of(
            "model", "llama-3.3-70b-versatile",
            "messages", List.of(
                Map.of("role", "system", "content", "You are an AI assistant analyzing a document or multimedia file. Use the following extracted text as your only context. \n\nIMPORTANT: If the file is audio or video, the text contains timestamps like [mm:ss]. You MUST include the relevant timestamp in your answer (e.g., 'At [00:15], the speaker mentioned...') so the user can play that portion. If the answer is not in the text, politely say so.\n\nContext:\n" + context),
                Map.of("role", "user", "content", userMessage)
            )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(GROQ_API_URL, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List choices = (List) response.getBody().get("choices");
                Map firstChoice = (Map) choices.get(0);
                Map message = (Map) firstChoice.get("message");
                return (String) message.get("content");
            }
        } catch (Exception e) {
            return "Sorry, I encountered an error while communicating with the AI service: " + e.getMessage();
        }

        return "I'm sorry, I couldn't generate a response at this moment.";
    }
}
