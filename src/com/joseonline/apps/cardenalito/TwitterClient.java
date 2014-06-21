
package com.joseonline.apps.cardenalito;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class TwitterClient extends OAuthBaseClient {
    private static final String COUNT_PARAMETER = "count";
    private static final String MAX_ID_PARAMETER = "max_id";
    
    private static final String HOME_TIMELINE_PATH = "/statuses/home_timeline.json";

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
}
