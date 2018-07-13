package gr.mobap.mystories;

import android.app.Application;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.tweetui.TweetUi;

import static gr.mobap.mystories.BuildConfig.CONSUMER_KEY;
import static gr.mobap.mystories.BuildConfig.CONSUMER_SECRET;

class MyStoriesApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //Twitter sdk initialization
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(CONSUMER_KEY, CONSUMER_SECRET))
                .debug(true)
                .build();
        Twitter.initialize(config);
        new Thread(TweetUi::getInstance).start();
    }
}
