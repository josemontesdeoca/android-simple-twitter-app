
package com.joseonline.apps.cardenalito.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joseonline.apps.cardenalito.R;
import com.joseonline.apps.cardenalito.helpers.DateUtil;
import com.joseonline.apps.cardenalito.models.Tweet;
import com.joseonline.apps.cardenalito.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {

    private OnTweetClickListener listener;

    public interface OnTweetClickListener {
        public void onProfileImageClick(User user);
    }

    public TweetArrayAdapter(Context context, List<Tweet> tweets, OnTweetClickListener listener) {
        super(context, 0, tweets);
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet tweet = getItem(position);

        View v;

        if (convertView == null) {
            LayoutInflater inflator = LayoutInflater.from(getContext());
            v = inflator.inflate(R.layout.tweet_item, parent, false);
        } else {
            v = convertView;
        }

        ImageView ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);
        TextView tvUserName = (TextView) v.findViewById(R.id.tvUserName);
        TextView tvScreenName = (TextView) v.findViewById(R.id.tvScreenName);
        TextView tvCreatedAt = (TextView) v.findViewById(R.id.tvCreatedAt);
        TextView tvTweetBody = (TextView) v.findViewById(R.id.tvTweetBody);
        ImageView ivMediaEntity = (ImageView) v.findViewById(R.id.ivMediaEntity);
        
        ImageView ivRetweet = (ImageView) v.findViewById(R.id.ivRetweet);
        ImageView ivFavorite = (ImageView) v.findViewById(R.id.ivFavorite);
        TextView tvRetweets = (TextView) v.findViewById(R.id.tvRetweets);
        TextView tvFavorites = (TextView) v.findViewById(R.id.tvFavorites);
        
        // set UI values to defaults in case is a reused view
        ivProfileImage.setImageResource(android.R.color.transparent);
        ivFavorite.setImageResource(R.drawable.ic_no_favorite);
        ivRetweet.setImageResource(R.drawable.ic_no_retweet);
        tvRetweets.setText("");
        tvFavorites.setText("");

        ImageLoader imageLoader = ImageLoader.getInstance();

        // Set profile image listener
        ivProfileImage.setTag(tweet.getUser());
        ivProfileImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                User user = (User) v.getTag();
                listener.onProfileImageClick(user);
            }
        });
        
        imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), ivProfileImage);
        tvUserName.setText(tweet.getUser().getName());
        tvScreenName.setText(tweet.getUser().getScreenNameWithAt());
        tvCreatedAt.setText(DateUtil.getRelativeTimeAgo(tweet.getCreateAt(),
                DateUtil.TWITTER_TIME_FORMAT));
        tvTweetBody.setText(tweet.getBody());

        ivMediaEntity.setImageResource(android.R.color.transparent);
        ivMediaEntity.setVisibility(View.GONE);

        if (tweet.getMediaUrl() != null) {
            ivMediaEntity.setVisibility(View.VISIBLE);
            imageLoader.displayImage(tweet.getMediaUrlThumb(), ivMediaEntity);
        }
        
        if (tweet.getRetweetCount() > 0) {
            tvRetweets.setText(String.valueOf(tweet.getRetweetCount()));
        }
        
        if (tweet.isRetweeted()) {
            ivRetweet.setImageResource(R.drawable.ic_retweeted);
        }
        
        if (tweet.getFavoriteCount() > 0) {
            tvFavorites.setText(String.valueOf(tweet.getFavoriteCount()));
        }

        if (tweet.isFavorited()) {
            ivFavorite.setImageResource(R.drawable.ic_favorite);
        }
        
        return v;
    }
}
