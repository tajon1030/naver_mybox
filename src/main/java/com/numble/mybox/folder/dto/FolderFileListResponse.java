package com.numble.mybox.folder.dto;

import com.numble.mybox.file.dto.FileResponse;

import java.util.List;

public record FolderFileListResponse(
        List<FolderResponse> childFolderList,
        List<FileResponse> fileList
) {
}
