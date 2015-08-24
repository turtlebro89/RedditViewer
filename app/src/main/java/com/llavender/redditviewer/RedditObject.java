package com.llavender.redditviewer;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lsl017 on 8/10/2015.
 */
public class RedditObject {

    String subreddit;
    String title;
    String author;
    int score;
    int num_comments;
    String permalink;
    String url;
    String domain;
    String id;
    @SerializedName("thumbnail")
    String thumbnail;
    boolean over18;
    String display_name;

    public String getDetails(){
        return author + "posted this and got " + num_comments + "replies";
    }

    public String getTitle(){
        return title;
    }

    public String getScore(){
        return String.valueOf(score);
    }
}
