
package com.joseonline.apps.cardenalito.fragments;

import com.loopj.android.http.JsonHttpResponseHandler;

public class HomeTimelineFragment extends TweetsListFragment {

    @Override
    protected void getTweets(String maxId, JsonHttpResponseHandler handler) {
        client.getHomeTimeline(maxId, handler);
    }

    @Override
    protected void getLatestTweets(String sinceId, JsonHttpResponseHandler handler) {
        client.refreshHomeTimeline(sinceId, handler);
    }

}
