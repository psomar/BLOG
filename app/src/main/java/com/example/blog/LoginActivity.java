package com.example.blog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewForgotPassword;
    private Button buttonSignIn;
    private Button buttonRegistration;

    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        observeViewModel();
        setupClickListener();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    public void initViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        buttonRegistration = findViewById(R.id.buttonRegistration);
    }

    public void setupClickListener() {
        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email = editTextEmail.getText().toString().trim();
                        Intent intent = ForgotPasswordActivity.newIntent(LoginActivity.this,
                                email);
                        startActivity(intent);
                    }
                });
            }
        });
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                if ((password.length() != 0) && (email.length() != 0)) { // Проверяем заполнены ли все поля
                    viewModel.login(email, password);
                } else {
                    Toast.makeText(LoginActivity.this,
                            R.string.fill_all_fields_toast,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        buttonRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = RegistrationActivity.newIntent(LoginActivity.this);
                startActivity(intent);
            }
        });
    }

    public void observeViewModel() {
        viewModel.getOnError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                if (errorMessage != null) {
                    Toast.makeText(LoginActivity.this,
                            errorMessage,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewModel.getCurrentUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null) {
                    Intent intent = ProfileActivity.newIntent(LoginActivity.this);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}