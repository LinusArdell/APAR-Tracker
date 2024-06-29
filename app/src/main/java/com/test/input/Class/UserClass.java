package com.test.input.Class;

public class UserClass {

    private String username, role;
    private String email;

    public UserClass() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserClass(String username, String email, String role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
