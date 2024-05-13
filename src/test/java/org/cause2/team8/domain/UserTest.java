package org.cause2.team8.domain;

import org.cause2.team8.domain.user.User;
import org.cause2.team8.domain.user.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {
    @Test
    @DisplayName("사용자 생성 테스트")
    void create() {
        // given
        String userId = "testId";
        String name = "testName";
        String password = "testPassword";
        UserRole role = UserRole.DEV;
        // when
        User user = User.create(userId, name, password, role);
        // then
        assertEquals(userId, user.getUserId());
        assertEquals(name, user.getName());
        assertEquals(password, user.getPassword());
        assertEquals(role, user.getRole());
    }
}
