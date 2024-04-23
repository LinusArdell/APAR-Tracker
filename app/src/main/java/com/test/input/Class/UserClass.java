package com.test.input.Class;

public class UserClass {

    private String username;
    private String email;

    public UserClass() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserClass(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
