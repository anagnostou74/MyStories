package gr.mobap.mystories.twitter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthException;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.TwitterListTimeline;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import gr.mobap.mystories.Base;
import gr.mobap.mystories.R;
import gr.mobap.mystories.activities.StoriesActivity;
import gr.mobap.mystories.utilities.AndroidNetworkUtility;


/**
 * TimelineActivity shows a timeline from a list with funny and interesting tweets.
 */
public class TimelineActivity extends Base {

    private final WeakReference<Activity> activityRef = new WeakReference<>(TimelineActivity.this);
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(android.R.id.list)
    ListView listView;
    @BindView(android.R.id.empty)
    View emptyView;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.app_name));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        userProfile();

        AndroidNetworkUtility androidNetworkUtility = new AndroidNetworkUtility();
        if (androidNetworkUtility.isConnected(this)) {
            new FetchTweets().execute();
        } else {
            // display error
            Toast.makeText(this, getString(R.string.no_network),
                    Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                Intent i = new Intent(TimelineActivity.this, StoriesActivity.class);
                startActivity(i);
                // close this activity
                finish();
            }, 1000); // wait for 1 second
        }

        listView.setEmptyView(emptyView);
        // set custom scroll listener to enable swipe refresh layout only when at list top
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            boolean enableRefresh = false;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                if (listView.getChildCount() > 0) {
                    // check that the first item is visible and that its top matches the parent
                    enableRefresh = listView.getFirstVisiblePosition() == 0 &&
                            listView.getChildAt(0).getTop() >= 0;
                } else {
                    enableRefresh = false;
                }
                swipeLayout.setEnabled(enableRefresh);
            }
        });

    }

    // uses Async Task to complete Project Requirements
    @SuppressLint("StaticFieldLeak")
    private class FetchTweets extends AsyncTask<Void, Void, TweetTimelineListAdapter> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected TweetTimelineListAdapter doInBackground(Void... params) {
            // launch the app login activity when a guest user tries to favorite a Tweet
            final Callback<Tweet> actionCallback = new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    // Intentionally blank
                }

                @Override
                public void failure(TwitterException exception) {
                    if (exception instanceof TwitterAuthException) {
                        startActivity(TwitterCoreMainActivity.newIntent(TimelineActivity.this));
                    }
                }
            };
            // Collection "Funny from user mobap_gr"
            TwitterListTimeline timeline = new TwitterListTimeline.Builder()
                    .slugWithOwnerScreenName(getString(R.string.funny_list), getString(R.string.twitter_user))
                    .build();

            return new TweetTimelineListAdapter.Builder(getApplication())
                    .setTimeline(timeline)
                    .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
                    .setOnActionCallback(actionCallback)
                    .build();
        }

        @Override
        protected void onPostExecute(TweetTimelineListAdapter adapter) {
            super.onPostExecute(adapter);
            listView.setAdapter(adapter);
            swipeLayout.setColorSchemeResources(R.color.twitter_blue, R.color.twitter_dark);

            // specify action to take on swipe refresh
            swipeLayout.setOnRefreshListener(() -> {
                swipeLayout.setRefreshing(true);
                adapter.refresh(new Callback<TimelineResult<Tweet>>() {
                    @Override
                    public void success(Result<TimelineResult<Tweet>> result) {
                        swipeLayout.setRefreshing(false);
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        swipeLayout.setRefreshing(false);
                        final Activity activity = activityRef.get();
                        if (activity != null && !activity.isFinishing()) {
                            Toast.makeText(activity, exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            });

        }
    }
}