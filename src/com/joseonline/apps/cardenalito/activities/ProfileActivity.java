
package com.joseonline.apps.cardenalito.activities;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.joseonline.apps.cardenalito.CardenalitoApplication;
import com.joseonline.apps.cardenalito.R;
import com.joseonline.apps.cardenalito.fragments.UserTimelineFragment;
import com.joseonline.apps.cardenalito.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProfileActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        Intent i = getIntent();
        
        if (i.hasExtra(User.USER_KEY)) {
            User user = (User) i.getSerializableExtra(User.USER_KEY);
            populateProfileHeader(user);
            setupUserTimelineFragment(user);
        } else {
            // User looking into his own profile
            loadProfileInfo();
        }
    }

    private void loadProfileInfo() {
        CardenalitoApplication.getRestClient().getAuthenticatedUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                User user = User.fromJSON(jsonObject);
                populateProfileHeader(user);
                setupUserTimelineFragment(user);
            }
        });
    }

    private void populateProfileHeader(User user) {
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        TextView tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        TextView tvTweets = (TextView) findViewById(R.id.tvTweets);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
     
        ImageLoader.getInstance().displayImage(user.getProfileImageUrl(), ivProfileImage);
        tvUserName.setText(user.getName());
        tvScreenName.setText(user.getScreenNameWithAt());
        tvTweets.setText(user.getTweetsCount() + " Tweets");
        tvFollowing.setText(user.getFriends() + " Following");
        tvFollowers.setText(user.getFollowers() + " Followers");
    }
    
    private void setupUserTimelineFragment(User user) {
        UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(user);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flTimelineContainer, fragmentUserTimeline);
        ft.commit();
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
