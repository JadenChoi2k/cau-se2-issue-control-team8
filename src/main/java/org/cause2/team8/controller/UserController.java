package org.cause2.team8.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.cause2.team8.common.utils.Utils;
import org.cause2.team8.common.utils.exceptions.ErrorCode;
import org.cause2.team8.common.utils.exceptions.SimpleError;
import org.cause2.team8.domain.user.User;
import org.cause2.team8.domain.user.UserRole;
import org.cause2.team8.dto.user.UserDTO;
import org.cause2.team8.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            throw new SimpleError(ErrorCode.FORBIDDEN, "only admin can create new user");
        }
        return ResponseEntity.ok(userService.join(joinRequest, request.getSession(true)));
    }

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회")
    public ResponseEntity<UserDTO.Info> me(HttpSession session) {
        User user = Utils.getUserAuth(session);
        return ResponseEntity.ok(UserDTO.Info.from(user));
    }

    @PatchMapping("/{userObj}/{property}")
    @Operation(summary = "유저 정보 수정")
    public ResponseEntity<UserDTO.Info> editMe(
        @Parameter(description = "내 정보 수정의 경우 'me'. 타인 정보 수정일 경우 userId") @PathVariable String userObj,
        @Parameter(description = "name, password, role") @PathVariable String property,
        @Parameter(description = "수정할 정보의 값. body data 전부를 받습니다.") @RequestBody String value,
        HttpSession session) {
        boolean isMe = "me".equals(userObj);
        if (isMe) {
            return ResponseEntity.ok(userService.editMe(session, property, value));
        } else {
            if (!userService.hasRole(session, UserRole.ADMIN)) {
                throw new SimpleError(ErrorCode.FORBIDDEN);
            }
            try {
                Long userId = Long.parseLong(userObj);
                return ResponseEntity.ok(userService.editUser(userId, property, value));
            } catch (Exception e) {
                throw new SimpleError(ErrorCode.BAD_REQUEST, "number type으로 입력해주세요");
            }
        }
    }

    @GetMapping("/all")
    @Operation(summary = "모든 유저 조회. ADMIN만 접근 가능합니다.")
    public ResponseEntity<List<UserDTO.Info>> allUsers(HttpSession session, @RequestParam int page, @RequestParam int size) {
        if (!userService.hasRole(session, UserRole.ADMIN)) {
            throw new SimpleError(ErrorCode.FORBIDDEN);
        }
        return ResponseEntity.ok(userService.allUsers(page, size));
    }
}
