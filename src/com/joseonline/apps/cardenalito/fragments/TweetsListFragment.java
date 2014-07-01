
package com.joseonline.apps.cardenalito.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.joseonline.apps.cardenalito.CardenalitoApplication;
import com.joseonline.apps.cardenalito.R;
import com.joseonline.apps.cardenalito.TwitterClient;
import com.joseonline.apps.cardenalito.activities.ComposeActivity;
import com.joseonline.apps.cardenalito.activities.ProfileActivity;
import com.joseonline.apps.cardenalito.activities.TweetDetailActivity;
import com.joseonline.apps.cardenalito.adapters.TweetArrayAdapter;
import com.joseonline.apps.cardenalito.adapters.TweetArrayAdapter.OnTweetClickListener;
import com.joseonline.apps.cardenalito.helpers.DeleteAndSaveTweetsTask;
import com.joseonline.apps.cardenalito.helpers.EndlessScrollListener;
import com.joseonline.apps.cardenalito.helpers.NetworkUtils;
import com.joseonline.apps.cardenalito.helpers.SaveTweetsTask;
import com.joseonline.apps.cardenalito.models.Tweet;
import com.joseonline.apps.cardenalito.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public abstract class TweetsListFragment extends Fragment implements OnTweetClickListener {

    protected TwitterClient client;

    private ArrayList<Tweet> tweets;
    private ArrayAdapter<Tweet> aTweets;
    private PullToRefreshListView lvTimeline;
    private LinearLayout linlaHeaderProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = CardenalitoApplication.getRestClient();

        tweets = new ArrayList<Tweet>();
        aTweets = new TweetArrayAdapter(getActivity(), tweets, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);

        // Assign view references
        setupViews(v);

        setupListeners();

        populateTimeline(null);

        // return the layout view
        return v;
    }

    private void setupViews(View v) {
        // ListView
        lvTimeline = (PullToRefreshListView) v.findViewById(R.id.lvTimeline);
        lvTimeline.setAdapter(aTweets);

        // Progress bar
        linlaHeaderProgress = (LinearLayout) v.findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.VISIBLE);
    }

    private void setupListeners() {
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
                    linlaHeaderProgress.setVisibility(View.VISIBLE);
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
        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            getTweets(maxId, getTweetsHandler());
        } else {
            if (maxId == null) {
                // First load, get cache tweets from db if any
                List<Tweet> cacheTweets = Tweet.getAll();

                // Only display if there's actually data returned back
                if (cacheTweets.size() > 0) {
                    aTweets.addAll(cacheTweets);
                    Toast.makeText(getActivity(), "Cache tweet view. No internet connectivity",
                            Toast.LENGTH_SHORT).show();
                    linlaHeaderProgress.setVisibility(View.GONE);
                    return;
                }
            }
            // No internet connectivity error
            Toast.makeText(getActivity(), getString(R.string.no_internet_error_msg),
                    Toast.LENGTH_SHORT)
                    .show();
            linlaHeaderProgress.setVisibility(View.GONE);
        }
    }

    private void refreshTimeline(String sinceId) {
        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            getLatestTweets(sinceId, getLatestTweetsHandler());
        } else {
            // No internet connectivity error
            Toast.makeText(getActivity(), getString(R.string.no_internet_error_msg),
                    Toast.LENGTH_SHORT)
                    .show();
            lvTimeline.onRefreshComplete();
        }
    }

    /**
     * @param maxId
     * @param handler
     */
    protected abstract void getTweets(String maxId, JsonHttpResponseHandler handler);

    /**
     * @param sinceId
     * @param handler
     */
    protected abstract void getLatestTweets(String sinceId, JsonHttpResponseHandler handler);

    private JsonHttpResponseHandler getTweetsHandler() {
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray json) {
                Log.d("DEBUG", json.toString());

                ArrayList<Tweet> tweetArray = Tweet.fromJSONArray(json);
                aTweets.addAll(tweetArray);
                saveData(tweetArray);
                linlaHeaderProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable e, String s) {
                Log.d("DEBUG", e.toString());
                Log.d("DEBUG", s.toString());
                Toast.makeText(getActivity(), getString(R.string.remote_call_error_msg),
                        Toast.LENGTH_LONG).show();
                linlaHeaderProgress.setVisibility(View.GONE);
            }
        };
    }

    private JsonHttpResponseHandler getLatestTweetsHandler() {
        return new JsonHttpResponseHandler() {
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

                    Toast.makeText(getActivity(), "You are now up-to-date",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Things look quiet in Twitter",
                            Toast.LENGTH_SHORT).show();
                }
                lvTimeline.onRefreshComplete();
            }

            @Override
            public void onFailure(Throwable e, String s) {
                Log.d("DEBUG", s.toString());
                Log.d("DEBUG", "Fetch error: " + e.toString());
                Toast.makeText(getActivity(), getString(R.string.remote_call_error_msg),
                        Toast.LENGTH_SHORT).show();
                lvTimeline.onRefreshComplete();
            }
        };
    }

    // Delegate the adding to the internal adapter
    public void addAll(ArrayList<Tweet> tweets) {
        aTweets.addAll(tweets);
    }

    public void insertTop(Tweet tweet) {
        aTweets.insert(tweet, 0);
    }

    private void saveData(ArrayList<Tweet> tweets) {
        new DeleteAndSaveTweetsTask().execute(tweets);
    }

    @Override
    public void onProfileImageClick(User user) {
        Toast.makeText(getActivity(), "User: " + user.getScreenName(), Toast.LENGTH_SHORT).show();

        Intent i = new Intent(getActivity(), ProfileActivity.class);
        i.putExtra(User.USER_KEY, user);
        startActivity(i);
    }

    @Override
    public void onFavoriteClick(int pos, boolean isChecked) {
        Tweet tweet = (Tweet) aTweets.getItem(pos);
        
        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            String tweetId = String.valueOf(tweet.getUid());
            if (isChecked) {
                client.favoriteTweet(tweetId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, JSONObject jsonObject) {
                        Toast.makeText(getActivity(), "Tweet favorite", Toast.LENGTH_SHORT).show();
                        updateTweet(jsonObject);
                    }

                    @Override
                    public void onFailure(Throwable e, String s) {
                        Log.d("debug", e.toString());
                        Log.d("debug", s.toString());
                        Toast.makeText(getActivity(), "Problem favoriting the tweet. Try again",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                client.unfavoriteTweet(tweetId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, JSONObject jsonObject) {
                        Toast.makeText(getActivity(), "Tweet un-favorite", Toast.LENGTH_SHORT)
                                .show();
                        updateTweet(jsonObject);
                    }
                    
                    @Override
                    public void onFailure(Throwable e, String s) {
                        Log.d("debug", e.toString());
                        Log.d("debug", s.toString());
                        Toast.makeText(getActivity(), "Problem favoriting the tweet. Try again",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            // No internet connectivity error
            Toast.makeText(getActivity(), getString(R.string.no_internet_error_msg),
                    Toast.LENGTH_SHORT).show();
        }
    }
    
    private void updateTweet(JSONObject jsonObject) {
        Tweet updateTweet = Tweet.fromJSON(jsonObject);
        int pos = aTweets.getPosition(updateTweet);
        aTweets.insert(updateTweet, pos);
        aTweets.notifyDataSetChanged();
    }
}
