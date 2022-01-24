package com.lab.vm.service;


import com.lab.vm.common.security.jwt.JWTFilter;
import com.lab.vm.common.security.jwt.JWTToken;
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
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenRepository refreshTokenRepository;


    /**
     * methodName : authorize
     * author : yelee
     * description : 로그인 계정 토큰 생성
     * @param loginDto dto
     * @return response entity
     */
    public ResponseEntity<JWTToken> checkAuthorize(LoginDto loginDto) {
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
}
