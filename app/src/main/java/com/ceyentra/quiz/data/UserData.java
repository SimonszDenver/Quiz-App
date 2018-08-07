package com.ceyentra.quiz.data;

public class UserData {

    private static UserData userData;
    private String username;

    private UserData(){}

    public static UserData getInstance(){
        if (userData == null){
            userData = new UserData();
        }
        return userData;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
