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
import android.widget.Toast;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String EXTRA_EMAIL = "email";

    private ForgotPasswordViewModel viewModel;

    private EditText editTextEmailForgot;
    private Button buttonSendLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initViews();
        viewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);
        onObserveViewModel();
        setupOnClickListener();
        getSupportActionBar().setTitle(R.string.forgot_password_activity);
        String email = getIntent().getStringExtra(EXTRA_EMAIL);
        editTextEmailForgot.setText(email);
    }

    private void onObserveViewModel() {
        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (viewModel.getError() != null) {
                    Toast.makeText(ForgotPasswordActivity.this,
                            error,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewModel.inSuccess().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean inSuccess) {
                if (inSuccess) {
                    Toast.makeText(ForgotPasswordActivity.this,
                            R.string.toast_send_password,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initViews() {
        editTextEmailForgot = findViewById(R.id.editTextEmailForgot);
        buttonSendLink = findViewById(R.id.buttonSendLink);
    }

    private void setupOnClickListener() {
        buttonSendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmailForgot.getText().toString().trim();
                if (email.length() != 0) {
                    viewModel.resetPassword(email);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this,
                            R.string.fill_email_toast,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static Intent newIntent(Context context, String email) {
        Intent intent = new Intent(context, ForgotPasswordActivity.class);
        intent.putExtra(EXTRA_EMAIL, email);
        return intent;
    }
}