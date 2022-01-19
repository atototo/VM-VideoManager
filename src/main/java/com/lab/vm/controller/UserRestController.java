package com.lab.vm.controller;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.lab.vm.common.exception.UserReqFailedException;
import com.lab.vm.common.security.jwt.JWTFilter;
import com.lab.vm.common.security.jwt.TokenProvider;
import com.lab.vm.model.domain.RefreshToken;
import com.lab.vm.model.domain.User;
import com.lab.vm.model.dto.LoginDto;
import com.lab.vm.model.dto.RegisterDto;
import com.lab.vm.model.dto.TokenDto;
import com.lab.vm.model.vo.ApiResponseMessage;
import com.lab.vm.repository.RefreshTokenRepository;
import com.lab.vm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;


/**
 * packageName : com.lab.vm.common.controller
 * fileName : UserRestController
 * author : isbn8
 * date : 2022-01-18
 * description : 사용자 정보 관리  REST 컨트롤러
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-18              isbn8             최초 생성
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;



    /**
     * methodName : getActualUser
     * author : Young Lee
     * description : 사용자 정보 조회
     * @return the actual user
     */
    @GetMapping("/user")
    public ResponseEntity<User> getActualUser() {
        return ResponseEntity.ok(userService.getUserWithAuthorities().get());
    }

    /**
     * methodName : registerUser
     * author : Young Lee
     * description : 사용자 등록
     * @param registerDto dto
     * @return ResponseEntity<ApiResponseMessage> entity
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponseMessage> registerUser(@RequestBody @Valid RegisterDto registerDto) {
        return ResponseEntity.ok(userService.registerUser(registerDto));
    }

    /**
     * methodName : modifyUser
     * author : Young Lee
     * description : 사용자 정보 수정
     * @param registerDto registerDto
     * @return response entity
     */
    @PutMapping("/modify-user")
    public ResponseEntity<JWTToken> modifyUser(@RequestBody @Valid RegisterDto registerDto){

        var tokenDto = userService.userModify(registerDto);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + tokenDto.getAccessToken());
        return new ResponseEntity<>(new JWTToken( tokenDto.getAccessToken(), tokenDto.getRefreshToken()), httpHeaders, HttpStatus.OK);
    }


    /**
     * methodName : deleteUser
     * author : Young Lee
     * description : 사용자 탈퇴
     * @param LoginDto dto
     * @return response entity
     */
    @DeleteMapping("/delete-user")
    public ResponseEntity<ApiResponseMessage> deleteUser(@RequestBody @Valid LoginDto loginDto){

        var user = userService.userDelete(loginDto);
        user.orElseThrow(
            ()-> new UserReqFailedException("회원 탈퇴 요청 처리 중 문제가 발생하였습니다.")
        );

        return ResponseEntity.ok(new ApiResponseMessage(HttpStatus.OK.value(), "사용자 탈퇴 처리를 완료 하였습니다."));
    }


    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String accessToken;
        private String refreshToken;

        JWTToken(String accessToken, String refreshToken){

            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        @JsonProperty("access_token")
        String getAccessToken() {
            return accessToken;
        }
        @JsonProperty("refresh_token")
        String getRefreshToken() {
            return refreshToken;
        }



        void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
        void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
}
