package gr.mobap.mystories.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import gr.mobap.mystories.Base;
import gr.mobap.mystories.R;
import gr.mobap.mystories.utilities.GlideApp;

public class PostActivity extends Base {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.getTitle)
    EditText getTitle;
    @BindView(R.id.getPrologue)
    EditText getPrologue;
    @BindView(R.id.getBody)
    EditText getBody;
    @BindView(R.id.getEpilogue)
    EditText getEpilogue;
    @BindView(R.id.getType)
    RadioGroup getType;
    @BindView(R.id.adventure)
    RadioButton adventure;
    @BindView(R.id.comedy)
    RadioButton comedy;
    @BindView(R.id.fairy)
    RadioButton fairy;
    @BindView(R.id.fiction)
    RadioButton fiction;
    @BindView(R.id.romance)
    RadioButton romance;
    @BindView(R.id.thriller)
    RadioButton thriller;
    @BindView(R.id.imageBtn)
    ImageButton imageButton;
    @BindView(R.id.getDate)
    TextView getDate;
    @BindView(R.id.getUsername)
    TextView getUsername;
    @BindView(R.id.authorImageView)
    CircleImageView authorImageView;
    @BindView(R.id.btnPost)
    Button btnPost;
    @BindView(R.id.titleNo)
    TextView titleNo;
    @BindView(R.id.prologueNo)
    TextView prologueNo;
    @BindView(R.id.bodyNo)
    TextView bodyNo;
    @BindView(R.id.epilogueNo)
    TextView epilogueNo;

    private static final String TAG = PostActivity.class.getSimpleName();
    private static final int GALLERY_REQUEST_CODE = 100;
    private Uri uri = null;
    private StorageReference mStorageRef;
    DatabaseReference myRef;
    Uri downloadUrl;
    String type;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    int MIN_CHAR = 80;
    int MAX_WORDS = 450;
    int MAX_WORDS_TITLE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.post));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getType.check(R.id.adventure); // radiogroup should always has a value

        navigationView.setNavigationItemSelectedListener(this);
        userProfile();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        myRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("stories");

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        DateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
        final String date = df.format(Calendar.getInstance().getTime());
        getDate.setText(date);
        getUsername.setText(mFirebaseUser.getDisplayName());

        if (mFirebaseUser.getPhotoUrl() != null) {
            GlideApp
                    .with(this)
                    .load(mFirebaseUser.getPhotoUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .error(android.R.drawable.sym_def_app_icon)
                    .centerCrop()
                    .placeholder(android.R.drawable.sym_def_app_icon)
                    .into(authorImageView);
        }

        //to upload an image from phones gallery
        imageButton.setOnClickListener(view -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setType(getString(R.string.type_img));
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(galleryIntent, getString(R.string.select_img)), GALLERY_REQUEST_CODE);
        });

        getPrologue.setFocusable(true);
        getBody.setFocusable(true);
        getEpilogue.setFocusable(true);

        getTitle.clearFocus();
        getPrologue.clearFocus();
        getBody.clearFocus();
        getEpilogue.clearFocus();

        // controls the edit text fields and sets max limit
        getTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int wordsLength = countWords(s.toString());
                if (count == 0 && wordsLength >= MAX_WORDS_TITLE) {
                    setCharLimit(getTitle, getTitle.getText().length());
                } else {
                    removeFilter(getTitle);
                }

                titleNo.setText(getString(R.string.words_no, wordsLength, MAX_WORDS_TITLE));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // controls the edit text fields and sets max limit
        getPrologue.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setOnFocusChangeListener(getPrologue);
                int wordsLength = countWords(s.toString());
                if (count == 0 && wordsLength >= MAX_WORDS) {
                    setCharLimit(getPrologue, getPrologue.getText().length());
                } else {
                    removeFilter(getPrologue);
                }

                prologueNo.setText(getString(R.string.words_no, wordsLength, MAX_WORDS));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // controls the edit text fields and sets max limit
        getBody.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int wordsLength = countWords(s.toString());
                setOnFocusChangeListener(getBody);

                if (count == 0 && wordsLength >= MAX_WORDS) {
                    setCharLimit(getBody, getBody.getText().length());
                } else {
                    removeFilter(getBody);
                }

                bodyNo.setText(getString(R.string.words_no, wordsLength, MAX_WORDS));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // controls the edit text fields and sets max limit
        getEpilogue.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setOnFocusChangeListener(getEpilogue);
                int wordsLength = countWords(s.toString());
                if (count == 0 && wordsLength >= MAX_WORDS) {
                    setCharLimit(getEpilogue, getEpilogue.getText().length());
                } else {
                    removeFilter(getEpilogue);
                }

                epilogueNo.setText(getString(R.string.words_no, wordsLength, MAX_WORDS));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // posting to Firebase
        btnPost.setOnClickListener(view -> {
            if (uri == null) { // check if user choose an image
                Toast.makeText(PostActivity.this, getString(R.string.check_img), Toast.LENGTH_SHORT).show();

            } else {
                final String title = getTitle.getText().toString().trim();
                final String prologue = getPrologue.getText().toString().trim();
                final String body = getBody.getText().toString().trim();
                final String epilogue = getEpilogue.getText().toString().trim();

                int radioButtonID = getType.getCheckedRadioButtonId();
                RadioButton radioButton = getType.findViewById(radioButtonID);
                type = (String) radioButton.getText();

                // do a check for empty fields
                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(prologue) && !TextUtils.isEmpty(body) && !TextUtils.isEmpty(epilogue)) {
                    // Create the file metadata
                    StorageMetadata metadata = new StorageMetadata.Builder()
                            .setContentType(getString(R.string.type_img))
                            .build();

                    StorageReference uploadTask = mStorageRef.child(uri.getLastPathSegment());

                    // starts the task to upload image in firestore
                    uploadTask.putFile(uri, metadata).addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        Toast.makeText(PostActivity.this, getString(R.string.upload_done, progress), Toast.LENGTH_SHORT).show();
                    }).addOnPausedListener(taskSnapshot -> Toast.makeText(PostActivity.this, getString(R.string.upload_paused), Toast.LENGTH_SHORT).show()).addOnFailureListener(exception -> Toast.makeText(PostActivity.this, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show()).addOnSuccessListener(taskSnapshot -> {
                        // if uploading is successful starts to update the DB
                        uploadTask.getDownloadUrl().addOnSuccessListener(uri -> {
                            downloadUrl = uri;
                            Uri userPhoto = mFirebaseUser.getPhotoUrl();
                            final DatabaseReference newPost = myRef.push();
                            //adding post contents to database reference
                            newPost.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    HashMap<String, Boolean> fav = new HashMap<String, Boolean>() {{
                                        put(mFirebaseUser.getUid(), true);
                                    }};

                                    newPost.child("date").setValue(date);
                                    newPost.child("prologue").setValue(prologue);
                                    newPost.child("body").setValue(body);
                                    newPost.child("epilogue").setValue(epilogue);
                                    newPost.child("photo").setValue(uri.toString());
                                    newPost.child("title").setValue(title);
                                    newPost.child("user").setValue(mFirebaseUser.getDisplayName());
                                    newPost.child("email").setValue(mFirebaseUser.getEmail());
                                    newPost.child("favorited").setValue(1);
                                    newPost.child("fav").setValue(fav);
                                    newPost.child("image").setValue(userPhoto.toString());
                                    newPost.child("type").setValue(type).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(PostActivity.this, StoriesActivity.class);
                                            startActivity(intent);
                                            Toast.makeText(PostActivity.this, getString(R.string.upload_completed), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        });


                    });

                }
                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(prologue) || TextUtils.isEmpty(body) || TextUtils.isEmpty(epilogue)) {
                    Toast.makeText(PostActivity.this, getString(R.string.check_post), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            uri = data.getData();
            imageButton.setImageURI(uri);
        }
    }
    // function to count words
    private int countWords(String s) {
        String trim = s.trim();
        if (trim.isEmpty())
            return 0;
        return trim.split("\\s+").length; // separate string around spaces
    }

    private InputFilter filter;

    // set character limit
    private void setCharLimit(EditText et, int max) {
        filter = new InputFilter.LengthFilter(max);
        et.setFilters(new InputFilter[]{filter});
    }

    // remove filter if characters are inside limit
    private void removeFilter(EditText et) {
        if (filter != null) {
            et.setFilters(new InputFilter[0]);
            filter = null;
        }
    }

    // If user hasn't write enough sends a request, but do not stop him from submitting the story
    private void setOnFocusChangeListener(EditText editText) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (editText.getText().toString().trim().length() < MIN_CHAR) {
                editText.setError(getString(R.string.push));
            }
        });
    }

}