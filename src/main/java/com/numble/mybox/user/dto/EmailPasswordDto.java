package com.numble.mybox.user.dto;

import jakarta.validation.constraints.Email;

public record EmailPasswordDto(
        @Email
        String email,
        String password
) {
}
