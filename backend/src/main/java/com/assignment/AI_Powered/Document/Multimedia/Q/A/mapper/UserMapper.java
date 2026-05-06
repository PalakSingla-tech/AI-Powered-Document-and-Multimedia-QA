package com.assignment.AI_Powered.Document.Multimedia.Q.A.mapper;

import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.SignUpRequestDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User mapToEntity(SignUpRequestDTO signUpRequestDTO)
    {
        return User.builder()
                .username(signUpRequestDTO.getUsername())
                .password(signUpRequestDTO.getPassword())
                .email(signUpRequestDTO.getEmail())
                .build();
    }
}
