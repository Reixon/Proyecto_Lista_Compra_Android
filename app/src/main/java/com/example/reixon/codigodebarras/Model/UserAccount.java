package com.example.reixon.codigodebarras.Model;

import java.io.Serializable;

/**
 * Created by reixon on 06/02/2018.
 */

public class UserAccount implements Serializable{
    private String name, email, mToken;
    private int id_user;

    public UserAccount(int id, String name, String email, String token) {
        this.id_user=id;
        this.name = name;
        this.email = email;
        this.mToken = token;
    }
    public UserAccount(String name, String email, String token) {
        this.name = name;
        this.email = email;
        this.mToken = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public int getId_user() {
        return id_user;
    }
}
