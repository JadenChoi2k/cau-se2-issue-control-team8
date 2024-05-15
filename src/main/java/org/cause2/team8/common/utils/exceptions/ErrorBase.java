package org.cause2.team8.common.utils.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public abstract class ErrorBase extends RuntimeException {
    public abstract String getMessage();
    public abstract int getStatusCode();

    public ErrorBody getBody() {
        return new ErrorBody(getMessage(), getStatusCode());
    }

    @Getter
    @RequiredArgsConstructor
    public static class ErrorBody {
        private final String message;
        private final int statusCode;
    }
}
