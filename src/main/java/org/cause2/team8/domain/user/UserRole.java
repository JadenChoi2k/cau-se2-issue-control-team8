package org.cause2.team8.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    USER(1), ADMIN(2);

    private final int level;
}
