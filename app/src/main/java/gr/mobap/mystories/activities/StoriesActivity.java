package gr.mobap.mystories.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import gr.mobap.mystories.Base;
import gr.mobap.mystories.R;
import gr.mobap.mystories.utilities.GlideApp;

public class StoriesActivity extends Base {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

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

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

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
    }

}
