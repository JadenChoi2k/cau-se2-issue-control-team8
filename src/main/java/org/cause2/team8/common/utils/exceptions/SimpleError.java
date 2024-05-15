package org.cause2.team8.common.utils.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SimpleError extends ErrorBase {
    private final ErrorCode errorCode;
    private String message = null;

    public SimpleError(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message != null ? message : errorCode.getMessage();
    }

    @Override
    public int getStatusCode() {
        return errorCode.getStatusCode();
    }
}
