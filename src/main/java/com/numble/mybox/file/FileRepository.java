package com.numble.mybox.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

    List<File> findByFolderIdAndUserId(Long folderId, Long userId);

    void deleteByFolderId(Long folderId);
}
