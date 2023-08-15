package com.numble.mybox.folder;

import com.numble.mybox.folder.dto.FolderResponse;
import com.numble.mybox.folder.dto.FolderSaveDto;
import com.numble.mybox.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FolderMapper {

    default Folder toFolder(FolderSaveDto dto, User user) {
        Folder folder = new Folder();
        folder.setName(dto.name());

        if (dto.parentFolderId() != null) {
            Folder parentFolder = new Folder();
            parentFolder.setId(dto.parentFolderId());
        }

        folder.setUser(user);
        return folder;
    }

    FolderResponse toFolderResponse(Folder folder);
}
