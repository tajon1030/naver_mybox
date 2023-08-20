package com.numble.mybox.file;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<MyFile, Long> {

    List<MyFile> findByFolderIdAndUserId(Long folderId, Long userId);

    Optional<MyFile> findByUserIdAndId(Long userId, Long id);

    Optional<MyFile> findFirstByFolderIdAndOriName(Long folderId, String oriName);
}
