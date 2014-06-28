
package com.joseonline.apps.cardenalito.activities;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.joseonline.apps.cardenalito.CardenalitoApplication;
import com.joseonline.apps.cardenalito.R;
import com.joseonline.apps.cardenalito.fragments.HomeTimelineFragment;
import com.joseonline.apps.cardenalito.fragments.MentionsTimelineFragment;
import com.joseonline.apps.cardenalito.fragments.TweetsListFragment;
import com.joseonline.apps.cardenalito.listeners.FragmentTabListener;
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
                R.id.flContainer);

        getAuthenticatedUser();

        setupTabs();
    }

    private void setupTabs() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);

        Tab tab1 = actionBar
                .newTab()
                .setText("Home")
                // .setIcon(R.drawable.ic_home)
                .setTag("HomeTimelineFragment")
                .setTabListener(
                        new FragmentTabListener<HomeTimelineFragment>(R.id.flContainer, this,
                                "home", HomeTimelineFragment.class));

        actionBar.addTab(tab1);
        actionBar.selectTab(tab1);

        Tab tab2 = actionBar
                .newTab()
                .setText("Mentions")
                // .setIcon(R.drawable.ic_mentions)
                .setTag("MentionsTimelineFragment")
                .setTabListener(
                        new FragmentTabListener<MentionsTimelineFragment>(R.id.flContainer, this,
                                "mentions", MentionsTimelineFragment.class));

        actionBar.addTab(tab2);
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
