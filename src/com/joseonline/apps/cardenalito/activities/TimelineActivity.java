
package com.joseonline.apps.cardenalito.activities;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.joseonline.apps.cardenalito.CardenalitoApplication;
import com.joseonline.apps.cardenalito.R;
import com.joseonline.apps.cardenalito.fragments.TweetsListFragment;
import com.joseonline.apps.cardenalito.models.Tweet;
import com.joseonline.apps.cardenalito.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

public class TimelineActivity extends FragmentActivity {
    public static final int COMPOSE_TWEET_REQUEST_CODE = 1;
    public static final String AUTHENTICATED_USER_KEY = "authenticatedUser";

    private TweetsListFragment fragmentTweetsList;
    private User authenticatedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        fragmentTweetsList = (TweetsListFragment) getSupportFragmentManager().findFragmentById(
                R.id.fragment_timeline);

        getAuthenticatedUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
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
            fragmentTweetsList.insertTop(newTweet);
        }
    }

    private void getAuthenticatedUser() {
        CardenalitoApplication.getRestClient().getAuthenticatedUser(new JsonHttpResponseHandler() {
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

}
