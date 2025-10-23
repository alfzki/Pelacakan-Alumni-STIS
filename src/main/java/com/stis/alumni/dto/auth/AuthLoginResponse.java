package com.stis.alumni.dto.auth;

import com.stis.alumni.dto.user.UserSummaryResponse;

public class AuthLoginResponse {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private UserSummaryResponse user;

    public AuthLoginResponse() {
    }

    public AuthLoginResponse(String accessToken, String tokenType, long expiresIn, UserSummaryResponse user) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserSummaryResponse getUser() {
        return user;
    }

    public void setUser(UserSummaryResponse user) {
        this.user = user;
    }
}
