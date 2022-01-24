package com.lab.vm.controller;


import com.lab.vm.common.security.jwt.JWTToken;
import com.lab.vm.model.dto.LoginDto;
import com.lab.vm.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    private final AuthenticationService authenticationService;



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
        return authenticationService.checkAuthorize(loginDto);
    }

}
