package com.pratmodi.main.model;

import java.io.Serializable;

public class TweetAttributes implements Serializable {

    private String tweetText;
    private String tweetID;

    public TweetAttributes() {
    }

    public String getTweetText() {
        return tweetText;
    }

    public void setTweetText(String tweetText) {
        this.tweetText = tweetText;
    }

    public String getTweetID() {
        return tweetID;
    }

    public void setTweetID(String tweetID) {
        this.tweetID = tweetID;
    }
}
