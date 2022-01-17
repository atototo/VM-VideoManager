package com.lab.vm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class AdminProtectedRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();
    }

    public MockMvc getMockMvc() {
        return mockMvc;
    }


    @Test
    public void getAdminProtectedGreetingForUser() throws Exception {
        final String token = LogInUtils.getTokenForLogin("user", "user", getMockMvc());

        getMockMvc().perform(get("/api/hiddenmessage")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getAdminProtectedGreetingForAdmin() throws Exception {
        final String token = LogInUtils.getTokenForLogin("admin", "admin", getMockMvc());

        getMockMvc().perform(get("/api/hiddenmessage")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\n" +
                                "  \"message\" : \"this is a hidden message!\"\n" +
                                "}"
                ));
    }

    @Test
    public void getAdminProtectedGreetingForAnonymous() throws Exception {
        getMockMvc().perform(get("/api/hiddenmessage")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}