package com.lab.vm.repository;

import com.lab.vm.controller.LogInUtils;
import com.lab.vm.model.domain.Authority;
import com.lab.vm.model.domain.RefreshToken;
import com.lab.vm.model.dto.RegisterDto;
import com.lab.vm.model.dto.TokenDto;
import com.lab.vm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.lab.vm.controller.LogInUtils.getTokenForLogin;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class RefreshTokenRepositoryTest {

    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    UserService userService;

    @Autowired
    private MockMvc mockMvc;

    public MockMvc getMockMvc() {
        return mockMvc;
    }


    @BeforeEach
    @DisplayName("test 유저 정보 세팅")
    void testSetup() {

        var user = RegisterDto.builder()
                .username("test")
                .email("test@test.com")
                .password("1234")
                .passwordConfirm("1234")
                .phone("01011111111")
                .build();

        userService.registerUser(user);
    }

    @DisplayName("로그인 정보로 refreshToken 조회")
    String getRefreshToken() throws Exception {
        LogInUtils.AuthenticationResponse authenticationResponse = getTokenForLogin("test", "1234",  getMockMvc());

        var refreshNewToken = RefreshToken.builder()
                .key("test")
                .token(authenticationResponse.getRefreshToken())
                .build();

        return authenticationResponse.getRefreshToken();
    }

    @Test
    @DisplayName("생성된 리프레시토큰 이름으로 DB 조회 해왔을 때 일치 하는지 확인")
    void findByKey() throws Exception {
        var oriRefreshToken = getRefreshToken();

        // RefreshToken 업데이트
        var refreshToken = refreshTokenRepository.findByKey("test");

        //then
        assertAll(
                () -> assertTrue(refreshToken.isPresent()),
                () -> assertEquals(oriRefreshToken, refreshToken.get().getToken())
        );

    }

}