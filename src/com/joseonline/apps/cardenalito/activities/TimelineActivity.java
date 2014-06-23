
package com.joseonline.apps.cardenalito.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.joseonline.apps.cardenalito.CardenalitoApplication;
import com.joseonline.apps.cardenalito.R;
import com.joseonline.apps.cardenalito.TwitterClient;
import com.joseonline.apps.cardenalito.adapters.TweetArrayAdapter;
import com.joseonline.apps.cardenalito.helpers.EndlessScrollListener;
import com.joseonline.apps.cardenalito.models.Tweet;
import com.joseonline.apps.cardenalito.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class TimelineActivity extends Activity {
    public static final int COMPOSE_TWEET_REQUEST_CODE = 1;
    public static final String AUTHENTICATED_USER_KEY = "authenticatedUser";

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private ArrayAdapter<Tweet> aTweets;

    private PullToRefreshListView lvTimeline;
    private User authenticatedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = CardenalitoApplication.getRestClient();

        getAuthenticatedUser();

        setupViews();

        tweets = new ArrayList<Tweet>();
        aTweets = new TweetArrayAdapter(this, tweets);
        lvTimeline.setAdapter(aTweets);

        populateTimeline(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }

    private void getAuthenticatedUser() {
        client.getAuthenticatedUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                authenticatedUser = User.fromJSON(jsonObject);
            }

            @Override
            public void onFailure(Throwable e, String s) {
                Log.d("DEBUG", e.toString());
                Log.d("DEBUG", s.toString());
            }
        });

    }

    private void setupViews() {
        lvTimeline = (PullToRefreshListView) findViewById(R.id.lvTimeline);

        // Endless scrolling
        lvTimeline.setOnScrollListener(new EndlessScrollListener() {

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!aTweets.isEmpty()) {
                    Tweet oldestTweet = aTweets.getItem(aTweets.getCount() - 1);
                    Long oldestTweetId = oldestTweet.getUid();

                    populateTimeline(String.valueOf(oldestTweetId - 1));
                }
            }
        });

        // Pull to refresh listener
        lvTimeline.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                Tweet newestTweet = aTweets.getItem(0);
                Long newstTweetId = newestTweet.getUid();

                refreshTimeline(String.valueOf(newstTweetId));
            }
        });
    }

    public void populateTimeline(String maxId) {
        client.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray json) {
                Log.d("DEBUG", json.toString());
                
                ArrayList<Tweet> tweetArray = Tweet.fromJSONArray(json);
                aTweets.addAll(tweetArray);
                saveData(tweetArray);
            }

            @Override
            public void onFailure(Throwable e, String s) {
                Log.d("DEBUG", e.toString());
                Log.d("DEBUG", s.toString());
            }
        });
    }

    private void refreshTimeline(String sinceId) {
        client.refreshHomeTimeline(sinceId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                Log.d("DEBUG", jsonArray.toString());

                // TODO: Improve pagination by making sure all tweets between since_id and the
                // refresh are fetch
                // https://dev.twitter.com/docs/working-with-timelines

                ArrayList<Tweet> newTweets = Tweet.fromJSONArray(jsonArray);

                if (!newTweets.isEmpty()) {
                    for (int i = 0; i < newTweets.size(); i++) {
                        aTweets.insert(newTweets.get(i), i);
                    }
                    saveData(newTweets);
                    Toast.makeText(TimelineActivity.this, "You are now up-to-date",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TimelineActivity.this, "Things look quiet in Twitter",
                            Toast.LENGTH_SHORT).show();
                }

                lvTimeline.onRefreshComplete();
            }

            @Override
            public void onFailure(Throwable e, String s) {
                Log.d("DEBUG", s.toString());
                Log.d("DEBUG", "Fetch error: " + e.toString());
                Toast.makeText(TimelineActivity.this, "Twitter is funky. Try again",
                        Toast.LENGTH_SHORT).show();
                lvTimeline.onRefreshComplete();
            }
        });
    }

    public void onCompose(MenuItem item) {
        Intent i = new Intent(this, ComposeActivity.class);
        i.putExtra(AUTHENTICATED_USER_KEY, authenticatedUser);
        startActivityForResult(i, COMPOSE_TWEET_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == COMPOSE_TWEET_REQUEST_CODE) {
            Tweet newTweet = (Tweet) data.getSerializableExtra(Tweet.TWEET_KEY);
            aTweets.insert(newTweet, 0);
        }
    }
    
    private void saveData(ArrayList<Tweet> tweets) {
        Log.d("DEBUG", "Saving Tweets and User data");
        for (Tweet tweet : tweets) {
            tweet.getUser().save();
            tweet.save();
        }
    }
}
