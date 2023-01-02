package com.pratmodi.main.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pratmodi.main.model.TweetAttributes;
import com.pratmodi.main.model.TweetBasicModel;
import com.pratmodi.main.configuration.Config;
import com.pratmodi.main.es.ESConfiguration;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.api.TweetsApi;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.Tweet;
import org.apache.commons.text.StringEscapeUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.Strings;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.elasticsearch.xcontent.XContentType;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import twitter4j.Twitter;
import twitter4j.JSONObject;

import java.io.*;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.elasticsearch.xcontent.XContentFactory.*;

@Component("getTweets")
public class GetTweets {

    @Autowired
    ESConfiguration esConfiguration;

    @Autowired
    BasicService basicService;

    @Value("${bearer.Token}")
    private String bearerToken;

    public List<Tweet> getTweetsByQuery() throws IOException, ApiException {
        Twitter twitter;
        TwitterCredentialsBearer credentials = new TwitterCredentialsBearer(bearerToken);
        TwitterApi apiInstance = new TwitterApi(credentials);

        String query = "covid -is:retweet";

        int maxResults = 100;

        TweetsApi.APItweetsRecentSearchRequest get2TweetsSearchAllResponse = apiInstance.tweets().tweetsRecentSearch(query).maxResults(maxResults);

        List<Tweet> list = get2TweetsSearchAllResponse.execute().getData();

        JSONObject jsonObject = new JSONObject(list);

        StringBuilder tweetID = new StringBuilder();
        StringBuilder tweetText = new StringBuilder();

        if (list != null) {
            for (Tweet tweet : list) {
                tweetID.append(tweet.getId());
                tweetText.append(tweet.getText());
            }
        }

//        String str1 = tweetID.toString();
//        String str2 = tweetText.toString();
//        String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
//        String emotionless1 = str1.replaceAll(characterFilter,"");
//        String emotionless2 = str2.replaceAll(characterFilter,"");
//        tbm.setId(emotionless1);
//        tbm.setText(emotionless2);

        this.persistAll(list);
    //        this.saveToES(tbm,maxResults);
        return list;
    //    return tbm;
    }

    public List<Tweet> convertTweetTextToListUsingObjectMapper(String tweetText) throws JSONException, JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        List<Tweet> listOfTweets = new ArrayList<>();
        listOfTweets = mapper.readValue(tweetText, new TypeReference<List<Tweet>>() {
        });

        return listOfTweets;
    }

//    public void persistAll(JSONObject object/*String str*/) throws IOException {
//        try {
//            Tweet tweet = new Tweet();
//
//            Gson gson = new GsonBuilder()
//                    .setLenient()
//                    .create();
//
//            Tweet item = gson.fromJson(String.valueOf(object), Tweet.class);
//
//            JSONObject jo = new JSONObject(item);
//            JSONArray jsonArray = new JSONArray();
//            jsonArray.put(jo);
//
//            XContentBuilder builder = jsonBuilder()
//                    .startObject()
//                    .field(tweet.getId() != null ? tweet.getId() : "", item.getId())
//                    .field(tweet.getText() != null ? tweet.getText() : "null", item.getText())
//                    .endObject().prettyPrint();
//
//            IndexRequest indexRequest = new IndexRequest("user"/*esConfiguration.getES_INDEX()*/);
//
//            String result = Strings.toString(builder);
//
//            indexRequest.source(result, Tweet.class);
//
//            IndexResponse response = esConfiguration.getESClient().index(indexRequest, RequestOptions.DEFAULT);
//
//            System.out.println("^^^^^^^^&&&&&&&&&&&&&&^^^^^^^^^^" + response.getIndex() + "    " + response.getResult() + "    " + response.getId() + "    " + "^^^^^^^^&&&&&&&&&&&&&&^^^^^^^^^^");
//
//        } catch (IllegalStateException | JsonSyntaxException exception) {
//            System.out.println(" EXCEPTION OCCURRED!!!!!!!!!!!!" + exception);
//        }
//    }

