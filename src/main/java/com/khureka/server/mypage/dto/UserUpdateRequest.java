package com.khureka.server.mypage.dto;

import com.khureka.server.domain.SeatPreference;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {
    private String username;
    private String phone;
    private SeatPreference seatPreference;
}
