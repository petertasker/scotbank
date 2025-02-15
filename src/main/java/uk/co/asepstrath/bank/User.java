package uk.co.asepstrath.bank;

import java.util.UUID;

public class User {

    private String userName;
    private String userID;
    private String email;


    public User(String name, String email) {
        this.userName = name;
        this.userID = UUID.randomUUID().toString().replace("-", "");
        this.email = email;
    }


    public String getUserID() {
        return userID;
    }


    public String getUserName() {
        return userName;
    }


    public String getEmail() {
        return email;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}


