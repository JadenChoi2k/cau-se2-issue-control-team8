package org.cause2.team8.dto.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.cause2.team8.domain.user.User;
import org.cause2.team8.domain.user.UserRole;

public class UserDTO {
    @Getter
    @RequiredArgsConstructor
    public static class Info {
        private final Long id;
        private final String name;
        private final UserRole role;

        public static Info from(User user) {
            return new Info(user.getId(), user.getName(), user.getRole());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class LoginRequest {
        private final String loginId;
        private final String password;
    }

    @Getter
    @RequiredArgsConstructor
    public static class JoinRequest {
        private final String loginId;
        private final String password;
        private final String name;
        private final UserRole role;

        public User create() {
            if (role == UserRole.ADMIN) {
                throw new RuntimeException("어드민은 직접 생성할 수 없습니다.");
            }
            return User.create(loginId, name, password, role);
        }
    }
}
