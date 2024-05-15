package org.cause2.team8.domain.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    TESTER(0), DEV(1), PL(2), ADMIN(3);

    /**
     * 권한 범위를 계층적으로 나눔.
     * 0: 테스터. 이슈를 등록할 수 있음. 이슈 생성/조회/수정(본인)/삭제(본인)/코멘트 가능
     * 1: 개발자. 테스터의 권한을 포함하며, 이슈를 할당받아 작업 진행 가능. 이슈를 해결 처리할 수 있음.
     * 2: Project Leader. 개발자의 권한을 포함하며, 프로젝트 관리 가능. 이슈를 할당하고 관리할 수 있음. 완전히 처리되도록 처리 가능.
     * 3: 관리자. 모든 권한을 가짐. 사용자 관리 가능.
     */
    private final int level;

    @JsonCreator
    public static UserRole from(String value) {
        for (UserRole role : values()) {
            if (role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return name().toLowerCase();
    }

    public boolean hasRole(UserRole role) {
        return this.level >= role.level;
    }
}
