package gr.mobap.mystories.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import gr.mobap.mystories.Base;
import gr.mobap.mystories.R;
import gr.mobap.mystories.model.MyStory;
import gr.mobap.mystories.utilities.GlideApp;

public class DetailActivity extends Base {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.activity_detail_image)
    ImageView activity_detail_image;

    @BindView(R.id.prologue_card)
    CardView prologue_card;
    @BindView(R.id.body_card)
    CardView body_card;
    @BindView(R.id.epilogue_card)
    CardView epilogue_card;
    @BindView(R.id.info_card)
    CardView info_card;

    @BindView(R.id.prologue_tv)
    TextView prologue_tv;
    @BindView(R.id.body_tv)
    TextView body_tv;
    @BindView(R.id.epilogue_tv)
    TextView epilogue_tv;
    @BindView(R.id.info_date_tv)
    TextView info_date_tv;
    @BindView(R.id.info_favorited_tv)
    TextView info_favorited_tv;
    @BindView(R.id.info_user_tv)
    TextView info_user_tv;
    @BindView(R.id.info_email_tv)
    TextView info_email_tv;
    @BindView(R.id.user_photo)
    ImageView user_photo_iv;

    DatabaseReference myRef;
    private static final String TAG = DetailActivity.class.getSimpleName();
    ValueEventListener valueEventListener;
    private ValueEventListener mPostListener;
    private String mPostKey;
    public static final String EXTRA_POST_KEY = "post_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.app_name));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        userProfile();

        // Get post key from intent
        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        myRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("stories")
                .child(mPostKey);
        myRef.keepSynced(true);

    }

    @Override
    protected void onStart() {
        super.onStart();

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                MyStory myStory = dataSnapshot.getValue(MyStory.class);
                // [START_EXCLUDE]
                GlideApp.with(getApplicationContext())
                        .load(myStory.photo)
                        .fitCenter()
                        .into(activity_detail_image);
                prologue_tv.setText(myStory.prologue);
                body_tv.setText(myStory.body);
                epilogue_tv.setText(myStory.epilogue);
                info_date_tv.setText(myStory.date);
                info_favorited_tv.setText(String.valueOf(myStory.favorited));
                info_user_tv.setText(myStory.user);
                info_email_tv.setText(myStory.email);
                GlideApp.with(getApplicationContext())
                        .load(myStory.image)
                        .into(user_photo_iv);
                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(DetailActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        myRef.addValueEventListener(valueEventListener);
        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        mPostListener = valueEventListener;

    }

    @Override
    public void onStop() {
        super.onStop();
        // Remove post value event listener
        if (mPostListener != null) {
            myRef.removeEventListener(mPostListener);
        }

    }

}