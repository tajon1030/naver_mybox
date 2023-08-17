package com.numble.mybox.user.mapper;

import com.numble.mybox.user.dto.EmailPasswordDto;
import com.numble.mybox.user.dto.UserResponse;
import com.numble.mybox.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "unusedQuota", ignore = true)
    User toUser(EmailPasswordDto dto);

    UserResponse toUserResponse(User user);
}
