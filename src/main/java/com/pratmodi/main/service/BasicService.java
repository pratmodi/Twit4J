package com.pratmodi.main.service;


import com.pratmodi.main.configuration.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import twitter4j.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BasicService {

    @Autowired
    private Config config;

    private Twitter twitter;

    public String createTweet(String tweet) throws TwitterException, IOException {
   //     Twitter twitter = getTwitterinstance();
        this.twitter = config.configSetup();

        Status status = twitter.updateStatus("creating baeldung API");
        return status.getText();
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public void setTwitter(Twitter twitter) {
        this.twitter = twitter;
    }

    public List<String> getTimeLine() throws TwitterException, IOException {
        twitter = config.configSetup();

        return twitter.getHomeTimeline().stream()
                .map(item -> item.getText())
                .collect(Collectors.toList());
    }

    public String sendDirectMessage(String recipientName, String msg)
            throws TwitterException, IOException {

        twitter = config.configSetup();
        DirectMessage message = twitter.sendDirectMessage(recipientName, msg);
        return message.getText();
    }

    public List<String> searchtweets() throws TwitterException, IOException {

        twitter = config.configSetup();
        Query query = new Query("source:twitter4j baeldung");
        QueryResult result = twitter.search(query);

        return result.getTweets().stream()
                .map(item -> item.getText())
                .collect(Collectors.toList());
    }

}
