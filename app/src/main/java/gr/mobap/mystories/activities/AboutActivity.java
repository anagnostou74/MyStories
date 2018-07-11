package gr.mobap.mystories.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import gr.mobap.mystories.Base;
import gr.mobap.mystories.R;

public class AboutActivity extends Base {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.btnMail)
    Button button2;
    @BindView(R.id.btnVathm)
    Button button3;
    @BindView(R.id.about)
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.app_name));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        if (button2 != null) {
            button2.setOnClickListener(v -> mail());
        }

        if (button3 != null) {
            button3.setOnClickListener(v -> launchMarket());
        }

        final String htmlText = getResources().getString(R.string.about_text);

        if (tv != null) {
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            tv.setText(Html.fromHtml(htmlText));
        }
    }


    private void mail() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType(getString(R.string.type));
        i.putExtra(Intent.EXTRA_EMAIL,
                new String[]{getString(R.string.address_email)});
        i.putExtra(Intent.EXTRA_SUBJECT,
                getString(R.string.about_mail));
        i.putExtra(Intent.EXTRA_TEXT, getString(R.string.main_me));
        try {
            startActivity(Intent.createChooser(i,
                    getString(R.string.send)));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, getString(R.string.no_mail_programs),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void launchMarket() {

        Uri uri = Uri.parse(getString(R.string.market) + this.getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, getString(R.string.failed2), Toast.LENGTH_LONG)
                    .show();
        }
    }

}