//    public List<Tweet> getTweetsByQuery() throws IOException, ApiException {
//        Twitter twitter;
//        TwitterCredentialsBearer credentials = new TwitterCredentialsBearer(bearerToken);
//        TwitterApi apiInstance = new TwitterApi(credentials);
//
//        String query = "covid -is:retweet";
//
//        TweetsApi.APItweetsRecentSearchRequest get2TweetsSearchAllResponse = apiInstance.tweets().tweetsRecentSearch(query).maxResults(100);
//
//        List<Tweet> list = get2TweetsSearchAllResponse.execute().getData();
//
//        if (list != null) {
//            for (Tweet tweet : list) {
//                       System.out.println(tweet.getId());
//                       System.out.println(tweet.getText());
//            }
//
//        }
//        this.persistAll(list);
//        return list;
//    }
//

    public void persistAll(List<Tweet> list/*String str*/) throws IOException {
        try {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            String jsonString = null;
            String finalJSON = null;
            StringBuilder temp = new StringBuilder();
            for(int i=0;i<list.size();i++) {
                jsonString = String.valueOf(list.get(i));
                temp.append(gson.toJson(jsonString));
            }
            finalJSON = StringEscapeUtils.unescapeJava(temp.toString());
            try {

                String jsonCartList = gson.toJson(list);

                System.out.println("()()()()()()()()()(()()()()(" + jsonCartList);



                Type collectionType = new TypeToken<Collection<TweetAttributes>>(){}.getType();
                Collection<TweetAttributes> enums = gson.fromJson(jsonCartList, collectionType);


                ObjectMapper objectMapper = new ObjectMapper();
                List<TweetAttributes> data = objectMapper.readValue(objectMapper.writeValueAsString(enums), new TypeReference<List<TweetAttributes>>() {});

                XContentBuilder builder = jsonBuilder().startObject();
//        //        for (int i = 0; i < list.size()-1; i++) {
//                    builder = jsonBuilder()
//                            .startObject()
//                            .field(/*tweet.getId() != null ? tweet.getId() :*/ "tweetID", enums.toString())
//                            .field(/*tweet.getText() != null ? tweet.getText() :*/ "tweet", enums.toString())
//                            .endObject().prettyPrint();
//
//        //        }

                builder.startArray("tweets");
                for(int j=0;j<list.size();j++) {
                    builder.startObject();
                    builder.field("tweetID", data.get(j).getId());
                    builder.field("tweet", data.get(j).getText());
                    builder.endObject();
                }
                builder.endArray();
                builder.endObject();

                IndexRequest indexRequest = new IndexRequest("defaultbest");
                String result = Strings.toString(builder);
                indexRequest.source(result.replace(".",""), TweetAttributes.class);

                IndexResponse response = esConfiguration.getESClient().index(indexRequest, RequestOptions.DEFAULT);
                this.saveToFile(jsonCartList);
                System.out.println("^^^^^^^^&&&&&&&&&&&&&&^^^^^^^^^^" + response.getIndex() + "    " + response.getResult() + "    " + response.getId() + "    " + "^^^^^^^^&&&&&&&&&&&&&&^^^^^^^^^^");
            }catch (Exception e){
                System.out.println("NEWJSON EXCEPTION: "+e);
            }
        } catch (IllegalStateException | JsonSyntaxException exception) {
            System.out.println(" EXCEPTION OCCURRED!!!!!!!!!!!!"+exception);
        }
    }

    public void saveToES(TweetBasicModel tweetBasicModel, int maxResults) throws IOException {
        try {

            XContentBuilder xcb = XContentFactory.jsonBuilder().prettyPrint();

            xcb.startObject();
            xcb.startObject("tweets");

            xcb.field(tweetBasicModel.getId() != null ? tweetBasicModel.getId() : "", "string");
            xcb.field(tweetBasicModel.getText() != null ? tweetBasicModel.getText() : "", "string");
            //   xcb.endObject();
            xcb.endObject();
            xcb.endObject();

            IndexRequest indexRequest = new IndexRequest("user").source(xcb, XContentType.JSON);
            //indexRequest.source(json, TweetBasicModel.class);

            IndexResponse response = esConfiguration.getESClient().index(indexRequest, RequestOptions.DEFAULT);

            System.out.println("^^^^^^^^&&&&&&&&&&&&&&^^^^^^^^^^" + response.getIndex() + "    " + response.getResult() + "    " + response.getId() + "    " + "^^^^^^^^&&&&&&&&&&&&&&^^^^^^^^^^");

        } catch (IllegalStateException | JsonSyntaxException exception) {
            System.out.println(" EXCEPTION OCCURRED!!!!!!!!!!!!" + exception);
        }

    }

    public void saveToFile(String jsonString) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();

        FileWriter recipesFile = new FileWriter("C:\\Users\\pratm\\OneDrive\\Documents\\IDEA Intellij\\UBS\\ac41e4ba-375c-4bc7-b755-6ef39923aa97\\Twit4J\\TwitterExtract.json", true);

        //    this.persistAll(jsonString);
        recipesFile.write(jsonString);
        recipesFile.flush();
        recipesFile.close();
    }

}
