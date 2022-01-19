package com.lab.vm.common.exception;

/**
 * packageName : com.lab.vm.common.exception
 * fileName : UserReqFailedException
 * author : yelee
 * date : 2022-01-19
 * description : 사용자 요청 실패 Exception
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-19              yelee             최초 생성
 */
public class UserReqFailedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserReqFailedException() {
        super();
    }

    public UserReqFailedException(String message) {
        super(message);
    }

}
