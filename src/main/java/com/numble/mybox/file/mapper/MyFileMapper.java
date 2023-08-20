package com.numble.mybox.file.mapper;

import com.numble.mybox.file.MyFile;
import com.numble.mybox.file.dto.MyFileResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MyFileMapper {
    MyFileResponse toMyFileResponse(MyFile myFile);
}
