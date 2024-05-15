package org.cause2.team8.dto.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.cause2.team8.common.utils.exceptions.ErrorCode;
import org.cause2.team8.common.utils.exceptions.SimpleError;
import org.cause2.team8.domain.user.*;

public class UserDTO {
    @Getter
    @RequiredArgsConstructor
    public static class Info {
        private final Long id;
        private final String name;
        private final UserRole role;

        public static Info from(User user) {
            return new Info(user.getId(), user.getName(), user.getUserRole());
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
            return switch (role) {
                case DEV -> new Developer(loginId, name, password);
                case PL -> new ProjectLeader(loginId, name, password);
                case TESTER -> new Tester(loginId, name, password);
                case ADMIN -> throw new SimpleError(ErrorCode.FORBIDDEN);
            };
        }
    }
}
