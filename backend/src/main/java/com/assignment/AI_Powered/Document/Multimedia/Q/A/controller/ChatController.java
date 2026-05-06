package com.assignment.AI_Powered.Document.Multimedia.Q.A.controller;

import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.ChatRequestDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.ChatResponseDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5175")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponseDTO> chat(@RequestBody ChatRequestDTO request) {
        String answer = chatService.getChatResponse(request.getFileId(), request.getMessage());
        return ResponseEntity.ok(new ChatResponseDTO(answer));
    }
}
