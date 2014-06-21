
package com.joseonline.apps.cardenalito.activities;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.activeandroid.util.Log;
import com.joseonline.apps.cardenalito.CardenalitoApplication;
import com.joseonline.apps.cardenalito.R;
import com.joseonline.apps.cardenalito.TwitterClient;
import com.joseonline.apps.cardenalito.adapters.TweetArrayAdapter;
import com.joseonline.apps.cardenalito.helpers.EndlessScrollListener;
import com.joseonline.apps.cardenalito.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class TimelineActivity extends Activity {
    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private ArrayAdapter<Tweet> aTweets;

    private ListView lvTimeline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = CardenalitoApplication.getRestClient();

        setupViews();

        tweets = new ArrayList<Tweet>();
        aTweets = new TweetArrayAdapter(this, tweets);
        lvTimeline.setAdapter(aTweets);

        populateTimeline(null);
    }

    private void setupViews() {
        lvTimeline = (ListView) findViewById(R.id.lvTimeline);

        // Endless scrolling
        lvTimeline.setOnScrollListener(new EndlessScrollListener() {
            
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Tweet oldestTweet = aTweets.getItem(aTweets.getCount() - 1);
                Long oldestTweetId = oldestTweet.getUid();
                
                populateTimeline(String.valueOf(oldestTweetId -1));
            }
        });
    }

    public void populateTimeline(String maxId) {
        client.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray json) {
                Log.d("debug", json.toString());
                aTweets.addAll(Tweet.fromJSONArray(json));
            }

            @Override
            public void onFailure(Throwable e, String s) {
                Log.d("debug", e.toString());
                Log.d("debug", s.toString());
            }
        });
    }
}
