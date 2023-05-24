package com.example.blog.views;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.blog.core.FirebaseStorageManager;
import com.example.blog.models.AddPostViewModel;
import com.example.blog.R;
import com.example.blog.databinding.ActivityAddPostBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AddPostActivity extends AppCompatActivity {

    private static final String EXTRA_IMAGE = "SendImageData";
    private static final String EXTRA_CROP = "CROP";


    private AddPostViewModel viewModel;

    private EditText editTextAddTitle;
    private EditText editTextAddArticle;
    private Button buttonSavePost;
    private ImageView imageViewAddPhoto;
    private ProgressBar progressBarPosts;

    private String postId;
    private String nickname;
    private static String result;
    private String profileImage;

    private Uri postImageUri = null;

    private FirebaseAuth auth = FirebaseAuth.getInstance();


    ActivityAddPostBinding binding;
    ActivityResultLauncher<String> cropImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        initViews();
        viewModel = new ViewModelProvider(this).get(AddPostViewModel.class);
        binding = ActivityAddPostBinding.inflate(getLayoutInflater());
        setupOnClickListener();
        observeViewModel();
        setContentView(binding.getRoot());
        cropImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            Intent intent = new Intent(AddPostActivity.this.getApplicationContext(),
                    UcropperActivity.class);
            intent.putExtra(EXTRA_IMAGE, result.toString());
            startActivityForResult(intent, 100);
        });
        getSupportActionBar().setTitle(R.string.add_post_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public static Intent newIntent(Context context) {
        return new Intent(context, AddPostActivity.class);
    }

    public void setupOnClickListener() {
        binding.imageViewAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePermission();
            }
        });
        binding.buttonSavePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePost();
            }
        });
    }

    private void savePost() {
        String title = binding.editTextAddTitle.getText().toString().trim();
        String article = binding.editTextAddArticle.getText().toString().trim();
        Date currentTime = Calendar.getInstance().getTime();
        postId = UUID.randomUUID().toString();
        int countComment = 0;
        DateFormat dateFormat = new SimpleDateFormat(getString(R.string.dateFormat),
                Locale.getDefault());
        String dateText = dateFormat.format(currentTime);
        if ((!title.isEmpty()) && (!article.isEmpty())) {
            Map<String, Object> postMap = new HashMap<>();
            postMap.put("postId", postId);
            postMap.put("userId", auth.getCurrentUser().getUid());
            postMap.put("title", title);
            postMap.put("article", article);
            postMap.put("nickname", nickname);
            postMap.put("countComment", countComment);
            postMap.put("timestamp", dateText);
            postMap.put("urlAuthorPhoto", profileImage);
            // Проверяем, добавлена ли фотография, если нет, То загруажем пост без фото.
            if (postImageUri != null) {
                binding.progressBarPosts.setVisibility(View.VISIBLE);
                uploadPostWithImage(postImageUri, postMap);
            } else {
                addPostToDatabase(postMap);
            }
        } else {
            Toast.makeText(AddPostActivity.this,
                    R.string.fill_all_fields_toast,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadPostWithImage(Uri imageUri, Map<String, Object> postMap) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();


        FirebaseStorageManager.uploadImageToFirebaseStorage(imageUri, postMap, storageReference,
                "images", UUID.randomUUID().toString(),
                () -> {
                    addPostToDatabase(postMap);
                },
                () -> {
                    Toast.makeText(AddPostActivity.this,
                            R.string.failed_for_load_image,
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void addPostToDatabase(Map<String, Object> postMap) {
        viewModel.addPost(postMap, postId);
        Toast.makeText(this,
                R.string.toast_post_published,
                Toast.LENGTH_SHORT).show();
        binding.progressBarPosts.setVisibility(View.GONE);
        finish();
    }

    private void observeViewModel() {
        viewModel.getNickname().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String nick) {
                if (nick != null) {
                    nickname = nick;
                }
            }
        });
        viewModel.getProfileImage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String image) {
                if (image != null) {
                    profileImage = image;
                }
            }
        });
        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(AddPostActivity.this,
                        error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void imagePermission() {

        Dexter.withContext(AddPostActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        cropImage.launch("image/*");
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(AddPostActivity.this,
                                R.string.failled_image_permission,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest,
                                                                   PermissionToken permissionToken) {
                        permissionToken.cancelPermissionRequest();
                    }
                }).check();
    }


    private void initViews() {
        editTextAddTitle = findViewById(R.id.editTextAddTitle);
        editTextAddArticle = findViewById(R.id.editTextAddArticle);
        imageViewAddPhoto = findViewById(R.id.imageViewAddPhoto);
        buttonSavePost = findViewById(R.id.buttonSavePost);
        progressBarPosts = findViewById(R.id.progressBarPosts);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 101) {
            result = data.getStringExtra(EXTRA_CROP);
            postImageUri = data.getData();
            if (result != null) {
                postImageUri = Uri.parse(result);
            }
            binding.imageViewAddPhoto.setImageURI(postImageUri);

        }
    }
}