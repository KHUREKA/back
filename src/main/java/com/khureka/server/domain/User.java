package com.khureka.server.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(length = 30)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SeatPreference seatPreference;

    @Builder
    public User(String email, String password, String username, Role role, String phone, SeatPreference seatPreference) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role;
        this.phone = phone;
        this.seatPreference = (seatPreference != null) ? seatPreference : SeatPreference.NONE;
    }

    /**
     * 사용자 정보를 수정한다.
     */
    public void updateProfile(String username, String phone, SeatPreference seatPreference) {
        if (username != null) this.username = username;
        if (phone != null) this.phone = phone;
        if (seatPreference != null) this.seatPreference = seatPreference;
    }
}
