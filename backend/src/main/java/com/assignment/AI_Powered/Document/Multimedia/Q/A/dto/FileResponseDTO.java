package com.assignment.AI_Powered.Document.Multimedia.Q.A.dto;

import com.assignment.AI_Powered.Document.Multimedia.Q.A.entity.FileMetaData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponseDTO {
    private Long fileId;
    private String fileName;
    private FileMetaData.FileType fileType;
    private Long fileSize;
    private String uploadedAt;
    private String summary;
    private String extractedText;
}
