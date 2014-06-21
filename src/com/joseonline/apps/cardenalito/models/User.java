
package com.joseonline.apps.cardenalito.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private long uid;
    private String name;
    private String screenName;
    private String profileImageUrl;

    // Factory Method
    // User.fromJSON(..)
    public static User fromJSON(JSONObject jsonObject) {
        User user = new User();

        try {
            user.uid = jsonObject.getLong("id");
            user.name = jsonObject.getString("name");
            user.screenName = jsonObject.getString("screen_name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");
        } catch (JSONException e) {

        }

        return user;
    }

    public long getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

}
