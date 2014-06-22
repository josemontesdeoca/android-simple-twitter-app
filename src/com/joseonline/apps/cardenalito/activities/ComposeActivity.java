
package com.joseonline.apps.cardenalito.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.joseonline.apps.cardenalito.R;

public class ComposeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.compose, menu);
        return true;
    }

}
