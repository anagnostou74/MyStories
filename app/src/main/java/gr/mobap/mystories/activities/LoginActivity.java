package gr.mobap.mystories.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import gr.mobap.mystories.Base;
import gr.mobap.mystories.R;
import gr.mobap.mystories.utilities.AndroidNetworkUtility;
import gr.mobap.mystories.utilities.GlideApp;

public class LoginActivity extends Base implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.sign_out_button)
    Button signOutButton;
    @BindView(R.id.imageViewUser)
    ImageView imageViewUser;
    @BindView(R.id.disconnect_button)
    Button disconnectButton;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.userEmail)
    TextView userEmail;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;

    private CircleImageView mDisplayImageView;
    private TextView mNameTextView;
    private TextView mEmailTextView;
    private MenuItem mLogInTextView;
    private MenuItem mLogOutTextView;
    private MenuItem mPostTextView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.app_name));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                recreate();
                supportInvalidateOptionsMenu();

            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

        navigationView.setNavigationItemSelectedListener(this);

        View navHeaderView = navigationView.getHeaderView(0);
        mDisplayImageView = navHeaderView.findViewById(R.id.personalImageView);
        mNameTextView = navHeaderView.findViewById(R.id.name);
        mEmailTextView = navHeaderView.findViewById(R.id.email);

        mPostTextView = navigationView.getMenu().findItem(R.id.nav_post);
        mLogInTextView = navigationView.getMenu().findItem(R.id.nav_login);
        mLogOutTextView = navigationView.getMenu().findItem(R.id.nav_logout);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = mFirebaseAuth -> {
            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
            updateUI(firebaseUser);
        };

    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.please_wait));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            } else {
                // Google Sign In failed, update UI appropriately
                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, getString(R.string.firebase_auth_google) + acct.getId());
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            Log.d(TAG, getString(R.string.sign_with_cred) + task.isSuccessful());
            if (!task.isSuccessful()) {
                mNameTextView.setText(task.getException().getMessage());
            } else {
                mNameTextView.setTextColor(Color.DKGRAY);
            }
            hideProgressDialog();
        });
    }

    private void signIn() {
        AndroidNetworkUtility androidNetworkUtility = new AndroidNetworkUtility();
        if (androidNetworkUtility.isConnected(this)) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            Toast.makeText(LoginActivity.this, getString(R.string.no_network),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void signOut() {
        AndroidNetworkUtility androidNetworkUtility = new AndroidNetworkUtility();
        if (androidNetworkUtility.isConnected(this)) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage(R.string.log_out);
            alert.setCancelable(false);
            alert.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                // Firebase sign out
                mAuth.signOut();
                // Google sign out
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        status -> updateUI(null)
                );
            });
            alert.setNegativeButton(android.R.string.no, (dialogInterface, i) -> dialogInterface.dismiss());
            alert.show();
        } else {
            Toast.makeText(LoginActivity.this, getString(R.string.no_network),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void revokeAccess() {
        AndroidNetworkUtility androidNetworkUtility = new AndroidNetworkUtility();
        if (androidNetworkUtility.isConnected(this)) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage(R.string.disconnect);
            alert.setCancelable(false);
            alert.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                // Firebase sign out
                mAuth.signOut();
                // Google revoke access
                Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                        status -> updateUI(null)
                );
            });
            alert.setNegativeButton(android.R.string.no, (dialogInterface, i) -> dialogInterface.dismiss());
            alert.show();
        } else {
            Toast.makeText(LoginActivity.this, getString(R.string.no_network),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // if user is login or not updates drawer and xml and hides or make visible fields
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            if (user.getPhotoUrl() != null) {
                GlideApp
                        .with(this)
                        .load(user.getPhotoUrl())
                        .centerCrop()
                        .placeholder(android.R.drawable.sym_def_app_icon)
                        .error(android.R.drawable.sym_def_app_icon)
                        .into(mDisplayImageView);

                GlideApp
                        .with(LoginActivity.this)
                        .load(user.getPhotoUrl())
                        .error(android.R.drawable.sym_def_app_icon)
                        .centerCrop()
                        .placeholder(android.R.drawable.sym_def_app_icon)
                        .into(imageViewUser);

            }
            mNameTextView.setText(user.getDisplayName());
            mEmailTextView.setText(user.getEmail());
            mLogInTextView.setVisible(false);
            userName.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            signOutButton.setVisibility(View.VISIBLE);
            disconnectButton.setVisibility(View.VISIBLE);
        } else {
            GlideApp
                    .with(this)
                    .load(R.drawable.ic_account)
                    .centerCrop()
                    .into(mDisplayImageView);
            GlideApp
                    .with(this)
                    .load(R.drawable.ic_account)
                    .centerCrop()
                    .into(imageViewUser);

            mNameTextView.setVisibility(View.GONE);
            mEmailTextView.setVisibility(View.GONE);
            mLogOutTextView.setVisible(false);
            mPostTextView.setVisible(false);
            userName.setText(R.string.app_name);
            userEmail.setText(R.string.email_user);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
            disconnectButton.setVisibility(View.GONE);
        }
        hideProgressDialog();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, getString(R.string.connection_fail) + connectionResult);
        Toast.makeText(this, getString(R.string.play_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
        }
    }

}
