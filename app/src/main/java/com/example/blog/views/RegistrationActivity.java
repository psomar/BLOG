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
import android.widget.Toast;

import com.example.blog.R;
import com.example.blog.databinding.ActivityRegistrationBinding;
import com.example.blog.models.RegistrationViewModel;
import com.google.firebase.auth.FirebaseUser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


public class RegistrationActivity extends AppCompatActivity {

    private static final String EXTRA_CROP = "CROP";
    private static final String EXTRA_IMAGE = "SendImageData";
    private static final int REQUEST_IMAGE_GET = 1;

    private RegistrationViewModel viewModel;

    private ImageView imageViewChangeAvatar;
    private EditText editTextNickName;
    private EditText editTextEmailRegistration;
    private EditText editTextPasswordRegistration;
    private Button buttonSaveUser;


    ActivityRegistrationBinding binding;
    ActivityResultLauncher<String> cropImage;

    private Uri postImageUri = null;
    private String profileImage;

    private static String result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initViews();
        viewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setupOnClickListener();
        onObserveViewModel();
        setContentView(binding.getRoot());
        cropImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            Intent intent = new Intent(RegistrationActivity.this.getApplicationContext(),
                    UcropperActivity.class);
            intent.putExtra(EXTRA_IMAGE, result.toString());
            startActivityForResult(intent, 100);
        });
        getSupportActionBar().setTitle(R.string.registration_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void onObserveViewModel() {
        viewModel.getOnError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (viewModel.getOnError() != null) {
                    Toast.makeText(RegistrationActivity.this,
                            error,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewModel.getCurrentUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                Intent intent = MainActivity.newIntent(RegistrationActivity.this);
                startActivity(intent);
            }
        });
    }

    public void setupOnClickListener() {
        binding.buttonSaveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nickName = binding.editTextNickName.getText().toString().trim();
                String email = binding.editTextEmailRegistration.getText().toString().trim();
                String password = binding.editTextPasswordRegistration.getText().toString().trim();
                profileImage = result;
                if ((!nickName.isEmpty()) && (!email.isEmpty()) &&
                        (!password.isEmpty()) && (profileImage != null)) {
                    viewModel.registration(nickName, email, password, profileImage);
                    Toast.makeText(RegistrationActivity.this,
                            R.string.user_has_registration,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegistrationActivity.this,
                            R.string.fill_all_fields_toast,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.imageViewChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePermission();
            }
        });

    }

    private void imagePermission() {

        Dexter.withContext(RegistrationActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        cropImage.launch("image/*");
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(RegistrationActivity.this,
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

    public static Intent newIntent(Context context) {
        return new Intent(context, RegistrationActivity.class);
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
            binding.imageViewChangeAvatar.setImageURI(postImageUri);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        imageViewChangeAvatar = findViewById(R.id.imageViewChangeAvatar);
        editTextNickName = findViewById(R.id.editTextNickName);
        editTextEmailRegistration = findViewById(R.id.editTextEmailRegistration);
        editTextPasswordRegistration = findViewById(R.id.editTextPasswordRegistration);
        buttonSaveUser = findViewById(R.id.buttonSaveUser);
    }
}