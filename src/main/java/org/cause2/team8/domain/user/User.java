package org.cause2.team8.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    private String userId;
    @Column(length = 20, nullable = false)
    private String name;
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private UserRole role;

    public User(String userId, String name, String password, UserRole role) {
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.role = role;
    }
}
