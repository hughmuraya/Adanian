package io.adanianlabs.testcase.models;

public class Post {

    private int id;
    private String post;
    private String mediaurl;
    private String type;
    private String profilepicture;
    private String username;




    public Post(int id,  String post, String mediaurl, String type, String profilepicture,String username) {

        this.id = id;
        this.post = post;
        this.mediaurl = mediaurl;
        this.type = type;
        this.profilepicture = profilepicture;
        this.username = username;


    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getMediaurl() {
        return mediaurl;
    }

    public void setMediaurl(String mediaurl) {
        this.mediaurl = mediaurl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProfilepicture() {
        return profilepicture;
    }

    public void setProfilepicture(String profilepicture) {
        this.profilepicture = profilepicture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }




}

