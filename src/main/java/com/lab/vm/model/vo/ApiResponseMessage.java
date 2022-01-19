package com.lab.vm.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;


/**
 * packageName : com.lab.vm.vo.ApiResponseMessage
 * fileName : ApiResponseMessage
 * author : yelee
 * date : 2022-01-18
 * description : api 응답 메세지 vo
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-18              yelee             최초 생성
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
public class ApiResponseMessage {

    private int status;
    private String message;


    public ApiResponseMessage(int status, String message) {
        this.status = status;
        this.message = message;

    }

    public static ApiResponseMessage of(HttpStatus httpStatus, String message) {
        return new ApiResponseMessage(httpStatus.value(), message);
    }
}
