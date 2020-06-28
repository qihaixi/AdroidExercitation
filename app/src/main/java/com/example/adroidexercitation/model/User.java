package com.example.adroidexercitation.model;

import java.io.Serializable;

public class User implements Serializable {
    private int user_id;
    private String username;
    private String mail;
    private String sex;
    private String password;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMail(String mail){this.mail = mail;}

    public void setSex(String sex){this.sex = sex;}

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getMail(){return mail;}

    public String getSex(){return sex;}

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id=" + user_id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
