package com.khureka.server.security;

import com.khureka.server.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security UserDetails 구현체.
 *
 * User 엔티티를 감싸며, userId를 별도로 관리한다.
 * - 로그인 시: CustomUserDetailsService에서 DB 조회한 User로 생성 (userId = user.getId())
 * - JWT 복원 시: JwtTokenProvider에서 claim으로부터 userId를 전달받아 생성
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;
    private final Long userId;

    /**
     * 로그인 시 사용 — DB에서 조회한 User 객체로 생성.
     */
    public CustomUserDetails(User user) {
        this.user = user;
        this.userId = user.getId();
    }

    /**
     * JWT 복원 시 사용 — User 객체 + userId를 별도 전달.
     *
     * JWT 복원 시 User.builder()로 만든 객체는 id가 null이므로,
     * claim에서 꺼낸 userId를 별도로 보관한다.
     */
    public CustomUserDetails(User user, Long userId) {
        this.user = user;
        this.userId = userId;
    }

    public String getActualUsername() {
        return user.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
