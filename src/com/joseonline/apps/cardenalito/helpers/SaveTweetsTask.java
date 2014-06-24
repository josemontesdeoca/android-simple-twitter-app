package com.joseonline.apps.cardenalito.helpers;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.joseonline.apps.cardenalito.models.Tweet;

public class SaveTweetsTask extends AsyncTask<ArrayList<Tweet>, Void, Boolean> {

    @Override
    protected Boolean doInBackground(ArrayList<Tweet>... t) {
        Log.d("DEBUG", "Saving Tweets and User data");
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
