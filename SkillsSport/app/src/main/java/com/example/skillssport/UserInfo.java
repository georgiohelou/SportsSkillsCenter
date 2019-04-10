package com.example.skillssport;

public class UserInfo {
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String email;
    public String first_name;
    public String last_name;

    public UserInfo(String email, String first_name, String last_name) {
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public UserInfo() {
        this.email = "N/A";
        this.first_name = "N/A";
        this.last_name = "N/A";
    }
}
