package com.example.prif233.model;



import java.time.LocalDateTime;

public class BasicUser extends User {
    private String address;

    public BasicUser(int id, String login, String password, String name, String surname, String phone_number, String email, LocalDateTime dateCreated, LocalDateTime dateModified, boolean isAdmin, String address) {
        super(id, login, password, name, surname, phone_number, email, dateCreated, dateModified, isAdmin);
        this.address = address;
    }

    public BasicUser(String login, String password, String name, String surname, String phone_number, String email, String address) {
        super(login, password, name, surname, phone_number, email);
        this.address = address;
    }

    public BasicUser() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
