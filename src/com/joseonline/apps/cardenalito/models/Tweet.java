
package com.joseonline.apps.cardenalito.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ConflictAction;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.util.Log;

@Table(name = "Tweets")
public class Tweet extends Model implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String TWEET_KEY = "tweet";

    @Column(name = "remote_id", unique = true, onUniqueConflict = ConflictAction.REPLACE)
    private long uid;
    @Column(name = "body")
    private String body;
    @Column(name = "create_at")
    private String createAt;
    @Column(name = "user")
    private User user;
    @Column(name = "media_url")
    private String mediaUrl;
    @Column(name = "retweet_count")
    private int retweetCount;
    @Column(name = "retweeted")
    private boolean retweeted;
    @Column(name = "favorite_count")
    private int favoriteCount;
    @Column(name = "favorited")
    private boolean favorited;

    public Tweet() {
        super();
    }

    public long getUid() {
        return uid;
    }

    public String getBody() {
        return body;
    }

    public String getCreateAt() {
        return createAt;
    }

    public User getUser() {
        return user;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public String getMediaUrlThumb() {
        return mediaUrl + ":thumb";
    }

    public String getMediaUrlSmall() {
        return mediaUrl + ":small";
    }

    public static List<Tweet> getAll() {
        return new Select().from(Tweet.class).execute();
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject tweetJson = null;

            try {
                tweetJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Tweet tweet = Tweet.fromJSON(tweetJson);

            if (tweet != null) {
                tweets.add(tweet);
            }
        }

        return tweets;
    }

    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();

        try {
            tweet.uid = jsonObject.getLong("id");
            tweet.body = jsonObject.getString("text");
            tweet.createAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            tweet.retweetCount = jsonObject.getInt("retweet_count");
            tweet.retweeted = jsonObject.getBoolean("retweeted");
            tweet.favoriteCount = jsonObject.getInt("favorite_count");
            tweet.favorited = jsonObject.getBoolean("favorited");

            // Getting a media Url
            try {
                JSONObject entities = jsonObject.getJSONObject("entities");
                JSONArray media = entities.getJSONArray("media");
                if (media.length() > 0) {
                    Log.d("DEBUG", "A tweet with media object");
                    tweet.mediaUrl = media.getJSONObject(0).getString("media_url");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return tweet;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Tweet tweet = (Tweet) obj;

        return uid == tweet.getUid();
    }
}
