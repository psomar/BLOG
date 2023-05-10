package com.example.blog.models;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.blog.pojo.User;
import com.example.blog.view.AddPostViewModel;
import com.example.blog.R;
import com.example.blog.databinding.ActivityAddPostBinding;
import com.example.blog.pojo.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AddPostActivity extends AppCompatActivity {

    private static final String TAG = "AddPostActivity";
    private static final String EXTRA_IMAGE = "SendImageData";
    private static final String EXTRA_CROP = "CROP";


    private AddPostViewModel viewModel;

    private List<Post> posts = new ArrayList<>();

    private EditText editTextAddTitle;
    private EditText editTextAddArticle;
    private Button buttonSavePost;
    private ImageView imageViewAddPhoto;

    private String idPost;
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
        idPost = UUID.randomUUID().toString();
        int countComment = 0;
        DateFormat dateFormat = new SimpleDateFormat(getString(R.string.dateFormat),
                Locale.getDefault());
        String dateText = dateFormat.format(currentTime);
        if ((title.length() != 0) && (article.length() != 0)) {
            Map<String, Object> postMap = new HashMap<>();
            postMap.put("postId", idPost);
            postMap.put("userId", auth.getCurrentUser().getUid());
            postMap.put("title", title);
            postMap.put("article", article);
            postMap.put("nickname", nickname);
            postMap.put("countComment", countComment);
            postMap.put("timestamp", dateText);
            postMap.put("urlAuthorPhoto", profileImage);
            // Проверяем, есть ли изображение для загрузки
            if (postImageUri != null) {
                uploadImageToFirebaseStorage(postImageUri, idPost, postMap);
            } else {
                addPostToDatabase(postMap);
            }

        } else {
            Toast.makeText(AddPostActivity.this,
                    R.string.fill_all_fields_toast,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri, String postId, Map<String, Object> postMap) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("images/" + UUID.randomUUID().toString());
        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        postMap.put("urlImage", uri.toString());
                        addPostToDatabase(postMap);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddPostActivity.this,
                            R.string.failed_for_load_image,
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void addPostToDatabase(Map<String, Object> postMap) {
        viewModel.addPost(postMap, idPost);
        Toast.makeText(this,
                R.string.toast_post_published,
                Toast.LENGTH_SHORT).show();
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
                                "Permission Denied",
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