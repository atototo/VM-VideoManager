package com.lab.vm.common.exception;

import com.lab.vm.model.vo.ApiResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * packageName : com.lab.vm.common.exception
 * fileName : GlobalExceptionHandler
 * author : yelee
 * date : 2022-01-18
 * description : 예외 공통 처리
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-18              yelee             최초 생성
 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 입력값 validation Exception
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseMessage> methodValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.info("MethodArgumentNotValidException 발생!!! url:{}, trace:{}", request.getRequestURI(), e.getStackTrace());
        ApiResponseMessage errorResponse = ApiResponseMessage.of(HttpStatus.BAD_REQUEST,e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    /**
     * 기존 회원 여부 확인 Exception
     * @param ex
     * @return
     */
    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponseMessage>  userAlreadyExistException(UserAlreadyExistException ex) {
        ApiResponseMessage errorResponse = ApiResponseMessage.of(HttpStatus.BAD_REQUEST, ex.getMessage());
        log.info("UserAlreadyExistException 발생!! {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 유저 거부 Exception
     * @param ex
     * @return
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponseMessage>  userAccessDeniedException(AccessDeniedException ex) {
        ApiResponseMessage errorResponse = ApiResponseMessage.of(HttpStatus.FORBIDDEN, "접근 권한이 없습니다");
        log.info("AccessDeniedException 발생!! {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
   /**
     * refresh 토큰 유효성 Exception
     * @param ex
     * @return
     */
    @ExceptionHandler(RefreshTokenException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponseMessage>  refreshTokenException(RefreshTokenException ex) {
        ApiResponseMessage errorResponse = ApiResponseMessage.of(HttpStatus.FORBIDDEN, "리프레쉬 토큰이 유효하지 않습니다.");
        log.info("RefreshTokenException 발생!! {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

   /**
     * 사용자 정보 미확인 Exception
     * @param ex
     * @return
     */
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponseMessage>  userNotFoundException(UserNotFoundException ex) {
        ApiResponseMessage errorResponse = ApiResponseMessage.of(HttpStatus.BAD_REQUEST, "사용자 정보를 찾을 수가 없습니다.");
        log.info("UserNotFoundException 발생!! {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


   /**
     * 비밀번호 불일치 Exception
     * @param ex
     * @return
     */
    @ExceptionHandler(PasswordConfirmFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponseMessage>  passwordConfirmFailedException(PasswordConfirmFailedException ex) {
        ApiResponseMessage errorResponse = ApiResponseMessage.of(HttpStatus.BAD_REQUEST, "비밀번호가 맞지 않습니다.");
        log.info("PasswordConfirmFailedException 발생!! {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 사용자 요청 실패 Exception
     * @param ex
     * @return
     */
   @ExceptionHandler(UserReqFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponseMessage> userReqFailedException(UserReqFailedException ex) {
        ApiResponseMessage errorResponse = ApiResponseMessage.of(HttpStatus.BAD_REQUEST, ex.getMessage());
        log.info("UserReqFailedException 발생!! {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    /**
     *  토큰 유효성 검사 실패 Exception
     * @param ex
     * @return
     */
   @ExceptionHandler(TokenValidationFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponseMessage> tokenValidationFailedException(TokenValidationFailedException ex) {
        ApiResponseMessage errorResponse = ApiResponseMessage.of(HttpStatus.BAD_REQUEST, ex.getMessage());
        log.info("TokenValidationFailedException 발생!! {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    /**
     *  비활성 사용자 접근 Exception
     * @param ex
     * @return
     */
   @ExceptionHandler(UserNotActivatedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponseMessage> userNotActivatedException(UserNotActivatedException ex) {
        ApiResponseMessage errorResponse = ApiResponseMessage.of(HttpStatus.FORBIDDEN, ex.getMessage());
        log.info("UserNotActivatedException 발생!! {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

  /**
     *  알수없는 사용자 접근 Exception
     * @param ex
     * @return
     */
   @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponseMessage> usernameNotFoundException(UsernameNotFoundException ex) {
        ApiResponseMessage errorResponse = ApiResponseMessage.of(HttpStatus.FORBIDDEN, ex.getMessage());
        log.info("UsernameNotFoundException 발생!! {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }


  /**
     *  알수없는 사용자 접근 Exception
     * @param ex
     * @return
     */
   @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponseMessage> authenticationException(AuthenticationException ex) {
        ApiResponseMessage errorResponse = ApiResponseMessage.of(HttpStatus.FORBIDDEN, ex.getMessage());
        log.info("AuthenticationException 발생!! {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }




}
