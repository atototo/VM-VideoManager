package com.lab.vm.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class ApiResponseMessage {
    // HttpStatus
    private int status;
    // Http Default Message
    private String message;
    // Error Message to USER
//   private String errorMessage;
//   // Error Code
//   private String errorCode;

    public ApiResponseMessage(int status, String message) {
        this.status = status;
        this.message = message;
//      this.errorCode = errorCode;
//      this.errorMessage = errorMessage;
    }

    public static ApiResponseMessage of(HttpStatus httpStatus, String message) {
        return new ApiResponseMessage(httpStatus.value(), message);
    }
}
