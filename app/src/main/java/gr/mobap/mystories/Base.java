package gr.mobap.mystories;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import gr.mobap.mystories.activities.AboutActivity;
import gr.mobap.mystories.activities.LoginActivity;
import gr.mobap.mystories.activities.StoriesActivity;
import gr.mobap.mystories.twitter.TimelineActivity;


public class Base extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent i = new Intent(Base.this, StoriesActivity.class);
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
}
