package com.numble.mybox.file;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

    List<File> findByFolderIdAndUserId(Long folderId, Long userId);
}
