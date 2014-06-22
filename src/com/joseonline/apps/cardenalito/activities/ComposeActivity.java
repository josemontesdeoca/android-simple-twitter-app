
package com.joseonline.apps.cardenalito.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.joseonline.apps.cardenalito.R;
import com.joseonline.apps.cardenalito.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ComposeActivity extends Activity {

    private ImageView ivAuthProfileImage;
    private TextView tvAuthUserName;
    private TextView tvAuthUserScreenName;
    private EditText etComposeTweet;

    private User authenticatedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        authenticatedUser = (User) getIntent().getSerializableExtra(
                TimelineActivity.AUTHENTICATED_USER_KEY);

        setupView();
    }

    private void setupView() {
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

}
