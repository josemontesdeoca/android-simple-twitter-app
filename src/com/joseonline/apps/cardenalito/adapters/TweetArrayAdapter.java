
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
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {

    public TweetArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, 0, tweets);
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

        ImageLoader imageLoader = ImageLoader.getInstance();

        ivProfileImage.setImageResource(android.R.color.transparent);
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

        return v;
    }
}
