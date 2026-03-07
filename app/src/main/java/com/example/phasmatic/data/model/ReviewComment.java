package com.example.phasmatic.data.model;

public class ReviewComment {

    public String id;
    public String review_id;
    public String user_id;
    public String user_name;
    public String academic_profile;
    public String comment_text;
    public String created_at;

    public ReviewComment() {
    }

    public ReviewComment(String id, String review_id, String user_id, String user_name, String comment_text, String created_at) {
        this.id = id;
        this.review_id = review_id;
        this.user_id = user_id;
        this.user_name = user_name;
        this.comment_text = comment_text;
        this.created_at = created_at;
    }
}
