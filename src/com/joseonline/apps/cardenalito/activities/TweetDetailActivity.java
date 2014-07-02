
package com.joseonline.apps.cardenalito.activities;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joseonline.apps.cardenalito.CardenalitoApplication;
import com.joseonline.apps.cardenalito.R;
import com.joseonline.apps.cardenalito.TwitterClient;
import com.joseonline.apps.cardenalito.fragments.RetweetDialogFragment;
import com.joseonline.apps.cardenalito.fragments.RetweetDialogFragment.onButtonClickListener;
import com.joseonline.apps.cardenalito.helpers.DateUtil;
import com.joseonline.apps.cardenalito.helpers.NetworkUtils;
import com.joseonline.apps.cardenalito.models.Tweet;
import com.joseonline.apps.cardenalito.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetDetailActivity extends FragmentActivity implements onButtonClickListener {

    private Tweet tweet;
    private ImageView ivProfileImage;
    private TextView tvUserName;
    private TextView tvUserScreenName;
    private TextView tvTweetBody;
    private TextView tvCreatedAt;
    private ImageView ivMediaEntity;
    private LinearLayout llRetweetsData;
    private LinearLayout llFavoriteData;
    private TextView tvRetweetsCount;
    private TextView tvFavoritesCount;
    private ImageView ivReply;
    private CheckBox cbFavorite;
    private CheckBox cbRetweet;
    private View vDivider;
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        client = CardenalitoApplication.getRestClient();

        setContentView(R.layout.activity_tweet_detail);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Get data from the parent activity
        tweet = (Tweet) getIntent().getSerializableExtra(Tweet.TWEET_KEY);

        setupViews();

        populateTweetData();

        setupListeners();
    }

    private void setupViews() {
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserScreenName = (TextView) findViewById(R.id.tvUserScreenName);
        tvTweetBody = (TextView) findViewById(R.id.tvTweetBody);
        tvCreatedAt = (TextView) findViewById(R.id.tvCreatedAt);
        ivMediaEntity = (ImageView) findViewById(R.id.ivMediaEntity);
        llRetweetsData = (LinearLayout) findViewById(R.id.llRetweetsData);
        llFavoriteData = (LinearLayout) findViewById(R.id.llFavoriteData);
        tvFavoritesCount = (TextView) findViewById(R.id.tvFavoritesCount);
        tvRetweetsCount = (TextView) findViewById(R.id.tvRetweetsCount);
        ivReply = (ImageView) findViewById(R.id.ivReply);
        cbFavorite = (CheckBox) findViewById(R.id.cbFavorite);
        cbRetweet = (CheckBox) findViewById(R.id.cbRetweet);
        vDivider = (View) findViewById(R.id.vDivider);
    }

    private void populateTweetData() {
        ImageLoader imageLoader = ImageLoader.getInstance();

        ivProfileImage.setImageResource(android.R.color.transparent);
        imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), ivProfileImage);
        tvUserName.setText(tweet.getUser().getName());
        tvUserScreenName.setText(tweet.getUser().getScreenNameWithAt());
        tvTweetBody.setText(tweet.getBody());
        tvCreatedAt.setText(DateUtil.getFullDate(this, tweet.getCreateAt(),
                DateUtil.TWITTER_TIME_FORMAT));

        cbFavorite.setChecked(tweet.isFavorited());
        cbRetweet.setChecked(tweet.isRetweeted());

        if (tweet.getRetweetCount() > 0) {
            tvRetweetsCount.setText(String.valueOf(tweet.getRetweetCount()));
            vDivider.setVisibility(View.VISIBLE);
            llRetweetsData.setVisibility(View.VISIBLE);
        }

        if (tweet.getFavoriteCount() > 0) {
            tvFavoritesCount.setText(String.valueOf(tweet.getFavoriteCount()));
            vDivider.setVisibility(View.VISIBLE);
            llFavoriteData.setVisibility(View.VISIBLE);
        }

        if (tweet.getMediaUrl() != null) {
            ivMediaEntity.setImageResource(android.R.color.transparent);
            ivMediaEntity.setVisibility(View.VISIBLE);
            imageLoader.displayImage(tweet.getMediaUrlSmall(), ivMediaEntity);
        }
    }

    private void setupListeners() {

        // Favorite listener
        cbFavorite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavoriteClick(tweet, cbFavorite.isChecked());
            }
        });

        // Reply listener
        ivReply.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onReplyClick();
            }
        });

        // Retweet Listener
        cbRetweet.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onRetweetClick(tweet, cbRetweet.isChecked());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onProfileImageClick(View v) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra(User.USER_KEY, tweet.getUser());
        startActivity(i);
    }

    private void onFavoriteClick(Tweet tweet, boolean isFavorite) {
        // TODO: Broadcast change to relevant list where this tweet appears so it updates the UI
        String tweetId = String.valueOf(tweet.getUid());
        if (NetworkUtils.isNetworkAvailable(this)) {
            showProgressBar();
            if (isFavorite) {
                favoriteTweet(tweetId);
            } else {
                unfavoriteTweet(tweetId);
            }
        } else {
            // No internet connectivity error
            Toast.makeText(this, getString(R.string.no_internet_error_msg),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void unfavoriteTweet(String tweetId) {
        client.unfavoriteTweet(tweetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, JSONObject jsonObject) {
                Toast.makeText(getApplicationContext(), "Tweet un-favorite", Toast.LENGTH_SHORT)
                        .show();
                hideProgressBar();
            }

            @Override
            public void onFailure(Throwable e, String s) {
                Log.d("debug", e.toString());
                Log.d("debug", s.toString());
                Toast.makeText(getApplicationContext(), "Problem favoriting the tweet. Try again",
                        Toast.LENGTH_SHORT).show();
                hideProgressBar();
            }
        });
    }

    private void favoriteTweet(String tweetId) {
        client.favoriteTweet(tweetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int status, JSONObject jsonObject) {
                Toast.makeText(getApplicationContext(), "Tweet favorite", Toast.LENGTH_SHORT)
                        .show();
                hideProgressBar();
            }

            @Override
            public void onFailure(Throwable e, String s) {
                Log.d("debug", e.toString());
                Log.d("debug", s.toString());
                Toast.makeText(getApplicationContext(), "Problem favoriting the tweet. Try again",
                        Toast.LENGTH_SHORT).show();
                hideProgressBar();
            }
        });
    }

    private void onReplyClick() {
        Intent i = new Intent(this, ComposeActivity.class);

        i.putExtra(Tweet.TWEET_KEY, tweet);
        startActivity(i);
    }

    private void onRetweetClick(Tweet tweet, boolean retweet) {
        FragmentManager fm = getSupportFragmentManager();
        RetweetDialogFragment retweetAlertDialog = RetweetDialogFragment
                .newInstance(tweet, retweet);
        retweetAlertDialog.show(fm, "fragment_alert");
    }

    // Should be called manually when an async task has started
    private void showProgressBar() {
        setProgressBarIndeterminateVisibility(true);
    }

    // Should be called when an async task has finished
    private void hideProgressBar() {
        setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onRetweetClick(Tweet tweet) {
        if (NetworkUtils.isNetworkAvailable(this)) {
            showProgressBar();
            String tweetId = String.valueOf(tweet.getUid());
            client.retweet(tweetId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int status, JSONObject jsonObject) {
                    Toast.makeText(getApplicationContext(), "retweeted!", Toast.LENGTH_SHORT)
                            .show();
                    hideProgressBar();
                }

                @Override
                public void onFailure(Throwable e, String s) {
                    Log.d("debug", e.toString());
                    Log.d("debug", s.toString());
                    Toast.makeText(getApplicationContext(), "Problem retweeting. Try again",
                            Toast.LENGTH_SHORT).show();
                    hideProgressBar();
                }
            });
        } else {
            // No internet connectivity error
            Toast.makeText(this, getString(R.string.no_internet_error_msg),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUndoRetweetClick(Tweet tweet) {
        // TODO: implement this
        Toast.makeText(this, "Coming soon...", Toast.LENGTH_SHORT).show();
    }
}
