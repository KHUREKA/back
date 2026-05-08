package com.khureka.server.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {
    @Schema(description = "이메일 주소", example = "user1@test.com")
    private String email;

    @Schema(description = "비밀번호", example = "password123")
    private String password;
}
