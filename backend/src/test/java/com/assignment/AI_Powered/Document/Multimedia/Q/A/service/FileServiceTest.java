package com.assignment.AI_Powered.Document.Multimedia.Q.A.service;

import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.FileResponseDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.entity.FileMetaData;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.entity.User;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.mapper.FileMapper;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.repository.FileMetaDataRepository;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.repository.UserRepository;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.util.TranscriptionUtil;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.util.PdfUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @Mock
    private FileMetaDataRepository fileMetaDataRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PdfUtil pdfUtil;
    @Mock
    private TranscriptionUtil transcriptionUtil;
    @Mock
    private FileMapper fileMapper;

    @InjectMocks
    private FileService fileService;

    @Test
    void testUploadAudioFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp3", "audio/mpeg", "test data".getBytes());
        User user = new User();
        user.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(transcriptionUtil.transcribe(any())).thenReturn("[00:00-00:05] Hello");
        when(transcriptionUtil.getSummary(any())).thenReturn("Summary");
        when(fileMapper.mapToDTO(any())).thenReturn(new FileResponseDTO());

        FileResponseDTO result = fileService.uploadFile(file, 1L);

        assertNotNull(result);
        verify(fileMetaDataRepository).save(any());
    }

    @Test
    void testGetFileById_NotFound() {
        when(fileMetaDataRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> fileService.getFileById(1L));
    }

    @Test
    void testGetFileById_Success() {
        FileMetaData file = new FileMetaData();
        file.setFileId(1L);
        when(fileMetaDataRepository.findById(1L)).thenReturn(Optional.of(file));

        FileMetaData result = fileService.getFileById(1L);
        assertEquals(1L, result.getFileId());
    }

    @Test
    void testDeleteFile_Success() {
        FileMetaData file = new FileMetaData();
        User user = new User();
        user.setUserId(1L);
        file.setUser(user);
        file.setFilePath("uploads/test.mp3");

        when(fileMetaDataRepository.findById(1L)).thenReturn(Optional.of(file));

        fileService.deleteFile(1L, 1L);
        verify(fileMetaDataRepository).delete(file);
    }

    @Test
    void testUploadPdfFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "test data".getBytes());
        User user = new User();
        user.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(fileMapper.mapToDTO(any())).thenReturn(new FileResponseDTO());

        FileResponseDTO result = fileService.uploadFile(file, 1L);

        assertNotNull(result);
        verify(fileMetaDataRepository).save(any());
    }

    @Test
    void testUploadVideoFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", "test data".getBytes());
        User user = new User();
        user.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(transcriptionUtil.transcribe(any())).thenReturn("[00:00-00:05] Video start");
        when(transcriptionUtil.getSummary(any())).thenReturn("Video Summary");
        when(fileMapper.mapToDTO(any())).thenReturn(new FileResponseDTO());

        FileResponseDTO result = fileService.uploadFile(file, 1L);

        assertNotNull(result);
        verify(fileMetaDataRepository).save(any());
    }

    @Test
    void testUploadFile_UserNotFound() {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp3", "audio/mpeg", "test data".getBytes());
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> fileService.uploadFile(file, 1L));
    }

    @Test
    void testDeleteFile_Unauthorized() {
        FileMetaData file = new FileMetaData();
        User owner = new User();
        owner.setUserId(1L);
        file.setUser(owner);

        when(fileMetaDataRepository.findById(1L)).thenReturn(Optional.of(file));

        assertThrows(RuntimeException.class, () -> fileService.deleteFile(1L, 2L));
    }

    @Test
    void testGetAllFiles() {
        fileService.getAllFiles(1L);
        verify(fileMetaDataRepository).findByUserUserId(1L);
    }

    @Test
    void testUploadFile_EmptyFileName() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "", "text/plain", "hello".getBytes());
        User user = new User();
        user.setUserId(1L);

        assertThrows(IllegalArgumentException.class, () -> fileService.uploadFile(file, 1L));
    }

    @Test
    void testValidateFile_UnsupportedType() {
        MockMultipartFile file = new MockMultipartFile("file", "test.exe", "application/x-msdownload",
                "hello".getBytes());
        assertThrows(IllegalArgumentException.class, () -> fileService.validateFile(file));
    }

    @Test
    void testValidateFile_OctetStream_Pdf() {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/octet-stream",
                "hello".getBytes());
        assertDoesNotThrow(() -> fileService.validateFile(file));
    }

    @Test
    void testValidateFile_OctetStream_Mp3() {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp3", "application/octet-stream",
                "hello".getBytes());
        assertDoesNotThrow(() -> fileService.validateFile(file));
    }

    @Test
    void testResolveFileType_Audio() {
        assertEquals("AUDIO", fileService.resolveFileType("audio/mpeg", "test.mp3"));
        assertEquals("AUDIO", fileService.resolveFileType("", "test.wav"));
    }

    @Test
    void testResolveFileType_Video() {
        assertEquals("VIDEO", fileService.resolveFileType("video/mp4", "test.mp4"));
        assertEquals("VIDEO", fileService.resolveFileType("", "test.avi"));
    }

    @Test
    void testResolveFileType_Other() {
        assertEquals("OTHER", fileService.resolveFileType("text/plain", "test.txt"));
    }

    @Test
    void testDeleteFile_PhysicalDeleteFailure() throws IOException {
        FileMetaData file = new FileMetaData();
        file.setFileId(1L);
        file.setFilePath("non-existent-path");
        User user = new User();
        user.setUserId(1L);
        file.setUser(user);

        when(fileMetaDataRepository.findById(1L)).thenReturn(Optional.of(file));

        assertDoesNotThrow(() -> fileService.deleteFile(1L, 1L));
        verify(fileMetaDataRepository).delete(file);
    }

    @Test
    void testValidateFile_OctetStream_Wav() {
        MockMultipartFile file = new MockMultipartFile("file", "test.wav", "application/octet-stream", "hello".getBytes());
        assertDoesNotThrow(() -> fileService.validateFile(file));
    }

    @Test
    void testValidateFile_OctetStream_Mp4() {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "application/octet-stream", "hello".getBytes());
        assertDoesNotThrow(() -> fileService.validateFile(file));
    }

    @Test
    void testValidateFile_OctetStream_Avi() {
        MockMultipartFile file = new MockMultipartFile("file", "test.avi", "application/octet-stream", "hello".getBytes());
        assertDoesNotThrow(() -> fileService.validateFile(file));
    }

    @Test
    void testResolveFileType_Combinations() {
        assertEquals("PDF", fileService.resolveFileType("application/pdf", ""));
        assertEquals("PDF", fileService.resolveFileType("", "test.pdf"));
        assertEquals("AUDIO", fileService.resolveFileType("audio/wav", ""));
        assertEquals("VIDEO", fileService.resolveFileType("video/x-msvideo", ""));
    }

    @Test
    void testDeleteFile_FileIdNotFound() {
        when(fileMetaDataRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> fileService.deleteFile(1L, 1L));
    }
}
