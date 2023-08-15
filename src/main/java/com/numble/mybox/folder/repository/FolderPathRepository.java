package com.numble.mybox.folder.repository;

import com.numble.mybox.folder.entity.FolderPath;
import com.numble.mybox.folder.entity.FolderPathId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface FolderPathRepository extends JpaRepository<FolderPath, FolderPathId> {

    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO folder_path (ancestor, descendant, depth)
            SELECT ancestor, ?1, depth+1
            FROM folder_path
            WHERE ?2 IS NOT NULL AND descendant = ?2
            UNION ALL
            SELECT ?1, ?1, 0
            """)
    void saveFolderPath(Long saveFolderId, Long parentFolderId);
}
