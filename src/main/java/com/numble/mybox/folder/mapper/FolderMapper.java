package com.numble.mybox.folder.mapper;

import com.numble.mybox.folder.dto.FolderResponse;
import com.numble.mybox.folder.dto.FolderSaveDto;
import com.numble.mybox.folder.entity.Folder;
import com.numble.mybox.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FolderMapper {

    default Folder toFolder(FolderSaveDto dto, User user) {
        return new Folder(dto.name(), user);
    }

    FolderResponse toFolderResponse(Folder folder);
}
