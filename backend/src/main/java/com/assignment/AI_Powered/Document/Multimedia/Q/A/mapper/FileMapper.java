package com.assignment.AI_Powered.Document.Multimedia.Q.A.mapper;

import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.FileResponseDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.entity.FileMetaData;
import org.springframework.stereotype.Component;

@Component
public class FileMapper {
    public FileResponseDTO mapToDTO(FileMetaData file)
    {
        return FileResponseDTO.builder()
                .fileId(file.getFileId())
                .fileName(file.getFileName())
                .fileType(file.getFileType())
                .fileSize(file.getFileSize())
                .uploadedAt(String.valueOf(file.getCreatedAt()))
                .summary(file.getSummary())
                .extractedText(file.getExtractedText())
                .build();
    }
}
