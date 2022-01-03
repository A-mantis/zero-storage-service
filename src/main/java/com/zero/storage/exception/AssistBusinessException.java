package com.zero.storage.exception;

import javax.servlet.http.HttpServletResponse;

public class AssistBusinessException extends RuntimeException {
    private static final long serialVersionUID = 4328407468288938158L;
    private static final int STATUS_CODE = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

    public AssistBusinessException() {
    }

    public AssistBusinessException(String message) {
        super(message);
    }

    public AssistBusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public AssistBusinessException(Throwable cause) {
        super(cause);
    }

    public void throwIf(boolean b) {
        if (b) {
            throw this;
        }
    }

    public int getStatusCode() {
        return STATUS_CODE;
    }
}

