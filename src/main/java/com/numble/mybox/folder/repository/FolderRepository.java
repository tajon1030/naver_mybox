package com.numble.mybox.folder.repository;

import com.numble.mybox.folder.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    Optional<Folder> findByIdAndUserId(Long id, Long userId);
}
