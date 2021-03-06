package com.lab.vm.common.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * packageName : com.lab.vm.common.exception
 * fileName : UserNotActivatedException
 * author : yelee
 * date : 2022-01-19
 * description : 사용자 비활성화 확인 Exception
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-19              yelee             최초 생성
 */
public class UserNotActivatedException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    public UserNotActivatedException(String message) {
        super(message);
    }
}
