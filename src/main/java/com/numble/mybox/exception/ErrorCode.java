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
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
