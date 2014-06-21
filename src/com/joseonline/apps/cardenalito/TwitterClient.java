
package com.joseonline.apps.cardenalito;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class TwitterClient extends OAuthBaseClient {

    public TwitterClient(Context context) {
        super(context, TwitterClientSettings.REST_API_CLASS, TwitterClientSettings.REST_URL,
                TwitterClientSettings.REST_CONSUMER_KEY,
                TwitterClientSettings.REST_CONSUMER_SECRET,
                TwitterClientSettings.REST_CALLBACK_URL);
    }

    public void getHomeTimeline(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("/statuses/home_timeline.json");

        RequestParams params = new RequestParams();
        params.put("since_id", "1");

        client.get(apiUrl, params, handler);
    }
}
