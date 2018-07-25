package gr.mobap.mystories.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import gr.mobap.mystories.Base;
import gr.mobap.mystories.R;

public class DetailActivity extends Base {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

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

    FirebaseRecyclerAdapter adapter;
    DatabaseReference myRef;
    private static final String TAG = DetailActivity.class.getSimpleName();
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


    }

}