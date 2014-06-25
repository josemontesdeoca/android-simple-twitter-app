
package com.joseonline.apps.cardenalito.models;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ConflictAction;
import com.activeandroid.annotation.Table;

@Table(name = "Users")
public class User extends Model implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String USER_KEY = "user";

    @Column(name = "remote_id", unique = true, onUniqueConflict = ConflictAction.IGNORE)
    private long uid;
    @Column(name = "name")
    private String name;
    @Column(name = "screen_name")
    private String screenName;
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    public User() {
        super();
    }

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

    public String getScreenNameWithAt() {
        return "@" + screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

}
