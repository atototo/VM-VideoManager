package com.lab.vm.common.exception;


/**
 * packageName : com.lab.vm.common.exception
 * fileName : UserNotFoundException
 * author : yelee
 * date : 2022-01-19
 * description : 사용자 미확인 Exception
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-19              yelee             최초 생성
 */
public class UserNotFoundException extends RuntimeException  {

    private static final long serialVersionUID = 1L;

    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
