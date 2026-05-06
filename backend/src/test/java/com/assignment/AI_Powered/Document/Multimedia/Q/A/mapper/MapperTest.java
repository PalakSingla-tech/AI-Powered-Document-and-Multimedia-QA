package com.assignment.AI_Powered.Document.Multimedia.Q.A.mapper;

import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.FileResponseDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.SignUpRequestDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.entity.FileMetaData;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapperTest {
    private final FileMapper fileMapper = new FileMapper();
    private final UserMapper userMapper = new UserMapper();
    @Test
    void testFileMapping() {
        FileMetaData entity = new FileMetaData();
        entity.setFileName("test.pdf");
        entity.setFileType(FileMetaData.FileType.PDF);

        FileResponseDTO dto = fileMapper.mapToDTO(entity);
        assertEquals("test.pdf", dto.getFileName());
    }
    @Test
    void testUserMapping() {
        SignUpRequestDTO dto = new SignUpRequestDTO();
        dto.setUsername("john");
        dto.setEmail("john@example.com");

        User user = userMapper.mapToEntity(dto);
        assertEquals("john", user.getUsername());
        assertEquals("john@example.com", user.getEmail());
    }
}
