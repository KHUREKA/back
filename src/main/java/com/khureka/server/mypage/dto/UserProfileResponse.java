package com.khureka.server.mypage.dto;

import com.khureka.server.domain.SeatPreference;
import com.khureka.server.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponse {
    private String email;
    private String username;
    private String phone;
    private SeatPreference seatPreference;

    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .seatPreference(user.getSeatPreference())
                .build();
    }
}
