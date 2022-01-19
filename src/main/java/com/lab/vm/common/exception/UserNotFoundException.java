package com.lab.vm.common.exception;

import org.springframework.security.core.AuthenticationException;

public class UserNotFoundException extends RuntimeException  {

    private static final long serialVersionUID = -1126699074574529145L;

    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
