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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import gr.mobap.mystories.Base;
import gr.mobap.mystories.R;
import gr.mobap.mystories.model.MyStory;
import gr.mobap.mystories.utilities.GlideApp;
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
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private RecyclerView recyclerView;

    FirebaseRecyclerAdapter adapter;
    FirebaseDatabase database;
    DatabaseReference myRef;

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

        View navHeaderView = navigationView.getHeaderView(0);
        CircleImageView mDisplayImageView = navHeaderView.findViewById(R.id.personalImageView);
        TextView mNameTextView = navHeaderView.findViewById(R.id.name);
        TextView mEmailTextView = navHeaderView.findViewById(R.id.email);

        MenuItem mLogInTextView = navigationView.getMenu().findItem(R.id.nav_login);
        MenuItem mLogOutTextView = navigationView.getMenu().findItem(R.id.nav_logout);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("stories");
        Log.d("Ref", String.valueOf(myRef));

        if (mFirebaseUser == null) {
            GlideApp
                    .with(this)
                    .load(R.drawable.ic_account)
                    .apply(RequestOptions.circleCropTransform())
                    .error(android.R.drawable.sym_def_app_icon)
                    .centerCrop()
                    .placeholder(android.R.drawable.sym_def_app_icon)
                    .into(mDisplayImageView);
            mNameTextView.setVisibility(View.GONE);
            mEmailTextView.setVisibility(View.GONE);
            mLogOutTextView.setVisible(false);
        } else {
            if (mFirebaseUser.getPhotoUrl() != null) {
                GlideApp
                        .with(this)
                        .load(mFirebaseUser.getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .error(android.R.drawable.sym_def_app_icon)
                        .centerCrop()
                        .placeholder(android.R.drawable.sym_def_app_icon)
                        .into(mDisplayImageView);

            }
            mNameTextView.setText(mFirebaseUser.getDisplayName());
            mEmailTextView.setText(mFirebaseUser.getEmail());
            mLogInTextView.setVisible(false);
        }

        //initialize recyclerview and FIrebase objects
        recyclerView = (RecyclerView) findViewById(R.id.rv_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<MyStory> list = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    MyStory myStory = ds.getValue(MyStory.class);
                    list.add(myStory);
                }
                populateUI();
                Log.d("TAG", list.toString()); //To see is not empty
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        myRef.addListenerForSingleValueEvent(valueEventListener);
    }


    @Override
    protected void onStart() {
        super.onStart();
        populateUI();
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void populateUI() {
        FirebaseRecyclerOptions<MyStory> options =
                new FirebaseRecyclerOptions.Builder<MyStory>()
                        .setQuery(myRef, MyStory.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<MyStory, StoriesViewHolder>(options) {
            @Override
            public StoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.main_story_item, parent, false);

                return new StoriesViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(StoriesViewHolder holder, int position, MyStory model) {
                // Bind the Chat object to the ChatHolder
                final String post_key = getRef(position).getKey().toString();
                holder.setTitle(model.getTitle());
                holder.setDesc(model.getMain());
                holder.setImageUrl(getApplicationContext(), model.getPhoto());
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

        recyclerView.setAdapter(adapter);
    }

}