
package com.joseonline.apps.cardenalito.activities;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.util.Log;
import com.joseonline.apps.cardenalito.CardenalitoApplication;
import com.joseonline.apps.cardenalito.R;
import com.joseonline.apps.cardenalito.TwitterClient;
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

    private TwitterClient client;

    private User authenticatedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = CardenalitoApplication.getRestClient();

        authenticatedUser = (User) getIntent().getSerializableExtra(
                TimelineActivity.AUTHENTICATED_USER_KEY);

        setupView();
    }

    private void setupView() {
        // Setup back button on actionBar home icon
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ivAuthProfileImage = (ImageView) findViewById(R.id.ivAuthProfileImage);
        tvAuthUserName = (TextView) findViewById(R.id.tvAuthUserName);
        tvAuthUserScreenName = (TextView) findViewById(R.id.tvAuthUserScreenName);
        etComposeTweet = (EditText) findViewById(R.id.etComposeTweet);

        // Show user info
        ImageLoader imageLoader = ImageLoader.getInstance();

        ivAuthProfileImage.setImageResource(android.R.color.transparent);
        imageLoader.displayImage(authenticatedUser.getProfileImageUrl(), ivAuthProfileImage);
        tvAuthUserName.setText(authenticatedUser.getName());
        tvAuthUserScreenName.setText(authenticatedUser.getScreenNameWithAt());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.compose, menu);
        return true;
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
        String tweet = etComposeTweet.getText().toString();

        if (tweet.length() > MAX_TWEET_LENGTH) {
            Toast.makeText(this, "Text should be less than 140 characters", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        client.postTweet(tweet, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                Toast.makeText(ComposeActivity.this, "Tweet posted", Toast.LENGTH_SHORT).show();
                Tweet tweet = Tweet.fromJSON(jsonObject);
                
                Intent data = new Intent();
                data.putExtra(Tweet.TWEET_KEY, tweet);
                setResult(RESULT_OK, data);
                finish();
            }

            @Override
            public void onFailure(Throwable e, String s) {
                Log.d("debug", e.toString());
                Log.d("debug", s.toString());
                Toast.makeText(ComposeActivity.this, "Problem sending the tweet. Try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
