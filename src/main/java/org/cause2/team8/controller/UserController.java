package org.cause2.team8.controller;

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
    public ResponseEntity<UserDTO.Info> login(@RequestBody UserDTO.LoginRequest loginRequest, HttpServletRequest request) {
        return ResponseEntity.ok(userService.login(loginRequest, request.getSession(true)));
    }

    @PostMapping("/join")
    public ResponseEntity<UserDTO.Info> join(@RequestBody UserDTO.JoinRequest joinRequest, HttpServletRequest request) {
        return ResponseEntity.ok(userService.join(joinRequest, request.getSession(true)));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO.Info> me(HttpSession session) {
        User user = Utils.getUser(session);
        if (user == null) {
            throw new RuntimeException("not logged in");
        }
        return ResponseEntity.ok(UserDTO.Info.from(user));
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<UserDTO.Info> changeUserRole(@PathVariable Long userId, @RequestParam UserRole role, HttpSession session) {
        if (!userService.hasRole(session, UserRole.ADMIN)) {
            throw new RuntimeException("only admin can change user role");
        }
        return ResponseEntity.ok(userService.changeRole(userId, role));
    }
}
