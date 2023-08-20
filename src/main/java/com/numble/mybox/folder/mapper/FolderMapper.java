package com.numble.mybox.folder.mapper;

import com.numble.mybox.file.MyFile;
import com.numble.mybox.file.mapper.MyFileMapper;
import com.numble.mybox.folder.dto.FolderFileListResponse;
import com.numble.mybox.folder.dto.FolderResponse;
import com.numble.mybox.folder.dto.FolderSaveDto;
import com.numble.mybox.folder.entity.Folder;
import com.numble.mybox.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FolderMapper {

    MyFileMapper MY_FILE_MAPPER = Mappers.getMapper(MyFileMapper.class);

    default Folder toFolder(FolderSaveDto dto, User user) {
        return new Folder(dto.name(), user);
    }

    FolderResponse toFolderResponse(Folder folder);

    default FolderFileListResponse toFolderFileResponse(List<Folder> folderList, List<MyFile> myFileList) {
        return new FolderFileListResponse(
                folderList.stream().map(this::toFolderResponse).toList(),
                myFileList.stream().map(MY_FILE_MAPPER::toMyFileResponse).toList());
    }

}
