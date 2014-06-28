
package com.joseonline.apps.cardenalito;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class TwitterClient extends OAuthBaseClient {
    private static final String COUNT_PARAMETER = "count";
    private static final String MAX_ID_PARAMETER = "max_id";
    private static final String SINCE_ID_PARAMETER = "since_id";
    private static final String STATUS_PARAMETER = "status";

    private static final String HOME_TIMELINE_PATH = "/statuses/home_timeline.json";
    private static final String ACCOUNT_VERIFY_CREDENTIALS_PATH = "/account/verify_credentials.json";
    private static final String POST_TWEET_PATH = "/statuses/update.json";
    private static final String MENTIONS_TIMELINE_PATH = "/statuses/mentions_timeline.json";

    public TwitterClient(Context context) {
        super(context, TwitterClientSettings.REST_API_CLASS, TwitterClientSettings.REST_URL,
                TwitterClientSettings.REST_CONSUMER_KEY,
                TwitterClientSettings.REST_CONSUMER_SECRET,
                TwitterClientSettings.REST_CALLBACK_URL);
    }

    public void getHomeTimeline(String maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(HOME_TIMELINE_PATH);

        RequestParams params = new RequestParams();
        params.put(COUNT_PARAMETER, "20");

        if (maxId != null) {
            params.put(MAX_ID_PARAMETER, maxId);
        }

        client.get(apiUrl, params, handler);
    }
    
    public void refreshHomeTimeline(String sinceId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(HOME_TIMELINE_PATH);

        RequestParams params = new RequestParams();
        params.put(COUNT_PARAMETER, "20");
        params.put(SINCE_ID_PARAMETER, sinceId);
        
        client.get(apiUrl, params, handler);
    }
    
    public void getMentionsTimeline(String maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(MENTIONS_TIMELINE_PATH);

        RequestParams params = new RequestParams();
        params.put(COUNT_PARAMETER, "20");

        if (maxId != null) {
            params.put(MAX_ID_PARAMETER, maxId);
        }

        client.get(apiUrl, params, handler);
    }
    
    public void refreshMentionsTimeline(String sinceId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(MENTIONS_TIMELINE_PATH);

        RequestParams params = new RequestParams();
        params.put(COUNT_PARAMETER, "20");
        params.put(SINCE_ID_PARAMETER, sinceId);
        
        client.get(apiUrl, params, handler);
    }

    public void getAuthenticatedUser(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(ACCOUNT_VERIFY_CREDENTIALS_PATH);
        client.get(apiUrl, handler);
    }
    
    public void postTweet(String tweet, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(POST_TWEET_PATH);
        
        RequestParams params = new RequestParams();
        params.put(STATUS_PARAMETER, tweet);
        
        client.post(apiUrl, params, handler);
    }
}
