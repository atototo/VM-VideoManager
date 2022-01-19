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
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationRestController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenRepository refreshTokenRepository;

//
//
//    public AuthenticationRestController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
//        this.tokenProvider = tokenProvider;
//        this.authenticationManagerBuilder = authenticationManagerBuilder;
//    }

    @PostMapping("/authenticate")
    @Transactional
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginDto loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var tokenDto = tokenProvider.createToken(authentication);

        // RefreshToken 업데이트
        var refreshToken = refreshTokenRepository.findByKey(loginDto.getUsername());



        // RefreshToken 없으면 저장
        if (!refreshToken.isPresent()) {

            log.info("[refresh token 없음 :: 새로 저장 ]");
            var refreshNewToken = RefreshToken.builder()
                    .key(loginDto.getUsername())
                    .token(tokenDto.getRefreshToken())
                    .build();
            refreshTokenRepository.save(refreshNewToken);
        } else {
            //RefreshToken 있으면 업데이트
            log.info("[refresh token 있음 :: 업데이트 ]");
            refreshToken.ifPresent(selectToken -> {
                selectToken.setToken(tokenDto.getRefreshToken());
                refreshTokenRepository.save(selectToken);
            });

        }

        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + tokenDto.getAccessToken());

        /**
         * storage accesstoken refrestoken 두개 다 저장 시켜야 하는데 가능 여부 파악 id_token 으로 accesstoken  받는데 어떻게..?
         */
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + tokenDto.getAccessToken());
        return new ResponseEntity<>(new JWTToken( tokenDto.getAccessToken(), tokenDto.getRefreshToken()), httpHeaders, HttpStatus.OK);
    }

    /**
     * Object to return as body in JWT Authentication.
     */
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
