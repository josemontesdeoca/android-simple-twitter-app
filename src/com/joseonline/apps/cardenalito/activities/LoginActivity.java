
package com.joseonline.apps.cardenalito.activities;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.oauth.OAuthLoginActivity;
import com.joseonline.apps.cardenalito.CardenalitoApplication;
import com.joseonline.apps.cardenalito.R;
import com.joseonline.apps.cardenalito.TwitterClient;
import com.joseonline.apps.cardenalito.helpers.NetworkUtils;
import com.joseonline.apps.cardenalito.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

public class LoginActivity extends OAuthLoginActivity<TwitterClient> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /**
     * OAuth authenticated successfully, launch primary authenticated activity
     */
    @Override
    public void onLoginSuccess() {
        User authUser = User.getLoginUser();

        if (authUser != null) {
            Log.d("DEBUG", "Login user: " + authUser.getScreenName() + " already in db");
            goToTimeline();
        } else {
            if (NetworkUtils.isNetworkAvailable(this)) {
                Log.d("DEBUG", "Getting login user");
                CardenalitoApplication.getRestClient().getAuthenticatedUser(
                        new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(JSONObject jsonObject) {
                                User user = User.fromJSON(jsonObject);
                                user.setLoginUser(true);
                                user.save();
                                goToTimeline();
                            }

                            @Override
                            public void onFailure(Throwable e, JSONObject jsonObject) {
                                Log.d("DEBUG", e.toString());
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.remote_call_error_msg),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                Toast.makeText(this, getString(R.string.no_internet_error_msg), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /**
     * OAuth authentication flow failed, handle the error
     */
    @Override
    public void onLoginFailure(Exception e) {
        e.printStackTrace();
        Toast.makeText(this, getString(R.string.remote_call_error_msg), Toast.LENGTH_LONG).show();
    }

    /**
     * Click handler method for the "Connect to twitter" button used to start OAuth flow Uses the
     * client to initiate OAuth authorization
     */
    public void loginToTwitter(View view) {
        getClient().connect();
    }

    private void goToTimeline() {
        Intent i = new Intent(this, TimelineActivity.class);
        startActivity(i);
    }

}
