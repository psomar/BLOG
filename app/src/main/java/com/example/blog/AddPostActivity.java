package com.example.blog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.blog.pojo.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    private static final String TAG = "AddPostActivity";

    private AddPostViewModel viewModel;

    private List<Post> posts = new ArrayList<>();

    private EditText editTextAddTitle;
    private EditText editTextAddArticle;
    private Button buttonSavePost;
    private ImageView imageViewAddPhoto;

    private String currentUserId;
    private String nickname;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        initViews();
        currentUserId = auth.getCurrentUser().getUid();
        viewModel = new ViewModelProvider(this).get(AddPostViewModel.class);
        setupOnClickListener();
        getSupportActionBar().setTitle(R.string.add_post_activity);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, AddPostActivity.class);
    }

    public void setupOnClickListener() {
        imageViewAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // load image
            }
        });
        buttonSavePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePost();
            }
        });

    }

    private void savePost() {
        String title = editTextAddTitle.getText().toString().trim();
        String article = editTextAddArticle.getText().toString().trim();
        Date currentTime = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentTime);
        Log.i(TAG, String.valueOf(currentTime));
        viewModel.getNickname().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String nick) {
                if (nick != null) {
                    nickname = nick;
                }
            }
        });
        if ((title.length() != 0) && (article.length() != 0)) {
            Map<String, Object> postMap = new HashMap<>();
            postMap.put("user_Id", currentUserId);
            postMap.put("title", title);
            postMap.put("article", article);
            postMap.put("nickname", nickname);
            postMap.put("timestamp", dateText);
            viewModel.addPost(postMap);
            finish();
        } else {
            Toast.makeText(AddPostActivity.this,
                    R.string.fill_all_fields_toast,
                    Toast.LENGTH_SHORT).show();
        }
    }

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1 : {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    newPostDesc.setText(result.get(0));
                }
                break;
            }
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE: {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                    postImageUri = result.getUri();
                    newPostImage.setImageURI(postImageUri);

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                    Exception error = result.getError();

                }
                break;
            }

        }
    }*/


    private void initViews() {
        editTextAddTitle = findViewById(R.id.editTextAddTitle);
        editTextAddArticle = findViewById(R.id.editTextAddArticle);
        imageViewAddPhoto = findViewById(R.id.imageViewAddPhoto);
        buttonSavePost = findViewById(R.id.buttonSavePost);
    }


}