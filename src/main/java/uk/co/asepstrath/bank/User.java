package uk.co.asepstrath.bank;

import java.util.UUID;

public class User {

    private String userName;
    private String userID;

    public User(String name) {
        this.userName = name;
        this.userID = UUID.randomUUID().toString().replace("-", "");
    }

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}


