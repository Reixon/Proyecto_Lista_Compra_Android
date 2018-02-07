package com.example.reixon.codigodebarras.Model;

import java.io.Serializable;

/**
 * Created by reixon on 06/02/2018.
 */

public class UserAccount implements Serializable{
    private String name, email, mToken;
    private int id_user;
    private boolean accountSelected;

    public UserAccount(String name, String email, String token) {
        this.name = name;
        this.email = email;
        this.mToken = token;
        accountSelected=false;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public boolean isAccountSeleccted() {
        return accountSelected;
    }

    public void setAccountSeleccted(boolean accountSeleccted) {
        this.accountSelected = accountSeleccted;
    }
}
