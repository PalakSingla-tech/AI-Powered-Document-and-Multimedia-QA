package com.assignment.AI_Powered.Document.Multimedia.Q.A.repository;

import com.assignment.AI_Powered.Document.Multimedia.Q.A.entity.FileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileMetaDataRepository extends JpaRepository<FileMetaData, Long> {
    List<FileMetaData> findByUserUserId(Long userId);
}
