package com.assignment.AI_Powered.Document.Multimedia.Q.A.security;


import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.LoginRequestDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.LoginResponseDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.SignUpRequestDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.mapper.UserMapper;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.entity.User;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final AuthUtil authUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public LoginResponseDTO login(@Valid LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(),
                        loginRequestDTO.getPassword()));

        User user = (User) authentication.getPrincipal();

        String token = authUtil.generateAccessToken(user);
        
        return new LoginResponseDTO(token, user.getUserId());
    }

    public String signup(SignUpRequestDTO signupRequestDTO) {
        User user = (User) userRepository.findByUsername(signupRequestDTO.getUsername()).orElse(null);

        if(user != null) throw new IllegalArgumentException("User already exists with username " + signupRequestDTO.getUsername());
        user = userMapper.mapToEntity(signupRequestDTO);
        user.setPassword(passwordEncoder.encode(signupRequestDTO.getPassword()));

        userRepository.save(user);

        return "User registered successfully";

    }


}
