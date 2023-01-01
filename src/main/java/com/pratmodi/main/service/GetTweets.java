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

import static org.elasticsearch.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.xcontent.XContentFactory.xContent;

@Component("getTweets")
public class GetTweets {

    @Autowired
    ESConfiguration esConfiguration;

    @Autowired
    BasicService basicService;

    @Autowired
    private Config config;

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

   //     TweetBasicModel tbm = new TweetBasicModel();

        StringBuilder tweetID = new StringBuilder();
        StringBuilder tweetText = new StringBuilder();

        if (list != null) {
            for (Tweet tweet : list) {
                ///       System.out.println(tweet.getId());
                //       System.out.println(tweet.getText());
             //   tweetID.append(tweet.getId()+" ");
             //   tweetText.append(tweet.getText()+" ");
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
            Tweet tweet = new Tweet();

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
                // convert your list to json
                String jsonCartList = gson.toJson(list);

                System.out.println("()()()()()()()()()(()()()()(" + jsonCartList);
        //        TweetAttributes s = gson.fromJson(String.valueOf(jsonCartList), TweetAttributes.class);


                Type collectionType = new TypeToken<Collection<TweetAttributes>>(){}.getType();
                Collection<TweetAttributes> enums = gson.fromJson(jsonCartList, collectionType);

//                Type collectionType = new TypeToken<List<TweetAttributes>>(){}.getType();
//                List<TweetAttributes> lcs = (List<TweetAttributes>) new Gson()
//                        .fromJson( jsonCartList , collectionType);
//
                ObjectMapper objectMapper = new ObjectMapper();
                List<TweetAttributes> data = objectMapper.readValue(objectMapper.writeValueAsString(enums), new TypeReference<List<TweetAttributes>>() {});
//
//                System.out.println("+++++++++++++++++++++"+" TEST: "+" REACHED HERE "+data.get(0).getTweetText()+"+++++++++++++++++++++");

                //    Tweet item = gson.fromJson(String.valueOf(list), Tweet.class);

                XContentBuilder builder = null;
                for (int i = 0; i < list.size()-1; i++) {
                    builder = jsonBuilder()
                            .startObject()
                            .field(/*tweet.getId() != null ? tweet.getId() :*/ "tweetID", data.get(i).getTweetID())
                            .field(/*tweet.getText() != null ? tweet.getText() :*/ "tweet", data.get(i).getTweetText())
                            .endObject().prettyPrint();
                }

                IndexRequest indexRequest = new IndexRequest("user");
//                String result = Strings.toString(builder);
                indexRequest.source(jsonCartList.replace("."," "), String.class);

                IndexResponse response = esConfiguration.getESClient().index(indexRequest, RequestOptions.DEFAULT);

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

//            ObjectMapper objectMapper = new ObjectMapper();
//            tweetBasicModel =  objectMapper.readValue(tweetBasicModel.getId()+tweetBasicModel.getText(), TweetBasicModel.class);
//
//            System.out.println("objectMapper.writeValueAsString: "+objectMapper.writeValueAsString(tweetBasicModel));

            XContentBuilder xcb = XContentFactory.jsonBuilder().prettyPrint();

            xcb.startObject();
            xcb.startObject("tweets");
            //  xcb.startObject("Rechnungsdatum");
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

}
