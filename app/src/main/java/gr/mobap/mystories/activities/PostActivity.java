package gr.mobap.mystories.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
    @BindView(R.id.imageBtn)
    ImageButton imageButton;
    @BindView(R.id.getDate)
    TextView getDate;
    @BindView(R.id.getUsername)
    TextView getUsername;
    @BindView(R.id.authorImageView)
    CircleImageView authorImageView;
    @BindView(R.id.btnPost)
    Button post;

    private static final String TAG = PostActivity.class.getSimpleName();
    private static final int GALLERY_REQUEST_CODE = 100;
    private Uri uri = null;
    private StorageReference mStorageRef;
    DatabaseReference myRef, mAuthor;
    Uri downloadUrl;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    String type;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

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

        navigationView.setNavigationItemSelectedListener(this);
        userProfile();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        myRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("stories");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mAuthor = myRef.child(mFirebaseUser.getUid());

        DateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy");
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
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), GALLERY_REQUEST_CODE);
            }
        });
        // posting to Firebase
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PostActivity.this, "POSTING...", Toast.LENGTH_LONG).show();
                final String title = getTitle.getText().toString().trim();
                final String prologue = getPrologue.getText().toString().trim();
                final String body = getBody.getText().toString().trim();
                final String epilogue = getEpilogue.getText().toString().trim();
                getType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get selected radio button from radioGroup
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        // find the radiobutton by returned id
                        radioButton = (RadioButton) findViewById(selectedId);
                        type = radioButton.getText().toString();
                    }
                });


                // do a check for empty fields
                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(prologue) && !TextUtils.isEmpty(body) && !TextUtils.isEmpty(epilogue)) {
                    // Create the file metadata
                    StorageMetadata metadata = new StorageMetadata.Builder()
                            .setContentType("image/jpeg")
                            .build();

                    StorageReference uploadTask = mStorageRef.child(uri.getLastPathSegment());

                    uploadTask.putFile(uri, metadata).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            System.out.println("Upload is " + progress + "% done");
                        }
                    }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("Upload is paused");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            uploadTask.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = uri;
                                    Toast.makeText(getBaseContext(), "Upload success! URL - " + downloadUrl.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            final DatabaseReference newPost = myRef.push();
                            //adding post contents to database reference
                            mAuthor.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    newPost.child("title").setValue(title);
                                    newPost.child("prologue").setValue(prologue);
                                    newPost.child("body").setValue(body);
                                    newPost.child("epilogue").setValue(epilogue);
                                    newPost.child("type").setValue(type);
                                    newPost.child("imageUrl").setValue(downloadUrl.toString());
                                    newPost.child("date").setValue(date);
                                    newPost.child("username").setValue(dataSnapshot.child("user").getValue())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Intent intent = new Intent(PostActivity.this, StoriesActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });

                }
            }
        });


    }

    // Handle the Activity results.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            uri = data.getData();
            imageButton.setImageURI(uri);
        }
    }

}