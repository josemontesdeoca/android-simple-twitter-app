
package com.joseonline.apps.cardenalito.helpers;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.joseonline.apps.cardenalito.models.Tweet;
import com.joseonline.apps.cardenalito.models.User;

public class SaveTweetsToDbAsync extends AsyncTask<ArrayList<Tweet>, Void, Boolean> {

    @Override
    protected Boolean doInBackground(ArrayList<Tweet>... t) {
        Log.d("DEBUG", "Saving Tweets and User data");

        // drop tables
        new Delete().from(Tweet.class).execute();
        new Delete().from(User.class).execute();

        ActiveAndroid.beginTransaction();
        try {
            for (Tweet tweet : t[0]) {
                tweet.getUser().save();
                tweet.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("DEBUG", "Saving to db failed: " + e.toString());
            return false;
        } finally {
            ActiveAndroid.endTransaction();
        }
        return true;
    }

}
