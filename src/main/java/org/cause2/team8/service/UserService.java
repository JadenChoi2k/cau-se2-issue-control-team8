package org.cause2.team8.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cause2.team8.domain.user.User;
import org.cause2.team8.domain.user.UserRole;
import org.cause2.team8.dto.user.UserDTO;
import org.cause2.team8.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDTO.Info login(UserDTO.LoginRequest loginRequest, HttpSession session) {
        User user = userRepository.findByUserId(loginRequest.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new RuntimeException("Password not matched");
        }
        session.setAttribute("user", user);
        return UserDTO.Info.from(user);
    }

    public UserDTO.Info join(UserDTO.JoinRequest joinRequest, HttpSession session) {
        if (userRepository.existsByUserId(joinRequest.getUserId())) {
            throw new RuntimeException("User already exists");
        }
        User user = joinRequest.create();
        userRepository.save(user);
        session.setAttribute("user", user);
        return UserDTO.Info.from(user);
    }

    @Transactional(readOnly = true)
    public boolean hasRole(HttpSession session, UserRole role) {
        if (session == null) {
            return false;
        }
        Object user = session.getAttribute("user");
        if (user instanceof User) {
            return ((User) user).getRole().equals(role);
        }
        return false;
    }

    public UserDTO.Info changeRole(Long userId, UserRole role) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(role);
        return UserDTO.Info.from(user);
    }
}
