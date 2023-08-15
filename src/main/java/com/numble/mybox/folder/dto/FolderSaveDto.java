package com.numble.mybox.folder.dto;

public record FolderSaveDto(
        String name,
        Long parentFolderId
) {
}
