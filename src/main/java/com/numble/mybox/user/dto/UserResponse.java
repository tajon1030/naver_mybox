package com.numble.mybox.user.dto;

public record UserResponse(
        String email,
        Long unusedQuota
) {
}
