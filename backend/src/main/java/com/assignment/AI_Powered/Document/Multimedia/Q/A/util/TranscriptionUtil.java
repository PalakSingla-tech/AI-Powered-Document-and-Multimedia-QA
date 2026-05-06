package com.assignment.AI_Powered.Document.Multimedia.Q.A.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.List;

@Component
@lombok.RequiredArgsConstructor
public class TranscriptionUtil {

    private final RestTemplate restTemplate;

    @Value("${deepgram.api.key}")
    private String apiKey;

    private final String DEEPGRAM_API_URL = "https://api.deepgram.com/v1/listen?model=nova-2&smart_format=true&punctuate=true&utterances=true&language=en";

    @Value("${groq.api.key}")
    private String apiKey1;

    private final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";

    public String transcribe(File file) throws IOException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set("Authorization", "Token " + apiKey.trim());

            byte[] fileContent = Files.readAllBytes(file.toPath());
            HttpEntity<byte[]> requestEntity = new HttpEntity<>(fileContent, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(DEEPGRAM_API_URL, requestEntity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map results = (Map) response.getBody().get("results");
                List channels = (List) results.get("channels");
                Map firstChannel = (Map) channels.get(0);
                List alternatives = (List) firstChannel.get("alternatives");
                Map firstAlternative = (Map) alternatives.get(0);
                
                String transcript = (String) firstAlternative.get("transcript");

                // EXTRACT UTTERANCES FOR TIMESTAMPS
                StringBuilder timestamps = new StringBuilder();
                List utterances = (List) results.get("utterances");
                if (utterances != null) {
                    for (Object obj : utterances) {
                        Map utterance = (Map) obj;
                        double start = ((Number) utterance.get("start")).doubleValue();
                        double end = ((Number) utterance.get("end")).doubleValue();
                        String text = (String) utterance.get("transcript");
                        
                        int startMin = (int) start / 60;
                        int startSec = (int) start % 60;
                        int endMin = (int) end / 60;
                        int endSec = (int) end % 60;
                        
                        timestamps.append(String.format("[%02d:%02d-%02d:%02d] %s\n", 
                                startMin, startSec, endMin, endSec, text));
                    }
                }

                return timestamps.length() > 0 ? timestamps.toString() : transcript;
            }
        } catch (Exception e) {
            System.err.println("Deepgram API Error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public String getSummary(String extractedText) {
        if (extractedText == null || extractedText.isEmpty()) {
            return "I couldn't find any transcribed content for this file. Please ensure the file was processed correctly.";
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey1.trim());

        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(
                        Map.of("role", "system", "content", "You are an expert summarizer. Summarize this content in 3 bullet points."),
                        Map.of("role", "user", "content", extractedText)
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
