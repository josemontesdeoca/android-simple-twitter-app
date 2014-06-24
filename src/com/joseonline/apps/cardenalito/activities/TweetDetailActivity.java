
package com.joseonline.apps.cardenalito.activities;

import com.joseonline.apps.cardenalito.R;
import com.joseonline.apps.cardenalito.R.id;
import com.joseonline.apps.cardenalito.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetDetailActivity extends Activity {
    
    private ImageView ivProfileImage;
    private TextView tvUserName;
    private TextView tvUserScreenName;
    private TextView tvTweetBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        
        setupViews();
    }

    private void setupViews() {
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserScreenName = (TextView) findViewById(R.id.tvUserScreenName);
        tvTweetBody = (TextView) findViewById(R.id.tvTweetBody);
    }
}
