package com.lab.vm.common.exception;

import lombok.extern.slf4j.Slf4j;


/**
 * packageName : com.lab.vm.common.exception
 * fileName : RefreshTokenException
 * author : yelee
 * date : 2022-01-18
 * description :
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-18              yelee             최초 생성
 */
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
