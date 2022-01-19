package com.lab.vm.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * packageName : com.lab.vm.common.exception
 * fileName : UserAlreadyExistException
 * author : yelee
 * date : 2022-01-18
 * description : 회원 등록 시 이미 존재할 경우 발생하는 Exception
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-18              yelee             최초 생성
 */
@Slf4j
public class UserAlreadyExistException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserAlreadyExistException(String message) {
        super(message);
        log.error(message);
    }
}
