package com.pratmodi.main.streamingapi;


import com.google.gson.Gson;
import com.pratmodi.main.configuration.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

@Component("streamingRealTime")
public class StreamingRealTime {

    @Autowired
    Config config;

    public void streamFeed() throws IOException, TwitterException {

        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

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

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(consumerKey);
        builder.setOAuthConsumerSecret(consumerSecret);
        builder.setOAuthAccessToken(accessToken);
        builder.setOAuthAccessTokenSecret(accessTokenSecret);

        Configuration configuration = builder.build();
        TwitterFactory factory = new TwitterFactory(configuration);
        Twitter twitter = factory.getInstance();

        //    twitterStream.setOAuthAccessToken(config.configSetup().getOAuthAccessToken());
        twitterStream.setOAuthConsumer(consumerKey,consumerSecret);
        twitterStream.setOAuthAccessToken(twitter.getOAuthAccessToken());

        StatusListener listener = new StatusListener() {

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
            @Override
            public void onDeletionNotice(StatusDeletionNotice arg) {
            }
            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
            }
            @Override
            public void onStallWarning(StallWarning warning) {
            }
            @Override
            public void onStatus(Status status) {
                String rawJSON = TwitterObjectFactory.getRawJSON(status);

                // System.out.println(rawJSON);
            //    writer.println(rawJSON);
                int counter = 0;
                FileWriter data = null;
                Gson gson = new Gson();
                StringBuilder jsonString = null;
                try {
                    while(counter<=5) {
                        counter++;
                        data = new FileWriter("C:\\Users\\pratm\\OneDrive\\Documents\\IDEA Intellij\\UBS\\ac41e4ba-375c-4bc7-b755-6ef39923aa97\\Twit4J\\Twitter.json", true);

                         jsonString = jsonString.append(rawJSON);
                        if(counter==5){
                            data.write(gson.toJson(jsonString));
                            twitterStream.cleanUp();
                            twitterStream.shutdown();
                            data.flush();
                            data.close();

                        }
                    }

                    System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText() + "Lat:"
                            + status.getGeoLocation().getLatitude() + "Long"
                            + status.getGeoLocation().getLongitude());
                    String s = "@" + status.getUser().getScreenName() + " - " + status.getText() + "Lat:"
                            + status.getGeoLocation().getLatitude() + "Long"
                            + status.getGeoLocation().getLongitude();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            }
        };

        twitterStream.addListener(listener);

        twitterStream.sample();

    }
}
