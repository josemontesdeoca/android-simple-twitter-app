package com.joseonline.apps.cardenalito.fragments;

import android.os.Bundle;
import android.widget.Toast;

import com.joseonline.apps.cardenalito.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

public class UserTimelineFragment extends TweetsListFragment {
    
    private User user;
    
    public static UserTimelineFragment newInstance(User user) {
        UserTimelineFragment fragmentUserTimeline = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putSerializable(User.USER_KEY, user);
        fragmentUserTimeline.setArguments(args);
        return fragmentUserTimeline;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getArguments().getSerializable(User.USER_KEY);
        
    }

    @Override
    protected void getTweets(String maxId, JsonHttpResponseHandler handler) {
        client.getUserTimeline(user.getScreenName(), maxId, handler);
    }

    @Override
    protected void getLatestTweets(String sinceId, JsonHttpResponseHandler handler) {
    }
    
    @Override
    public void onProfileImageClick(User user) {
        Toast.makeText(getActivity(), "Naughty you!", Toast.LENGTH_SHORT).show();
    }

}
