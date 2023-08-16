package com.numble.mybox.file.dto;

public record FileResponse(
        Long id,
        Long folderId,
        Long userId,
        String oriName,
        Long size,
        String fileType
) {
}
