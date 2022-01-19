package com.lab.vm.controller;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.lab.vm.common.security.jwt.JWTFilter;
import com.lab.vm.common.security.jwt.TokenProvider;
import com.lab.vm.model.domain.RefreshToken;
import com.lab.vm.model.dto.LoginDto;
import com.lab.vm.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * packageName : com.lab.vm.controller
 * fileName : AuthenticationRestController
 * author : yelee
 * date : 2022-01-18
 * description : 로그인 진행 하면 권한 토큰 처리 진행
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-18              yelee             최초 생성
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationRestController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenRepository refreshTokenRepository;


    /**
     * methodName : authorize
     * author : yelee
     * description : 로그인 진행 및 토큰생성
     * @param loginDto dto
     * @return response entity
     */
    @PostMapping("/authenticate")
    @Transactional
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginDto loginDto) {
        log.info("[ 사용자 로그인 진행 ]");

        // 자격증명 검증용 데이터 세팅 1
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        // 자격증명 정보 데이터 세팅 2
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 토큰 생성 (access, refresh)
        var tokenDto = tokenProvider.createToken(authentication);

        // RefreshToken 업데이트
        var refreshToken = refreshTokenRepository.findByKey(loginDto.getUsername());


        // refreshToken 없었을 경우 생성
        if (refreshToken.isEmpty()) {
            log.info("[refresh token 없음 :: 새로 저장 ]");
            var refreshNewToken = RefreshToken.builder()
                    .key(loginDto.getUsername())
                    .token(tokenDto.getRefreshToken())
                    .build();
            refreshTokenRepository.save(refreshNewToken);

        //RefreshToken 있으면 업데이트
        } else {
            log.info("[refresh token 있음 :: 업데이트 ]");
            refreshToken.ifPresent(selectToken -> {
                selectToken.setToken(tokenDto.getRefreshToken());
                refreshTokenRepository.save(selectToken);
            });

        }

        // 헤더 토큰 정보 세팅 및 반환
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + tokenDto.getAccessToken());
        return new ResponseEntity<>(new JWTToken( tokenDto.getAccessToken(), tokenDto.getRefreshToken()), httpHeaders, HttpStatus.OK);
    }


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
