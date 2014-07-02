
package com.joseonline.apps.cardenalito.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.joseonline.apps.cardenalito.models.Tweet;

public class RetweetDialogFragment extends DialogFragment {

    private onButtonClickListener listener;
    private Tweet tweet;
    private boolean retweet;

    public interface onButtonClickListener {
        void onRetweetClick(Tweet tweet);

        void onUndoRetweetClick(Tweet tweet);
    }

    public RetweetDialogFragment() {
    }

    public static RetweetDialogFragment newInstance(Tweet tweet, boolean retweet) {
        RetweetDialogFragment frag = new RetweetDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(Tweet.TWEET_KEY, tweet);
        args.putBoolean("retweet", retweet);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        tweet = (Tweet) getArguments().getSerializable(Tweet.TWEET_KEY);
        retweet = getArguments().getBoolean("retweet");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Retweet");
        if (retweet) {
            alertDialogBuilder.setMessage("Retweet this to your followers?");
            alertDialogBuilder.setPositiveButton("Retweet", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.onRetweetClick(tweet);

                }
            });
        } else {
            alertDialogBuilder.setMessage("Undo this retweet?");
            alertDialogBuilder.setPositiveButton("Undo", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.onUndoRetweetClick(tweet);
                }
            });
        }

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return alertDialogBuilder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof onButtonClickListener) {
            listener = (onButtonClickListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement RetweetDialogFragment.onButtonClickListener");
        }
    }
}
