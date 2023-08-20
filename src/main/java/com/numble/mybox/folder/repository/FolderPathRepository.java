package com.numble.mybox.folder.repository;

import com.numble.mybox.folder.entity.Folder;
import com.numble.mybox.folder.entity.FolderPath;
import com.numble.mybox.folder.entity.FolderPathId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

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

    @Query(value = """
            SELECT f
            FROM FolderPath fp
            JOIN Folder f
            ON fp.folderPathId.descendant = f.id
            WHERE fp.folderPathId.ancestor = :ancestor
            AND fp.depth = coalesce(:depth, fp.depth)
            ORDER BY fp.depth desc, f.id
            """)
    List<Folder> findByAncestorAndDepth(Long ancestor, Long depth);


    @Query(value = """
            SELECT f
            FROM FolderPath fp
            JOIN Folder f
            ON fp.folderPathId.ancestor = f.id
            WHERE fp.folderPathId.descendant = :descendant
            AND f.user.id = :userId
            ORDER BY fp.depth desc, f.id
            """)
    List<Folder> findByDescendantAndUserId(Long descendant, Long userId);

    @Modifying
    void deleteByFolderPathIdDescendant(Long descendant);

    @Query(value = """
            SELECT f.id, f.name, f.user_id as userId
            FROM folder_path fp
            JOIN folder f
            ON fp.descendant = f.id
            WHERE fp.ancestor = :parentFolderId
            AND fp.depth = 1
            AND f.name = :name
            LIMIT 1
            """, nativeQuery = true)
    Optional<Object> findChildFolderWithSameName(String name, Long parentFolderId);
}
