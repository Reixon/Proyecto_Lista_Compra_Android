package com.example.reixon.codigodebarras.Class;

import java.io.Serializable;

/**
 * Created by reixon on 23/01/2018.
 */

public class User implements Serializable {

    private String userName, email;
    private int mid;
    public User(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }

    public void setId(int id){mid=id;}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
