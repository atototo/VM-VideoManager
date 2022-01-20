package com.lab.vm.controller;

import com.lab.vm.model.dto.RegisterDto;
import com.lab.vm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import static com.lab.vm.controller.LogInUtils.getTokenForLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();
    }

    public MockMvc getMockMvc() {
        return mockMvc;
    }

    @Autowired
    private UserService userService;
    void makeUser() {
        var user  = RegisterDto.builder()
//            .activated(true)
                .username("user")
                .password("user")
                .passwordConfirm("user")
                .phone("01011113333")
                .email("user@user.com")
                .build();

        userService.registerUser(user);
    }

    @Test
    @DisplayName("user 권한정보 및 토큰 정보 확인")
     void getActualUserForUserWithToken() throws Exception {
        makeUser();

        //given, when
        final LogInUtils.AuthenticationResponse authResponse = getTokenForLogin("user", "user", getMockMvc());

        //then
        getMockMvc().perform(get("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authResponse.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\n" +
                                "  \"username\" : \"user\",\n" +
                                "  \"email\" : \"user@user.com\",\n" +
                                "  \"authorities\" : [ {\n" +
                                "    \"name\" : \"ROLE_USER\"\n" +
                                "  } ]\n" +
                                "}"
                ));
    }

    @Test
    @DisplayName("토큰 없을 경우 권한문제 발생 확인")
     void getActualUserForUserWithoutToken() throws Exception {
        //given when
        // 아무것도 주어지지 않았을 경우

        //then
        getMockMvc().perform(get("/api/user")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

}