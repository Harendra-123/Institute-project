package com.example.irp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id") // Database column name same rahega
    private int userId;

    @Column(name = "user_name") // Error fix: Ab JPA 'userName' ko pehchan lega
    private String userName;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name ="password")
    private  String password;

    private String role;

    // Default Constructor
    public User() {}

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }


    public String getUserPassword() {
        return password;
    }

    public void setUserPassword(String password) {
        this.password = password;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

//    public Object getId() {
//        return userId;
//    }
}