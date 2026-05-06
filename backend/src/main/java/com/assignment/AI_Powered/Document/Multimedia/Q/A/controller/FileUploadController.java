package com.assignment.AI_Powered.Document.Multimedia.Q.A.controller;

import com.assignment.AI_Powered.Document.Multimedia.Q.A.dto.FileResponseDTO;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.entity.FileMetaData;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.service.FileService;
import com.assignment.AI_Powered.Document.Multimedia.Q.A.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5175")
@RequestMapping("/api/files")
public class FileUploadController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<FileResponseDTO> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        Long userId = getUserIdFromContext();
        return ResponseEntity.ok(fileService.uploadFile(file, userId));
    }

    @GetMapping
    public ResponseEntity<List<FileResponseDTO>> getAllFiles() {
        Long userId = getUserIdFromContext();
        return ResponseEntity.ok(fileService.getAllFiles(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        Long userId = getUserIdFromContext();
        fileService.deleteFile(id, userId);
        return ResponseEntity.ok().build();
    }

    private Long getUserIdFromContext() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            return ((User) principal).getUserId();
        }
        return 1L; 
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<Resource> viewFile(@PathVariable Long id) {
        FileMetaData file = fileService.getFileById(id);
        
        try {
            Path path = Paths.get(file.getFilePath());
            Resource resource = new UrlResource(path.toUri());
            
            String contentType = file.getFileType() == FileMetaData.FileType.PDF ? "application/pdf" : 
                                 file.getFileType() == FileMetaData.FileType.VIDEO ? "video/mp4" : "audio/mpeg";

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFileName() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
