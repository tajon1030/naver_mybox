package com.numble.mybox.file.mapper;

import com.numble.mybox.file.File;
import com.numble.mybox.file.dto.FileResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMapper {
    FileResponse toFileResponse(File file);
}
