package com.numble.mybox.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    DUPLICATED_LOGIN_ID(HttpStatus.CONFLICT, "MEMBER-ERR-409", "loginId is duplicated"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-ERR-404", "user not founded"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "MEMBER-ERR-401", "Password is invalid"),
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "MEMBER-ERR-401", "Permission is invalid"),
    DISK_OUT_OF_SPACE(HttpStatus.OK, "MEMBER-ERR-413", "Disk out of space"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-ERR-500", "Internal server error"),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE-ERR-404", "file not founded"),
    FOLDER_NOT_FOUND(HttpStatus.NOT_FOUND, "FOLDER-ERR-404", "folder not founded"),
    DUPLICATED_NAME(HttpStatus.BAD_REQUEST, "RESOURCE-ERR-404", "same name folder or file exists");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
