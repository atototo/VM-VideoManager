package com.lab.vm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import static com.lab.vm.controller.LogInUtils.getTokenForLogin;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PersonRestControllerTest {
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
    void getPersonForUser() throws Exception {
        final String token = getTokenForLogin("user", "user", getMockMvc());

        assertSuccessfulPersonRequest(token);
    }

    @Test
    void getPersonForAdmin() throws Exception {
        final String token = getTokenForLogin("admin", "admin", getMockMvc());

        assertSuccessfulPersonRequest(token);
    }

    @Test
    void getPersonForAnonymous() throws Exception {
        getMockMvc().perform(get("/api/person")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    private void assertSuccessfulPersonRequest(String token) throws Exception {
        getMockMvc().perform(get("/api/person")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\n" +
                                "  \"name\" : \"John Doe\",\n" +
                                "  \"email\" : \"john.doe@test.org\"\n" +
                                "}"
                ));
    }
}