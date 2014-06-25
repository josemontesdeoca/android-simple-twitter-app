
package com.joseonline.apps.cardenalito.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.joseonline.apps.cardenalito.CardenalitoApplication;
import com.joseonline.apps.cardenalito.R;
import com.joseonline.apps.cardenalito.TwitterClient;
import com.joseonline.apps.cardenalito.adapters.TweetArrayAdapter;
import com.joseonline.apps.cardenalito.helpers.DeleteAndSaveTweetsTask;
import com.joseonline.apps.cardenalito.helpers.EndlessScrollListener;
import com.joseonline.apps.cardenalito.helpers.NetworkUtils;
import com.joseonline.apps.cardenalito.helpers.SaveTweetsTask;
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
    private LinearLayout linlaHeaderProgress;

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
        // Progress bar
        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.VISIBLE);

        // ListView
        lvTimeline = (PullToRefreshListView) findViewById(R.id.lvTimeline);

        // OnItemClickListener
        lvTimeline.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View parent, int position, long rowId) {
                Intent i = new Intent(parent.getContext(), TweetDetailActivity.class);
                Tweet tweet = tweets.get(position);
                i.putExtra(Tweet.TWEET_KEY, tweet);
                startActivity(i);
            }
        });

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
        if (NetworkUtils.isNetworkAvailable(this)) {
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
        } else {
            if (maxId == null) {
                // First load, get cache tweets from db if any
                List<Tweet> cacheTweets = Tweet.getAll();

                // Only display if there's actually data returned back
                if (cacheTweets.size() > 0) {
                    aTweets.addAll(cacheTweets);
                    Toast.makeText(this, "Cache tweet view. No internet connectivity",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            // No internet connectivity error
            Toast.makeText(this, "No internet connectivity. Try again", Toast.LENGTH_SHORT).show();
        }
        linlaHeaderProgress.setVisibility(View.GONE);
    }

    private void refreshTimeline(String sinceId) {
        if (NetworkUtils.isNetworkAvailable(this)) {
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

                        // Store them on db
                        new SaveTweetsTask().execute(newTweets);

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
        } else {
            // No internet connectivity error
            Toast.makeText(this, "Network is acting up. Try again", Toast.LENGTH_SHORT).show();
            lvTimeline.onRefreshComplete();
        }
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
        new DeleteAndSaveTweetsTask().execute(tweets);
    }
}
