package com.numble.mybox.folder.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class FolderPathId implements Serializable {
    private Long ancestor;
    private Long descendant;
}
