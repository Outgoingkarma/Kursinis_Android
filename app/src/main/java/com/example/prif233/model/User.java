package com.example.prif233.model;



import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;


public class User implements Serializable {

    protected int id;
    protected String login;
    protected String password;
    protected String name;
    protected String surname;
    @SerializedName("phone_number")
    protected String phone_number;
    protected String email;
    protected LocalDateTime dateCreated;
    protected LocalDateTime dateModified;
    protected boolean isAdmin;

    public User(int id, String login, String password, String name, String surname, String phone_number, String email, LocalDateTime dateCreated, LocalDateTime dateModified, boolean isAdmin) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phone_number = phone_number;
        this.email = email;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.isAdmin = isAdmin;
    }

    public User(String login, String password, String name, String surname, String phone_number, String email) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phone_number = phone_number;
        this.email = email;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateModified() {
        return dateModified;
    }

    public void setDateModified(LocalDateTime dateModified) {
        this.dateModified = dateModified;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
    @Override
    public String toString() {
        return "Name: " + name + "Surname:" + surname;
    }
}
