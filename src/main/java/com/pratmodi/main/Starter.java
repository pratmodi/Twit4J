package com.pratmodi.main;

import com.pratmodi.main.service.GetTweets;
import com.pratmodi.main.streamingapi.StreamingRealTime;
import com.twitter.clientlib.ApiException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import twitter4j.TwitterException;

import java.io.IOException;

@SpringBootApplication(scanBasePackages = {"com.pratmodi"},exclude = {DataSourceAutoConfiguration.class})
public class Starter {

    public static void main(String[] args) throws IOException, TwitterException, ApiException {
        ApplicationContext ctx= SpringApplication.run(Starter.class, args);
//        StreamingRealTime srt = (StreamingRealTime) ctx.getBean("streamingRealTime");
//        srt.streamFeed();

        GetTweets getTweets = (GetTweets) ctx.getBean("getTweets");
        getTweets.getTweetsByQuery();
    }

}
