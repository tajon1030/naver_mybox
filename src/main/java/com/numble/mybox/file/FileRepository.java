package com.numble.mybox.file;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<MyFile, Long> {

    List<MyFile> findByFolderIdAndUserId(Long folderId, Long userId);

    void deleteByFolderId(Long folderId);
}
