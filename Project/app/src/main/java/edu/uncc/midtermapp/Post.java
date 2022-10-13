package edu.uncc.midtermapp;

public class Post {
    String created_by_name, post_id, created_by_uid, post_text, created_at;

    public Post(String created_by_name, String post_id, String created_by_uid, String post_text, String created_at) {
        this.created_by_name = created_by_name;
        this.post_id = post_id;
        this.created_by_uid = created_by_uid;
        this.post_text = post_text;
        this.created_at = created_at;
    }

    public Post() {
        //does nothing
    }

    public String getPost() {
        return post_text;
    }

    public String getPostName() {
        return created_by_name;
    }

    public String getTime() {
        return created_at;
    }

    public String getPostID() {
        return post_id;
    }

}
