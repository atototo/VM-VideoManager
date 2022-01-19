package com.lab.vm.controller;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


public class LogInUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private LogInUtils() {
    }

    /**
     * 로그인 시 발행되는 토큰 생성
     * @param username
     * @param password
     * @param mockMvc
     * @return
     * @throws Exception
     */
    public static String getTokenForLogin(String username, String password, MockMvc mockMvc) throws Exception {
        String content = mockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\": \"" + password + "\", \"username\": \"" + username + "\"}"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 생성된 데이터의 토큰 값만 매핑
        AuthenticationResponse authResponse = OBJECT_MAPPER.readValue(content, AuthenticationResponse.class);

        return authResponse.getIdToken();
    }

    private static class AuthenticationResponse {

        @JsonAlias("id_token")
        private String idToken;

        public void setIdToken(String idToken) {
            this.idToken = idToken;
        }

        public String getIdToken() {
            return idToken;
        }
    }
}
