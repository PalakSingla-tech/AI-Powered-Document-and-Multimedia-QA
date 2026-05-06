package com.assignment.AI_Powered.Document.Multimedia.Q.A.controller;

import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.FileResponseDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.entity.FileMetaData;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@SpringBootTest
@AutoConfigureMockMvc
public class FileUploadControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private FileService fileService;

    @Test
    @WithMockUser
    public void testUploadFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.txt", "text/plain", "hello".getBytes());
        
        when(fileService.uploadFile(any(), anyLong())).thenReturn(new FileResponseDTO());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/files/upload")
                .file(file))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testGetAllFiles_Authorized() throws Exception {
        mockMvc.perform(get("/api/files"))
               .andExpect(status().isOk());
    }

    @Test
    public void testGetAllFiles_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/files"))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void testDeleteFile() throws Exception {
        mockMvc.perform(delete("/api/files/1")).andExpect(status().isOk());
    }

    @Test
    void testViewFile() throws Exception {
        Path tempFile = Files.createTempFile("test", ".pdf");

        try {
            FileMetaData meta = new FileMetaData();
            meta.setFileId(1L);
            meta.setFileName("test.pdf");
            meta.setFilePath(tempFile.toString());
            meta.setFileType(FileMetaData.FileType.PDF);
            when(fileService.getFileById(1L)).thenReturn(meta);

            mockMvc.perform(get("/api/files/view/1"))
                    .andExpect(status().isOk());
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void testViewFile_NotFound() throws Exception {
        FileMetaData meta = new FileMetaData();
        meta.setFilePath("invalid-path");
        when(fileService.getFileById(1L)).thenReturn(meta);

        mockMvc.perform(get("/api/files/view/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testViewFile_Audio() throws Exception {
        Path tempFile = Files.createTempFile("test", ".mp3");
        try {
            FileMetaData meta = new FileMetaData();
            meta.setFilePath(tempFile.toString());
            meta.setFileType(FileMetaData.FileType.AUDIO);
            when(fileService.getFileById(1L)).thenReturn(meta);

            mockMvc.perform(get("/api/files/view/1"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Type", "audio/mpeg"));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void testViewFile_Video() throws Exception {
        Path tempFile = Files.createTempFile("test", ".mp4");
        try {
            FileMetaData meta = new FileMetaData();
            meta.setFilePath(tempFile.toString());
            meta.setFileType(FileMetaData.FileType.VIDEO);
            when(fileService.getFileById(1L)).thenReturn(meta);

            mockMvc.perform(get("/api/files/view/1"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Type", "video/mp4"));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    @WithMockUser(username = "someone")
    void testGetUserIdFromContext_Fallback() throws Exception {
        mockMvc.perform(get("/api/files")).andExpect(status().isOk());
    }
}
