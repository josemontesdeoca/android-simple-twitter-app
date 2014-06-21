
package com.joseonline.apps.cardenalito.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.codepath.oauth.OAuthLoginActivity;
import com.joseonline.apps.cardenalito.R;
import com.joseonline.apps.cardenalito.TwitterClient;

public class LoginActivity extends OAuthLoginActivity<TwitterClient> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    /**
     * OAuth authenticated successfully, launch primary authenticated activity
     */
    @Override
    public void onLoginSuccess() {
        Intent i = new Intent(this, TimelineActivity.class);
        startActivity(i);
    }

    /**
     * OAuth authentication flow failed, handle the error
     */
    @Override
    public void onLoginFailure(Exception e) {
        e.printStackTrace();
    }

    /**
     * Click handler method for the "Connect to twitter" button used to start OAuth flow Uses the
     * client to initiate OAuth authorization
     */

    public void loginToTwitter(View view) {
        getClient().connect();
    }

}
