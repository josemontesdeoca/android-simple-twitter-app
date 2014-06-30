
package com.joseonline.apps.cardenalito.activities;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.util.Log;
import com.joseonline.apps.cardenalito.CardenalitoApplication;
import com.joseonline.apps.cardenalito.R;
import com.joseonline.apps.cardenalito.TwitterClient;
import com.joseonline.apps.cardenalito.helpers.NetworkUtils;
import com.joseonline.apps.cardenalito.models.Tweet;
import com.joseonline.apps.cardenalito.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ComposeActivity extends Activity {
    private static final int MAX_TWEET_LENGTH = 140;

    private ImageView ivAuthProfileImage;
    private TextView tvAuthUserName;
    private TextView tvAuthUserScreenName;
    private EditText etComposeTweet;
    private TextView tvCharsLeft;

    private TwitterClient client;

    private User authenticatedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_compose);

        client = CardenalitoApplication.getRestClient();

        setupView();
        setupListeners();
        populateProfileHeader();
    }

    private void setupView() {
        // Get log in user
        authenticatedUser = User.getLoginUser();

        // Setup back button on actionBar home icon
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ivAuthProfileImage = (ImageView) findViewById(R.id.ivAuthProfileImage);
        tvAuthUserName = (TextView) findViewById(R.id.tvAuthUserName);
        tvAuthUserScreenName = (TextView) findViewById(R.id.tvAuthUserScreenName);
        etComposeTweet = (EditText) findViewById(R.id.etComposeTweet);
    }

    private void populateProfileHeader() {
        // Show user info
        ImageLoader imageLoader = ImageLoader.getInstance();

        ivAuthProfileImage.setImageResource(android.R.color.transparent);
        imageLoader.displayImage(authenticatedUser.getProfileImageUrl(), ivAuthProfileImage);
        tvAuthUserName.setText(authenticatedUser.getName());
        tvAuthUserScreenName.setText(authenticatedUser.getScreenNameWithAt());
    }

    private void setupListeners() {
        etComposeTweet.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nothing
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                int tweetLenght = s.toString().length();

                tvCharsLeft.setText(String.valueOf(MAX_TWEET_LENGTH - tweetLenght));

                if (tweetLenght > MAX_TWEET_LENGTH) {
                    tvCharsLeft.setTextColor(getResources().getColor(R.color.darkred));
                } else {
                    tvCharsLeft.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.compose, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem actionViewItem = menu.findItem(R.id.counter);
        View v = actionViewItem.getActionView();

        tvCharsLeft = (TextView) v.findViewById(R.id.tvCharsLeft);

        tvCharsLeft.setText(String.valueOf(MAX_TWEET_LENGTH));

        return super.onPrepareOptionsMenu(menu);
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

    public void onCompose(MenuItem item) {
        String tweetBody = etComposeTweet.getText().toString();

        if (tweetBody.length() > MAX_TWEET_LENGTH) {
            Toast.makeText(this, "Text should be less than 140 characters", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        postTweet(tweetBody);
    }

    private void postTweet(String tweetBody) {
        showProgressBar();
        if (NetworkUtils.isNetworkAvailable(this)) {
            client.postTweet(tweetBody, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    Toast.makeText(ComposeActivity.this, "Tweet posted", Toast.LENGTH_SHORT).show();
                    Tweet tweet = Tweet.fromJSON(jsonObject);

                    Intent data = new Intent();
                    data.putExtra(Tweet.TWEET_KEY, tweet);
                    setResult(RESULT_OK, data);
                    hideProgressBar();
                    finish();
                }

                @Override
                public void onFailure(Throwable e, String s) {
                    Log.d("debug", e.toString());
                    Log.d("debug", s.toString());
                    hideProgressBar();
                    Toast.makeText(ComposeActivity.this, "Problem sending the tweet. Try again",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Oops! no internet connection...", Toast.LENGTH_SHORT).show();
            hideProgressBar();
        }
    }

    // Should be called manually when an async task has started
    public void showProgressBar() {
        setProgressBarIndeterminateVisibility(true);
    }

    // Should be called when an async task has finished
    public void hideProgressBar() {
        setProgressBarIndeterminateVisibility(false);
    }
}
