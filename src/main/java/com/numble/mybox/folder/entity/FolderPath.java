package com.numble.mybox.folder.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FolderPath {
    @EmbeddedId
    private FolderPathId folderPathId;
    private Long depth;
}
