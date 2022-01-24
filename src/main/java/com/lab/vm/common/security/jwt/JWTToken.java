package com.lab.vm.common.security.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JWTToken {

    private String accessToken;
    private String refreshToken;

    public JWTToken(String accessToken, String refreshToken){

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
