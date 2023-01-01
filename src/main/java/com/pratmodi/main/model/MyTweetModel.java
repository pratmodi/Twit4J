package com.pratmodi.main.model;

import com.twitter.clientlib.model.Tweet;

import java.io.Serializable;
import java.util.List;

public class MyTweetModel  implements Serializable {

    private List<TweetAttributes> tweetList;

    public MyTweetModel() {
    }

    public List<TweetAttributes> getTweetList() {
        return tweetList;
    }

    public void setTweetList(List<TweetAttributes> tweetList) {
        this.tweetList = tweetList;
    }
}
