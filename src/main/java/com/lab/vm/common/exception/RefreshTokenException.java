package com.lab.vm.common.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RefreshTokenException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RefreshTokenException() {
        super();
    }

    public RefreshTokenException(String message) {
        super(message);
        log.error(message);
    }
}
