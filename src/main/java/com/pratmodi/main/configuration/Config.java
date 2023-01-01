package com.pratmodi.main.configuration;

import org.springframework.context.annotation.Configuration;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.*;
import java.util.Properties;

@Configuration
public class Config {

    public Twitter configSetup() throws IOException {

        Properties properties = new Properties();
        String confPath = "twit4j.properties";

        try {
            properties.load(new FileInputStream(confPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String consumerKey = properties.getProperty("oauth.consumerKey");
        String consumerSecret = properties.getProperty("oauth.consumerSecret");
        String accessToken = properties.getProperty("oauth.accessToken");
        String accessTokenSecret = properties.getProperty("oauth.accessTokenSecret");

    //    Set<Object> allKeys = properties.keySet();
    //    Collection<Object> values = properties.values();



        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);

        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        return twitter;
    }

}
