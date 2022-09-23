package com.example.blog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity {

    private RegistrationViewModel viewModel;

    private EditText editTextNickName;
    private EditText editTextEmailRegistration;
    private EditText editTexteditTextPasswordRegistration;
    private Button buttonSaveUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initViews();
        viewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);
        onObserveViewModel();
        setupOnClickListener();
        getSupportActionBar().setTitle(R.string.registation_activity);
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
        buttonSaveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nickName = editTextNickName.getText().toString().trim();
                String email = editTextEmailRegistration.getText().toString().trim();
                String password = editTexteditTextPasswordRegistration.getText().toString().trim();
                if ((nickName.length() != 0) && (email.length() != 0) && (password.length() != 0)) {
                    viewModel.registration(nickName, email, password);
                } else {
                    Toast.makeText(RegistrationActivity.this,
                            R.string.fill_all_fields_toast,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, RegistrationActivity.class);
    }

    private void initViews() {
        editTextNickName = findViewById(R.id.editTextNickName);
        editTextEmailRegistration = findViewById(R.id.editTextEmailRegistration);
        editTexteditTextPasswordRegistration = findViewById(R.id.editTextPasswordRegistration);
        buttonSaveUser = findViewById(R.id.buttonSaveUser);
    }
}