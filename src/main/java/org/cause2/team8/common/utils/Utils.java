package org.cause2.team8.common.utils;

import jakarta.servlet.http.HttpSession;
import org.cause2.team8.common.utils.exceptions.ErrorCode;
import org.cause2.team8.common.utils.exceptions.SimpleError;
import org.cause2.team8.domain.user.User;

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
}
