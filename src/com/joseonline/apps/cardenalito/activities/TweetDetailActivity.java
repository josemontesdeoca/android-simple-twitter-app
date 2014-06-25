
package com.joseonline.apps.cardenalito.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.joseonline.apps.cardenalito.R;
import com.joseonline.apps.cardenalito.helpers.DateUtil;
import com.joseonline.apps.cardenalito.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetDetailActivity extends Activity {

    private Tweet tweet;
    private ImageView ivProfileImage;
    private TextView tvUserName;
    private TextView tvUserScreenName;
    private TextView tvTweetBody;
    private TextView tvCreatedAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Get data from the parent activity
        tweet = (Tweet) getIntent().getSerializableExtra(Tweet.TWEET_KEY);

        setupViews();
    }

    private void setupViews() {
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserScreenName = (TextView) findViewById(R.id.tvUserScreenName);
        tvTweetBody = (TextView) findViewById(R.id.tvTweetBody);
        tvCreatedAt = (TextView) findViewById(R.id.tvCreatedAt);

        ImageLoader imageLoader = ImageLoader.getInstance();

        ivProfileImage.setImageResource(android.R.color.transparent);
        imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), ivProfileImage);
        tvUserName.setText(tweet.getUser().getName());
        tvUserScreenName.setText(tweet.getUser().getScreenNameWithAt());
        tvTweetBody.setText(tweet.getBody());
        tvCreatedAt.setText(DateUtil.getFullDate(this, tweet.getCreateAt(),
                DateUtil.TWITTER_TIME_FORMAT));
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
}
