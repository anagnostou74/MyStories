package gr.mobap.mystories.activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gr.mobap.mystories.Base;
import gr.mobap.mystories.R;
import gr.mobap.mystories.model.MyStory;
import gr.mobap.mystories.viewholder.StoriesViewHolder;
import gr.mobap.mystories.widget.MyStoriesWidget;


// Save state: https://stackoverflow.com/questions/42514011/how-to-retain-recyclerviews-position-after-orientation-change-while-using-fire
public class StoriesActivity extends Base {
    public static String WIDGET_MESSAGES_SHAREDPREF = "widget_messages_list";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.rv_main)
    RecyclerView recyclerView;

    FirebaseRecyclerAdapter adapter;
    DatabaseReference myRef;
    private static final String TAG = StoriesActivity.class.getSimpleName();
    ValueEventListener valueEventListener;
    LinearLayoutManager mLayoutManager;
    String LIST_STATE_KEY = "list_state_key";
    Parcelable mListState;
    int recyclerViewPosition;
    private static Bundle mBundleRecyclerViewState;
    private FirebaseAuth mAuth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.app_name));

        mAuth = FirebaseAuth.getInstance();

        fab.setOnClickListener(view -> {
            if (user != null) {
                Intent i = new Intent(StoriesActivity.this, PostActivity.class);
                startActivity(i);
            } else {
                Intent i = new Intent(StoriesActivity.this, LoginActivity.class);
                Snackbar.make(view, getString(R.string.sign_to_post), Snackbar.LENGTH_LONG).show();
                startActivity(i);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        userProfile();

        Intent intent = new Intent(this, MyStoriesWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
        int[] ids = widgetManager.getAppWidgetIds(new ComponentName(this, MyStoriesWidget.class));
        widgetManager.notifyAppWidgetViewDataChanged(ids, R.id.widget_list);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, ids);
        sendBroadcast(intent);

        myRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(getString(R.string.stories));
        myRef.keepSynced(true);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<MyStory> list = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    MyStory myStory = ds.getValue(MyStory.class);
                    list.add(myStory);
                }
                Log.d(TAG, list.toString()); //To see is not empty

                Gson gson = new Gson();
                String jsonMessages = gson.toJson(list);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(WIDGET_MESSAGES_SHAREDPREF, jsonMessages);
                editor.apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        myRef.addListenerForSingleValueEvent(valueEventListener);
        myRef.addValueEventListener(valueEventListener);
        FirebaseRecyclerOptions<MyStory> options =
                new FirebaseRecyclerOptions.Builder<MyStory>()
                        .setQuery(myRef, MyStory.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<MyStory, StoriesViewHolder>(options) {
            @Override
            public StoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.main_story_item, parent, false);

                return new StoriesViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(StoriesViewHolder holder, int position, MyStory model) {
                final String post_key = getRef(position).getKey();

                holder.setMainImageUrl(getApplicationContext(), model.getPhoto());
                holder.setTitle(model.getTitle());
                // Determine if the current user has liked this post and set UI accordingly
                if (user != null) {
                    if (model.fav.containsKey(getUid())) {
                        holder.star.setImageResource(R.drawable.ic_favorite_full);
                    } else {
                        holder.star.setImageResource(R.drawable.ic_favorite_empty);
                    }
                } else {
                    holder.star.setImageResource(R.drawable.ic_favorite_empty);
                }
                holder.setStar(model.getFavorited());
                holder.setUserPhoto(getApplicationContext(), model.getImage());
                holder.setUserName(model.getUser());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Launch DetailActivity
                        Intent detailActivity = new Intent(StoriesActivity.this, DetailActivity.class);
                        detailActivity.putExtra(DetailActivity.EXTRA_POST_KEY, post_key);
                        startActivity(detailActivity);
                    }
                });

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                holder.bindToPost(model, starView -> {
                    setStar(post_key);
                });

            }
        };

        if (savedInstanceState != null) {
            recyclerViewPosition = savedInstanceState.getInt(LIST_STATE_KEY);
        }

        mLayoutManager = new LinearLayoutManager(StoriesActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(recyclerViewPosition);

    }

    public void setStar(String post_key) {
        DatabaseReference globalPostRef = myRef.child(post_key);
        DatabaseReference userPostRef = myRef.child(getString(R.string.user_posts)).child(user.getUid()).child(post_key);
        onStarClicked(globalPostRef);
        onStarClicked(userPostRef);
    }


    // [START post_stars_transaction]
    public void onStarClicked(DatabaseReference postRef) {
        if (user != null) {
            postRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    MyStory p = mutableData.getValue(MyStory.class);
                    if (p == null) {
                        return Transaction.success(mutableData);
                    }
                    if (p.fav.containsKey(getUid())) {
                        // Unstar the post and remove self from stars
                        p.favorited = p.favorited - 1;
                        p.fav.remove(getUid());
                    } else {
                        // Star the post and add self to stars
                        p.favorited = p.favorited + 1;
                        p.fav.put(getUid(), true);
                    }

                    // Set value and report transaction success
                    mutableData.setValue(p);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b,
                                       DataSnapshot dataSnapshot) {
                    // Transaction completed
                    Log.d(TAG, getString(R.string.post_transaction) + databaseError);
                }
            });
        } else {
            Toast.makeText(StoriesActivity.this, getString(R.string.login_vote), Toast.LENGTH_SHORT).show();
        }

    }

    // [END post_stars_transaction]
    public String getUid() {
        if (user != null) {
            return user.getUid();
        } else {
            Toast.makeText(StoriesActivity.this, getString(R.string.user_id), Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        myRef.addListenerForSingleValueEvent(valueEventListener);
        myRef.addValueEventListener(valueEventListener);

        if (mBundleRecyclerViewState != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListState = mBundleRecyclerViewState.getParcelable(LIST_STATE_KEY);
                    recyclerView.getLayoutManager().onRestoreInstanceState(mListState);

                }
            }, 50);
        }
        recyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRef.removeEventListener(valueEventListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        myRef.removeEventListener(valueEventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBundleRecyclerViewState = new Bundle();
        mListState = recyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(LIST_STATE_KEY, mListState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mBundleRecyclerViewState != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListState = mBundleRecyclerViewState.getParcelable(LIST_STATE_KEY);
                    recyclerView.getLayoutManager().onRestoreInstanceState(mListState);

                }
            }, 50);
        }
        recyclerView.setLayoutManager(mLayoutManager);
    }
}