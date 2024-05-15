package org.cause2.team8.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.cause2.team8.common.utils.Utils;
import org.cause2.team8.domain.user.User;
import org.cause2.team8.domain.user.UserRole;
import org.cause2.team8.dto.user.UserDTO;
import org.cause2.team8.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "로그인")
    public ResponseEntity<UserDTO.Info> login(@RequestBody UserDTO.LoginRequest loginRequest, HttpServletRequest request) {
        return ResponseEntity.ok(userService.login(loginRequest, request.getSession(true)));
    }

    @PostMapping("/new")
    @Operation(summary = "새로운 유저 생성")
    public ResponseEntity<UserDTO.Info> createNewUser(@RequestBody UserDTO.JoinRequest joinRequest, HttpServletRequest request) {
        if (!userService.hasRole(request.getSession(true), UserRole.ADMIN)) {
            throw new RuntimeException("only admin can create new user");
        }
        return ResponseEntity.ok(userService.join(joinRequest, request.getSession(true)));
    }

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회")
    public ResponseEntity<UserDTO.Info> me(HttpSession session) {
        User user = Utils.getUser(session);
        if (user == null) {
            throw new RuntimeException("not logged in");
        }
        return ResponseEntity.ok(UserDTO.Info.from(user));
    }

    @PatchMapping("/{userId}/role")
    @Operation(summary = "유저 권한 변경")
    public ResponseEntity<UserDTO.Info> changeUserRole(@PathVariable Long userId, @RequestParam UserRole role, HttpSession session) {
        if (!userService.hasRole(session, UserRole.ADMIN)) {
            throw new RuntimeException("only admin can change user role");
        }
        return ResponseEntity.ok(userService.changeRole(userId, role));
    }
}
