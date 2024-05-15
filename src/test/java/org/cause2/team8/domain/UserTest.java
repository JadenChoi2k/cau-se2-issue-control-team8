package org.cause2.team8.domain;

import org.cause2.team8.common.utils.exceptions.ErrorBase;
import org.cause2.team8.domain.user.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    User testUser() {
        return new Developer("testId", "testName", "testPassword123!");
    }

    @Test
    @DisplayName("Tester 생성 테스트")
    void createTester() {
        // given
        String userId = "testId";
        String name = "testName";
        String password = "testPassword123!";
        UserRole role = UserRole.TESTER;
        // when
        Tester user = new Tester(userId, name, password);
        // then
        user.validate();
        assertEquals(userId, user.getLoginId());
        assertEquals(name, user.getName());
        assertTrue(user.passwordMatches(password));
        assertEquals(role, user.getUserRole());
    }

    @Test
    @DisplayName("Developer 생성 테스트")
    void createDev() {
        // given
        String userId = "testId";
        String name = "testName";
        String password = "testPassword123!";
        UserRole role = UserRole.DEV;
        // when
        Developer user = new Developer(userId, name, password);
        // then
        user.validate();
        assertEquals(userId, user.getLoginId());
        assertEquals(name, user.getName());
        assertTrue(user.passwordMatches(password));
        assertEquals(role, user.getUserRole());
    }

    @Test
    @DisplayName("PL 생성 테스트")
    void createPL() {
        // given
        String userId = "testId";
        String name = "testName";
        String password = "testPassword123!";
        UserRole role = UserRole.PL;
        // when
        ProjectLeader user = new ProjectLeader(userId, name, password);
        // then
        user.validate();
        assertEquals(userId, user.getLoginId());
        assertEquals(name, user.getName());
        assertTrue(user.passwordMatches(password));
        assertEquals(role, user.getUserRole());
    }

    @Test
    @DisplayName("Admin 생성 테스트")
    void createAdmin() {
        // given
        String userId = "testId";
        String name = "testName";
        String password = "testPassword123!";
        UserRole role = UserRole.ADMIN;
        // when
        Admin user = new Admin(userId, name, password);
        // then
        user.validate();
        assertEquals(userId, user.getLoginId());
        assertEquals(name, user.getName());
        assertTrue(user.passwordMatches(password));
        assertEquals(role, user.getUserRole());
    }

    /* 이제부터 User 클래스 테스트 객체 타입은 Developer로 고정합니다. */

    @Test
    @DisplayName("정보 수정")
    void editUser() {
        // given
        User user = testUser();
        String newName = "newName";
        String newPassword = "newPassword123!";
        // when
        user.setName(newName);
        user.setPassword(newPassword);
        // then
        assertEquals(newName, user.getName());
        assertTrue(user.passwordMatches(newPassword));
    }

    @Test
    @DisplayName("유효성 검사 실패 - 아이디")
    void createValidationError1() {
        // given
        String userId = "tes";
        String name = "testName";
        String password = "testPassword123!";
        // when & then
        assertThrows(ErrorBase.class, () -> new Developer(userId, name, password));
    }

    @Test
    @DisplayName("유효성 검사 실패 - 이름")
    void createValidationError2() {
        // given
        String userId = "test1234";
        String name = "testName!@#@!";
        String password = "testPassword123!";
        // when & then
        assertThrows(ErrorBase.class, () -> new Developer(userId, name, password));
    }

    @Test
    @DisplayName("유효성 검사 실패 - 비밀번호")
    void createValidationError3() {
        // given
        String userId = "test1234";
        String name = "testName";
        String password = "test";
        // when & then
        assertThrows(ErrorBase.class, () -> new Developer(userId, name, password));
    }

    @Test
    @DisplayName("비밀번호 일치 실패")
    void passwordMatchFailed() {
        // given
        User user = testUser();
        String wrongPassword = "wrongPassword";
        // when & then
        assertFalse(user.passwordMatches(wrongPassword));
    }
}
