package com.numble.mybox.folder.mapper;

import com.numble.mybox.file.File;
import com.numble.mybox.file.mapper.FileMapper;
import com.numble.mybox.folder.dto.FolderFileListResponse;
import com.numble.mybox.folder.dto.FolderResponse;
import com.numble.mybox.folder.dto.FolderSaveDto;
import com.numble.mybox.folder.entity.Folder;
import com.numble.mybox.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FolderMapper {

    FileMapper fileMapper = Mappers.getMapper(FileMapper.class);

    default Folder toFolder(FolderSaveDto dto, User user) {
        return new Folder(dto.name(), user);
    }

    FolderResponse toFolderResponse(Folder folder);

    default FolderFileListResponse toFolderFileResponse(List<Folder> folderList, List<File> fileList) {
        return new FolderFileListResponse(
                folderList.stream().map(this::toFolderResponse).toList(),
                fileList.stream().map(fileMapper::toFileResponse).toList());
    }

}
