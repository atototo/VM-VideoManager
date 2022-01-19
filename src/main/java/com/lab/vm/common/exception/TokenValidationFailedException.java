package com.lab.vm.common.exception;

/**
 * packageName : com.lab.vm.common.exception
 * fileName : TokenValidationFailedException
 * author : yelee
 * date : 2022-01-19
 * description : 토큰 유효성 검사 실패 Exception
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-19              yelee             최초 생성
 */

public class TokenValidationFailedException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public TokenValidationFailedException() {
        super();
    }

    public TokenValidationFailedException(String message) {
        super(message);
    }

}
