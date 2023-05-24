package com.example.blog.views;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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

import com.bumptech.glide.Glide;
import com.example.blog.R;
import com.example.blog.core.FirebaseStorageManager;
import com.example.blog.databinding.ActivityPostEditorBinding;
import com.example.blog.pojo.Post;
import com.example.blog.models.PostEditorViewModel;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class PostEditorActivity extends AppCompatActivity {

    private static final String EXTRA_POST = "post";
    private static final String EXTRA_IMAGE = "SendImageData";
    private static final String EXTRA_CROP = "CROP";

    private PostEditorViewModel viewModel;

    private EditText editTextTitleEditor;
    private EditText editTextArticleEditor;
    private ImageView imageViewPhotoEditor;
    private Button buttonSaveEdit;
    private ProgressBar progressBarEdit;


    private static String result;
    private Uri postImageUri = null;
    private Post post;

    ActivityPostEditorBinding binding;
    ActivityResultLauncher<String> cropImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_editor);
        initViews();
        viewModel = new ViewModelProvider(this).get(PostEditorViewModel.class);
        binding = ActivityPostEditorBinding.inflate(getLayoutInflater());
        post = (Post) getIntent().getSerializableExtra(EXTRA_POST);
        setupTextViews();
        setupOnClickListener();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        observeViewModel();
        setContentView(binding.getRoot());
        cropImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            Intent intent = new Intent(PostEditorActivity.this.getApplicationContext(),
                    UcropperActivity.class);
            intent.putExtra(EXTRA_IMAGE, result.toString());
            startActivityForResult(intent, 100);
        });
    }

    public static Intent postEditorIntent(Context context, Post post) {
        Intent intent = new Intent(context, PostEditorActivity.class);
        intent.putExtra(EXTRA_POST, post);
        return intent;
    }

    public void saveEdit() {
        String newTitle = binding.editTextTitleEditor.getText().toString().trim();
        String newArticle = binding.editTextArticleEditor.getText().toString().trim();
        if (!newTitle.isEmpty() && !newArticle.isEmpty()) {
            Map<String, Object> editMap = new HashMap<>();
            editMap.put("title", newTitle);
            editMap.put("article", newArticle);
            if (postImageUri != null) {
                binding.progressBarEdit.setVisibility(View.VISIBLE);
                uploadPostWithImage(postImageUri, editMap);
            } else {
                addPostToDatabase(editMap);
            }

        } else {
            Toast.makeText(this,
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
                    Toast.makeText(PostEditorActivity.this,
                            R.string.failed_for_load_image,
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void addPostToDatabase(Map<String, Object> postMap) {
        String postId = post.getPostId();
        viewModel.editPost(postMap, postId);
        Toast.makeText(this,
                R.string.edit_saved,
                Toast.LENGTH_SHORT).show();
        binding.progressBarEdit.setVisibility(View.GONE);
        finish();
    }

    public void setupOnClickListener() {
        binding.buttonSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEdit();
            }
        });
        binding.imageViewPhotoEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePermission();
            }
        });
    }

    public void setupTextViews() {
        binding.editTextTitleEditor.setText(post.getTitle());
        binding.editTextArticleEditor.setText(post.getArticle());
        Glide.with(this)
                .load(post.getUrlImage())
                .into(binding.imageViewPhotoEditor);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void observeViewModel() {
        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(PostEditorActivity.this,
                        error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void imagePermission() {

        Dexter.withContext(PostEditorActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        cropImage.launch("image/*");
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(PostEditorActivity.this,
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 101) {
            result = data.getStringExtra(EXTRA_CROP);
            postImageUri = data.getData();
            if (result != null) {
                postImageUri = Uri.parse(result);
            }
            binding.imageViewPhotoEditor.setImageURI(postImageUri);

        }
    }

    private void initViews() {
        editTextTitleEditor = findViewById(R.id.editTextTitleEditor);
        editTextArticleEditor = findViewById(R.id.editTextArticleEditor);
        imageViewPhotoEditor = findViewById(R.id.imageViewPhotoEditor);
        buttonSaveEdit = findViewById(R.id.buttonSaveEdit);
        progressBarEdit = findViewById(R.id.progressBarEdit);
    }
}