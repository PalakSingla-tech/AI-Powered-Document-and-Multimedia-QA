package com.assignment.AI_Powered.Document.Multimedia.Q.A.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class FileMetaData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    private String fileName;
    private String filePath;

    public enum FileType
    {
        PDF,
        VIDEO,
        AUDIO
    }

    @Enumerated(EnumType.STRING)
    private FileType fileType;

    private Long fileSize;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String extractedText;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String summary;

}
