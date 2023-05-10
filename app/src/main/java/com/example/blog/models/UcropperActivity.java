package com.example.blog.models;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.blog.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class UcropperActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference storage;
    private Boolean isSuccess = true;

    private static final String EXTRA_IMAGE = "SendImageData";
    private static final String EXTRA_CROP = "CROP";

    String sourceUri, destinationUri;
    Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ucropper);


        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance().getReference();

        Intent intent = getIntent();
        if (intent.getExtras() != null) {

            sourceUri = intent.getStringExtra(EXTRA_IMAGE);
            uri = Uri.parse(sourceUri);

        }

        destinationUri = UUID.randomUUID().toString() + ".jpg";

        UCrop.Options options = new UCrop.Options();

        UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationUri)))
                .withOptions(options)
                .withMaxResultSize(2000, 2000)
                .start(UcropperActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);


            Intent intent = new Intent();
            intent.putExtra(EXTRA_CROP, resultUri + "");
            setResult( 101, intent);
            finish();


        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }
}