package com.numble.mybox.folder.dto;

import com.numble.mybox.file.dto.MyFileResponse;

import java.util.List;

public record FolderFileListResponse(
        List<FolderResponse> childFolderList,
        List<MyFileResponse> fileList
) {
}
