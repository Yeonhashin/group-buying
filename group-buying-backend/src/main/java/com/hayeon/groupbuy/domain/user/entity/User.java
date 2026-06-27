package com.hayeon.groupbuy.domain.user.entity;

import com.hayeon.groupbuy.domain.user.enums.UserRole;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.BUYER;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public static User create(String email, String password, String nickname, UserRole role) {
        User user = new User();
        user.email = email;
        user.password = password;
        user.nickname = nickname;
        user.role = role != null ? role : UserRole.BUYER;
        return user;
    }
}