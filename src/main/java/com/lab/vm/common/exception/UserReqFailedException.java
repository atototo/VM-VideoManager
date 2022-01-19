package com.lab.vm.common.exception;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class UserReqFailedException extends RuntimeException{

    private static final long serialVersionUID = -1126699074574529145L;

    public UserReqFailedException() {
        super();
    }

    public UserReqFailedException(String message) {
        super(message);
    }

}
