package gr.mobap.mystories.activities;

import android.content.Intent;
import android.os.Bundle;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gr.mobap.mystories.Base;
import gr.mobap.mystories.R;
import gr.mobap.mystories.model.MyStory;
import gr.mobap.mystories.viewholder.StoriesViewHolder;

public class StoriesActivity extends Base {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.app_name));

        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        userProfile();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRef.removeEventListener(valueEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myRef.addListenerForSingleValueEvent(valueEventListener);
        myRef.addValueEventListener(valueEventListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        myRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("stories");
        myRef.keepSynced(true);

        Log.d(TAG, String.valueOf(myRef));
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<MyStory> list = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    MyStory myStory = ds.getValue(MyStory.class);
                    list.add(myStory);
                }
                Log.d(TAG, list.toString()); //To see is not empty
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
                //TODO set star image, get favorites and set, get author photo from Firebase db
                //holder.setStarImage(getApplicationContext(), model.getPhoto());
                holder.setStar(model.getFavorited());
                holder.setUserPhoto(getApplicationContext(), model.getUserPhoto());
                holder.setUserName(model.getUser());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent singleActivity = new Intent(StoriesActivity.this, StoriesActivity.class);
                        singleActivity.putExtra("PostID", post_key);
                        startActivity(singleActivity);
                    }
                });

            }
        };

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(StoriesActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        myRef.removeEventListener(valueEventListener);
    }

}