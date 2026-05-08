package com.khureka.server.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {
    @Schema(description = "이메일 주소", example = "elder@test.com")
    private String email;

    @Schema(description = "비밀번호", example = "password123")
    private String password;

    @Schema(description = "사용자 이름(성함)", example = "홍길동")
    private String username;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;

    @Schema(description = "신체적 특성(좌석 선호도)", example = "EYESIGHT", 
            allowableValues = {"NONE", "EYESIGHT", "LEG", "HEARING"})
    private com.khureka.server.domain.SeatPreference seatPreference;
}
