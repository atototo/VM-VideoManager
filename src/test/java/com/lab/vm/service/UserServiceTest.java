package com.lab.vm.service;

import com.lab.vm.common.exception.PasswordConfirmFailedException;
import com.lab.vm.common.exception.UserAlreadyExistException;
import com.lab.vm.common.security.SecurityUtils;
import com.lab.vm.common.security.jwt.TokenProvider;
import com.lab.vm.controller.LogInUtils;
import com.lab.vm.model.domain.Authority;
import com.lab.vm.model.domain.User;
import com.lab.vm.model.domain.Video;
import com.lab.vm.model.dto.LoginDto;
import com.lab.vm.model.dto.RegisterDto;
import com.lab.vm.model.dto.TokenDto;
import com.lab.vm.repository.AuthorityRepository;
import com.lab.vm.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static com.lab.vm.controller.LogInUtils.getTokenForLogin;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * packageName : com.lab.vm.service
 * fileName : UserServiceTest
 * author : isbn8
 * date : 2022-01-19
 * description :
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-19              isbn8             최초 생성
 */
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();
    }

    public MockMvc getMockMvc() {
        return mockMvc;
    }

    @BeforeEach
    @DisplayName("권한 부여")
    void makeRules(){
        Authority authority = new Authority();
        authority.setName("ROLE_ADMIN");

        Authority authority2 = new Authority();
        authority2.setName("ROLE_USER");

        authorityRepository.save(authority);
        authorityRepository.save(authority2);

    }


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


    @Test
    @DisplayName("회원가입_정상확인")
//   @Rollback(value = false)   // 이 옵션을 두면 트랜잭션이 롤백을 하지 않아 눈으로 확인 가능하다
    void join() throws Exception {
        //given
        var user = RegisterDto.builder()
                .username("lee")
                .email("yelee@yelee.com")
                .password("1234")
                .phone("01011111111")
                .build();

        //when
        Optional<User> savedUser = userService.userJoin(user);
        BCryptPasswordEncoder b = new BCryptPasswordEncoder();
        var userDto = RegisterDto.builder()
                .id(savedUser.orElseThrow().getId())
                .username("lee")
                .email("yelee@yelee.com")
                .password("1234")
                .passwordConfirm(null)
                .phone("01011111111")
                .build();


       RegisterDto registerDto = userService.findUserInfoByName("lee");


        System.out.println(" [ findUserInfoByName ]  결과 UserDto : " + registerDto.toString());

        //then
        assertEquals(userDto.getId(), registerDto.getId());
//        assertEquals(userDto.getEmail(), regDto.orElseThrow().getEmail());
//        assertEquals(userDto.getPhone(), regDto.orElseThrow().getPhone());
        assertTrue(b.matches(userDto.getPassword(), registerDto.getPassword()));   //비빌번호 동일 여부 논리값으로 반환

    }


    @Test
    @DisplayName("기존 회원 여부 검증 확인")
    void chkValidateExistUser() {
        //  given : test
        testSetup();

        //when : 동일 이름 등록 재시도
        var registerDto = RegisterDto.builder()
                .username("test")
                .email("test@test.com")
                .password("1234")
                .passwordConfirm("1234")
                .phone("01011111111")
                .build();

        //then
        assertThrows(UserAlreadyExistException.class, () -> {
            userService.registerUser(registerDto);
        });
     }

     @Test
     @DisplayName("비밀번호 오입력 검증 확인")
     void chkPasswordConfirm() {
         //given when : 비밀번호 다르게 입력
         var registerDto = RegisterDto.builder()
                 .username("test")
                 .email("test@test.com")
                 .password("1234")
                 .passwordConfirm("5678")
                 .phone("01011111111")
                 .build();

         //then
         assertThrows(PasswordConfirmFailedException.class, () -> {
             userService.registerUser(registerDto);
         });

    }

    @Test
    @DisplayName("사용자 정보 업데이트 확인")
    void userModify() {
        //  given : test, pwd:  1234, email: test@test.com , phone :  01011111111
        testSetup();

        //when : email pwd phone 변경
        var registerDto = RegisterDto.builder()
                .username("test")
                .email("test22@test.com")
                .password("5678")
                .passwordConfirm("5678")
                .phone("01022222222")
                .build();

        userService.userModify(registerDto);


        var modifyUser = userRepository.findAllByUsername("test");

        //then
        assertAll(
                () -> assertTrue(modifyUser.isPresent()),
                () -> assertEquals(registerDto.getEmail(), modifyUser.get().getEmail()),
                () -> assertEquals(registerDto.getPhone(), modifyUser.get().getPhone())
        );

    }

    @Test
    @DisplayName("사용자 정보 비활성화 처리")
    void userDelete() {

        //  given : test, pwd:  1234, email: test@test.com , phone :  01011111111
        testSetup();

        var reqBlock = LoginDto.builder()
                .username("test")
                .password("1234")
                .build();
        //when
        var user = userService.userDelete(reqBlock);

        //then
        assertAll(
                () -> assertTrue(user.isPresent()),
                () -> assertFalse(user.get().isActivated())
        );


    }


    @Test
    @DisplayName("입력된 비밀번호와 암호화 된 DB 비밀번호 동일 여부 확인")
    void isSameOriPwdWithEncPwd() {
        //  given : test, pwd:  1234, email: test@test.com , phone :  01011111111
        testSetup();

        var user = userRepository.findAllByUsername("test");

        //then
        assertTrue( userService.isSameOriPwdWithEncPwd("1234", user.get().getPassword()));
    }

    @Test
    @DisplayName("사용자명으로 정보 조회 일치 확인")
    void findUserInfoByName(){
        //  given : test, pwd:  1234, email: test@test.com , phone :  01011111111
        testSetup();

        var user = userRepository.findAllByUsername("test");

        //then
        assertAll(
                () -> assertTrue(user.isPresent()),
                () -> assertEquals("test@test.com", user.get().getEmail()),
                () -> assertEquals("01011111111", user.get().getPhone()),
                () -> assertTrue(userService.isSameOriPwdWithEncPwd("1234", user.get().getPassword()))
        );
    }

    @Test
    @DisplayName("기존 토큰으로 refresh 토큰 정상 발행 되는지 확인")
    void refreshToken() throws Exception{
//          given : test, pwd:  1234, email: test@test.com , phone :  01011111111
        testSetup();
        //토큰 생성
        LogInUtils.AuthenticationResponse authenticationResponse  = getTokenForLogin("test", "1234", getMockMvc());

        var tokenDto = TokenDto.builder()
                .refreshToken(authenticationResponse.getRefreshToken())
                .accessToken(authenticationResponse.getAccessToken())
                .build();
        // when 기존 발행된 토큰 정보로 refresh 토큰 정보를 받아온다
        var refreshTokenDto = userService.refreshToken(tokenDto);
        // 새로 발행 된 토큰의 권한 정보 확인
        var authentication = tokenProvider.getAuthentication(refreshTokenDto.getAccessToken());
        assertEquals("test", authentication.getName());
    }
}