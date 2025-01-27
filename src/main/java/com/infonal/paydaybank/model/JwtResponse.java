package com.infonal.paydaybank.model;

public class JwtResponse {
    private String token;
    private String fullname;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public JwtResponse(String token, String fullname) {
        this.token = token;
        this.fullname = fullname;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}