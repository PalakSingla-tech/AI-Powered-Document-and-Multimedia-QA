package com.assignment.AI_Powered.Document.Multimedia.Q.A;

import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.ChatRequestDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.ChatResponseDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.entity.FileMetaData;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoilerPlateTest {

    @Test
    void testEntityCoverage() {

        User user = new User();
        user.setUserId(1L);
        user.setUsername("bob");
        user.setPassword("secret");
        user.setEmail("bob@mail.com");
        assertEquals(1L, user.getUserId());
        assertEquals("bob", user.getUsername());

        FileMetaData meta = new FileMetaData();
        meta.setFileId(10L);
        meta.setFileName("recording.mp3");
        meta.setFileType(FileMetaData.FileType.AUDIO);
        meta.setExtractedText("hello");
        meta.setSummary("hi");
        assertEquals(10L, meta.getFileId());
        assertEquals(FileMetaData.FileType.AUDIO, meta.getFileType());

        ChatRequestDTO req = new ChatRequestDTO();
        req.setFileId(1L);
        req.setMessage("What was said?");
        assertEquals("What was said?", req.getMessage());
        ChatResponseDTO res = new ChatResponseDTO("AI Response");
        assertEquals("AI Response", res.getAnswer());
    }
}
