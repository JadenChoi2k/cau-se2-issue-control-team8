package org.cause2.team8.common.utils.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    BAD_REQUEST("잘못된 요청입니다.", 400),
    UNAUTHORIZED("인증되지 않은 사용자입니다.", 401),
    FORBIDDEN("권한이 없습니다.", 403),
    NOT_FOUND("요청한 자원을 찾을 수 없습니다.", 404),
    CONFLICT("이미 존재하는 자원입니다.", 409),
    INTERNAL_SERVER_ERROR("서버 내부 오류입니다.", 500);

    private final String message;
    private final int statusCode;
}
