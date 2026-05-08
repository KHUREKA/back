package com.khureka.server.mypage.controller;

import com.khureka.server.mypage.dto.UserProfileResponse;
import com.khureka.server.mypage.dto.UserUpdateRequest;
import com.khureka.server.mypage.service.MyPageService;
import com.khureka.server.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MyPage", description = "마이페이지 API (내 정보 조회 및 수정)")
@RestController
@RequestMapping("/api/v1/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @Operation(summary = "내 정보 조회", description = "로그인한 사용자의 프로필(이름, 이메일, 전화번호, 좌석 선호도)을 조회합니다.")
    @GetMapping("/me")
    public UserProfileResponse getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return myPageService.getMyProfile(userDetails.getUserId());
    }

    @Operation(summary = "내 정보 수정", description = "사용자의 이름, 전화번호, 좌석 선호도(불편한 곳)를 수정합니다.")
    @PutMapping("/me")
    public UserProfileResponse updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserUpdateRequest request) {
        return myPageService.updateMyProfile(userDetails.getUserId(), request);
    }
}
