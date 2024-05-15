package org.cause2.team8.domain;

import org.cause2.team8.domain.user.Tester;
import org.cause2.team8.domain.user.User;
import org.cause2.team8.domain.user.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {
    @Test
    @DisplayName("유저 생성 테스트")
    void create() {
        // given
        String userId = "testId";
        String name = "testName";
        String password = "testPassword123!";
        UserRole role = UserRole.TESTER;
        // when
        Tester user = new Tester(userId, name, password);
        // then
        assertEquals(userId, user.getLoginId());
        assertEquals(name, user.getName());
        assertEquals(password, user.getPassword());
        assertEquals(role, user.getUserRole());
    }
}
