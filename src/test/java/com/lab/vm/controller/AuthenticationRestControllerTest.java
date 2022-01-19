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
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthenticationRestControllerTest  {


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();
    }
    public MockMvc getMockMvc() {
        return mockMvc;
    }

    @Test
    @DisplayName("사용자 정보 및 토큰 정상 발행 확인")
    void successfulAuthenticationWithUser() throws Exception {
        getMockMvc().perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\": \"user\", \"username\": \"user\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id_token")));
    }

    @Test
    @DisplayName("관리자 정보 및 토큰 정상 발행 확인")
    void successfulAuthenticationWithAdmin() throws Exception {
        getMockMvc().perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\": \"admin\", \"username\": \"admin\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id_token")));
    }

    @Test
    void unsuccessfulAuthenticationWithDisabled() throws Exception {
        getMockMvc().perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\": \"password\", \"username\": \"disabled\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(not(containsString("id_token"))));
    }

    @Test
    void unsuccessfulAuthenticationWithWrongPassword() throws Exception {
        getMockMvc().perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\": \"wrong\", \"username\": \"user\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(not(containsString("id_token"))));
    }

    @Test
    void unsuccessfulAuthenticationWithNotExistingUser() throws Exception {
        getMockMvc().perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\": \"password\", \"username\": \"not_existing\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(not(containsString("id_token"))));
    }
}