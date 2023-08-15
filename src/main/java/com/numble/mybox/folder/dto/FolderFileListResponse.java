package com.numble.mybox.folder.dto;

import java.util.List;

public record FolderFileListResponse(
        List<FolderResponse> childFolderList
) {
}
