package gr.mobap.mystories;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import gr.mobap.mystories.activities.AboutActivity;
import gr.mobap.mystories.activities.LoginActivity;
import gr.mobap.mystories.activities.PostActivity;
import gr.mobap.mystories.activities.StoriesActivity;
import gr.mobap.mystories.twitter.TimelineActivity;
import gr.mobap.mystories.utilities.GlideApp;


public class Base extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @Nullable
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent i = new Intent(Base.this, StoriesActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_post) {
            Intent i = new Intent(Base.this, PostActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_twitter) {
            Intent i = new Intent(Base.this, TimelineActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_login) {
            Intent i = new Intent(Base.this, LoginActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_logout) {
            Intent i = new Intent(Base.this, LoginActivity.class);
            startActivity(i);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stories, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent i = new Intent(Base.this, AboutActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    public void userProfile() {
        FirebaseAuth mFirebaseAuth;

        View navHeaderView = navigationView.getHeaderView(0);
        CircleImageView mDisplayImageView = navHeaderView.findViewById(R.id.personalImageView);
        TextView mNameTextView = navHeaderView.findViewById(R.id.name);
        TextView mEmailTextView = navHeaderView.findViewById(R.id.email);

        MenuItem mPostTextView = navigationView.getMenu().findItem(R.id.nav_post);
        MenuItem mLogInTextView = navigationView.getMenu().findItem(R.id.nav_login);
        MenuItem mLogOutTextView = navigationView.getMenu().findItem(R.id.nav_logout);
        mFirebaseAuth = FirebaseAuth.getInstance();
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
            mPostTextView.setVisible(false);
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

    public void shareSocial(CharSequence text, CharSequence subject, Uri imageUri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType(getString(R.string.type_img));
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject.toString());
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);

        // get available share intents
        List<Intent> targets = new ArrayList<>();
        Intent template = new Intent(Intent.ACTION_SEND);
        template.setType(getString(R.string.type_txt));
        List<ResolveInfo> candidates = this.getPackageManager().
                queryIntentActivities(template, 0);

        // remove facebook
        for (ResolveInfo candidate : candidates) {
            String packageName = candidate.activityInfo.packageName;
            if (!packageName.equals(getString(R.string.facebook))) {
                Intent target = new Intent(android.content.Intent.ACTION_SEND);
                target.setType(getString(R.string.type_txt));
                target.putExtra(Intent.EXTRA_TEXT, text);
                target.putExtra(Intent.EXTRA_SUBJECT, subject);
                target.setPackage(packageName);
                targets.add(target);
            }
        }
        Intent chooser = Intent.createChooser(targets.remove(0), getString(R.string.share));
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targets.toArray(new Parcelable[]{}));
        startActivity(chooser);
    }

}
