package org.cause2.team8.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cause2.team8.common.utils.Utils;
import org.cause2.team8.common.utils.exceptions.SimpleError;
import org.cause2.team8.domain.user.User;
import org.cause2.team8.domain.user.UserRole;
import org.cause2.team8.dto.user.UserDTO;
import org.cause2.team8.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.cause2.team8.common.utils.exceptions.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * 현재 접속 유저가 role에 해당하는 권한을 갖고 있는지를 확인
     */
    @Transactional(readOnly = true)
    public boolean hasRole(HttpSession session, UserRole role) {
        if (session == null) {
            return false;
        }
        Object user = session.getAttribute("user");
        if (user instanceof User) {
            return ((User) user).getUserRole().hasRole(role);
        }
        return false;
    }

    /**
     * 로그인 경로. session에 user 정보를 저장
     */
    public UserDTO.Info login(UserDTO.LoginRequest loginRequest, HttpSession session) {
        User user = userRepository.findByLoginId(loginRequest.getLoginId())
            .orElseThrow(() -> new SimpleError(NOT_FOUND));
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new SimpleError(UNAUTHORIZED);
        }
        session.setAttribute("user", user);
        return UserDTO.Info.from(user);
    }

    /**
     * 회원가입 경로. 현재 어드민만 접근 가능.
     */
    public UserDTO.Info join(UserDTO.JoinRequest joinRequest, HttpSession session) {
        if (userRepository.existsByLoginId(joinRequest.getLoginId())) {
            throw new SimpleError(CONFLICT);
        }
        User user = joinRequest.create();
        userRepository.save(user);
//        session.setAttribute("user", user);
        return UserDTO.Info.from(user);
    }

    private UserDTO.Info editUser(User user, String property, String value) {
        switch (property) {
            case "name":
                user.setName(value);
                break;
            case "password":
                user.setPassword(value);
                break;
            default:
                throw new SimpleError(BAD_REQUEST, "수정할 수 없는 속성입니다.");
        }
        user.validate();
        userRepository.save(user);
        return UserDTO.Info.from(user);
    }

    public UserDTO.Info editMe(HttpSession session, String property, String value) {
        User user = Utils.getUserAuth(session);
        return editUser(user, property, value);
    }

    public UserDTO.Info editUser(Long userId, String property, String value) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new SimpleError(NOT_FOUND));
        return editUser(user, property, value);
    }
}
