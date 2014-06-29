package com.joseonline.apps.cardenalito.fragments;

import com.loopj.android.http.JsonHttpResponseHandler;

public class UserTimelineFragment extends TweetsListFragment {

    @Override
    protected void getTweets(String maxId, JsonHttpResponseHandler handler) {
        client.getUserTimeline(maxId, handler);
    }

    @Override
    protected void getLatestTweets(String sinceId, JsonHttpResponseHandler handler) {
    }

}
