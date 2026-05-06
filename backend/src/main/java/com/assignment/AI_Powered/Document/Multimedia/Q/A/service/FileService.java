package com.assignment.AI_Powered.Document.Multimedia.Q.A.service;

import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.FileResponseDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.entity.FileMetaData;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.mapper.FileMapper;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.repository.FileMetaDataRepository;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.repository.UserRepository;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.util.PdfUtil;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.util.TranscriptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileMetaDataRepository fileMetaDataRepository;
    private final UserRepository userRepository;
    private final PdfUtil pdfUtil;
    private final TranscriptionUtil transcriptionUtil;
    private final FileMapper fileMapper;

    public FileResponseDTO uploadFile(MultipartFile file, Long userId) throws Exception{
        validateFile(file);

        String fileType = resolveFileType(file.getContentType(), file.getOriginalFilename());
        System.out.println("Uploading file: " + file.getOriginalFilename() + " | Detected type: " + fileType);
        
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        String uploadDir = "uploads/";
        Path path = Paths.get(uploadDir, fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        String extractedText = null;
        String summary = null;
        if(fileType.equals("PDF"))
        {
            extractedText = pdfUtil.extractText(path.toFile());
        }
        else if(fileType.equals("AUDIO") || fileType.equals("VIDEO"))
        {
            System.out.println("Starting transcription for: " + file.getOriginalFilename());
            extractedText = transcriptionUtil.transcribe(path.toFile());
        }

        if (extractedText != null && !extractedText.isEmpty()) {
            System.out.println("Generating summary for: " + file.getOriginalFilename());
            summary = transcriptionUtil.getSummary(extractedText);
        }

        FileMetaData fileMetaData = new FileMetaData();
        fileMetaData.setFileName(file.getOriginalFilename());
        fileMetaData.setFileType(FileMetaData.FileType.valueOf(fileType));
        fileMetaData.setFilePath(path.toString());
        fileMetaData.setFileSize(file.getSize());
        fileMetaData.setExtractedText(extractedText);
        fileMetaData.setSummary(summary);
        fileMetaData.setUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));

        fileMetaDataRepository.save(fileMetaData);
        System.out.println("File saved to DB. Extracted text length: " + (extractedText != null ? extractedText.length() : 0));
        return fileMapper.mapToDTO(fileMetaData);
    }

    public void validateFile(MultipartFile file)
    {
        if(file.isEmpty())
        {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        String fileType = contentType != null ? contentType.toLowerCase() : "";
        String fileName = file.getOriginalFilename();

        if(fileType.equals("application/octet-stream") || fileType.isEmpty())
        {
            if(fileName != null && fileName.toLowerCase().endsWith(".pdf")) return;
            if(fileName != null && (fileName.toLowerCase().endsWith(".mp3") || fileName.toLowerCase().endsWith(".wav"))) return;
            if(fileName != null && (fileName.toLowerCase().endsWith(".mp4") || fileName.toLowerCase().endsWith(".avi"))) return;

            throw new IllegalArgumentException("Unsupported file type: " + fileName);
        }

        if(!fileType.contains("pdf") && !fileType.contains("video") && !fileType.contains("audio"))
        {
            throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }
    }

    public String resolveFileType(String contentType, String fileName)
    {
        String type = contentType != null ? contentType.toLowerCase() : "";
        String name = fileName != null ? fileName.toLowerCase() : "";
        
        if(type.contains("pdf") || name.endsWith(".pdf")) return "PDF";
        else if(type.contains("audio") || name.endsWith(".mp3") || name.endsWith(".wav")) return "AUDIO";
        else if(type.contains("video") || name.endsWith(".mp4") || name.endsWith(".avi")) return "VIDEO";
        else return "OTHER";
    }

    public java.util.List<FileResponseDTO> getAllFiles(Long userId) {
        return fileMetaDataRepository.findByUserUserId(userId)
                .stream()
                .map(fileMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public void deleteFile(Long fileId, Long userId) {
        FileMetaData file = fileMetaDataRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!file.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this file");
        }

        try {
            Files.deleteIfExists(Paths.get(file.getFilePath()));
        } catch (Exception e) {
            System.err.println("Failed to delete physical file: " + e.getMessage());
        }

        fileMetaDataRepository.delete(file);
    }

    public FileMetaData getFileById(Long id) {
        return fileMetaDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));
    }
}
