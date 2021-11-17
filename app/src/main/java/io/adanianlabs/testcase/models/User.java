package io.adanianlabs.testcase.models;

public class User {
    private String accesstoken;
    private String username;
    private String email;
    private String mobile;
    private int id;
    private String profilepicture;


    public User(String accesstoken, String username,  String email, String mobile, int id, String profilepicture) {
        this.accesstoken = accesstoken;
        this.username = username;
        this.email = email;
        this.mobile = mobile;
        this.id = id;
        this.profilepicture = profilepicture;
    }

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProfilepicture() {
        return profilepicture;
    }

    public void setProfilepicture(String profilepicture) {
        this.profilepicture = profilepicture;
    }


}
