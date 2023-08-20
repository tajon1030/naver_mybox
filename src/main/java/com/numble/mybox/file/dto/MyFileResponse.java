package com.numble.mybox.file.dto;

public record MyFileResponse(
        Long id,
        Long folderId,
        Long userId,
        String oriName,
        Long size,
        String fileType
) {
}
