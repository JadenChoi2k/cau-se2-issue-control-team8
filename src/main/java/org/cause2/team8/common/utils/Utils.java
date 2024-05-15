package org.cause2.team8.common.utils;

import jakarta.servlet.http.HttpSession;
import org.cause2.team8.common.utils.exceptions.ErrorCode;
import org.cause2.team8.common.utils.exceptions.SimpleError;
import org.cause2.team8.domain.user.*;

public abstract class Utils {
    public static User getUser(HttpSession session) {
        if (session == null || session.isNew()) {
            return null;
        }
        Object userObj = session.getAttribute("user");
        if (userObj instanceof User) {
            return (User) userObj;
        }
        return null;
    }

    public static User getUserAuth(HttpSession session) {
        User user = getUser(session);
        if (user == null) {
            throw new SimpleError(ErrorCode.UNAUTHORIZED);
        }
        return user;
    }

    public static User getUserAuth(HttpSession session, UserRole role) {
        User user = getUserAuth(session);
        if (!user.getUserRole().hasRole(role)) {
            throw new SimpleError(ErrorCode.FORBIDDEN);
        }
        return user;
    }

    public static Admin getAdmin(HttpSession session) {
        User user = getUserAuth(session, UserRole.ADMIN);
        if (user instanceof Admin) {
            return (Admin) user;
        } else {
            throw new SimpleError(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public static Developer getDeveloper(HttpSession session) {
        User user = getUserAuth(session, UserRole.DEV);
        if (user instanceof Developer) {
            return (Developer) user;
        } else {
            throw new SimpleError(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public static ProjectLeader getProjectLeader(HttpSession session) {
        User user = getUserAuth(session, UserRole.PL);
        if (user instanceof ProjectLeader) {
            return (ProjectLeader) user;
        } else {
            throw new SimpleError(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
