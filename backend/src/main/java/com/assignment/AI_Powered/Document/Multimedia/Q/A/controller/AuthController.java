package com.assignment.AI_Powered.Document.Multimedia.Q.A.controller;


import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.LoginRequestDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.LoginResponseDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.SignUpRequestDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.entity.User;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.security.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5175")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authService.login(loginRequestDTO));
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignUpRequestDTO signUpRequestDTO) {
        return ResponseEntity.ok(authService.signup(signUpRequestDTO));
    }


}
