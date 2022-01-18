package com.lab.vm.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * packageName : com.lab.vm.common.exception
 * fileName : PasswordConfirmFailedException
 * author : isbn8
 * date : 2022-01-18
 * description :
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-18              isbn8             최초 생성
 */

@Slf4j
public class PasswordConfirmFailedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PasswordConfirmFailedException(String message) {
        super(message);
        log.error(message);
    }
}
