package edu.uncc.midtermapp;

import java.io.Serializable;

public class UserToken implements Serializable {
    String token, fullname, userId;
    public UserToken(String token, String fullname, String userId){
        this.token = token;
        this.fullname = fullname;
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
