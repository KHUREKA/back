package com.khureka.server.mypage.service;

import com.khureka.server.auth.repository.UserRepository;
import com.khureka.server.common.exception.BusinessException;
import com.khureka.server.common.exception.ErrorCode;
import com.khureka.server.domain.User;
import com.khureka.server.mypage.dto.UserProfileResponse;
import com.khureka.server.mypage.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final UserRepository userRepository;

    public UserProfileResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserProfileResponse.from(user);
    }

    @Transactional
    public UserProfileResponse updateMyProfile(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.updateProfile(request.getUsername(), request.getPhone(), request.getSeatPreference());
        
        return UserProfileResponse.from(user);
    }
}
