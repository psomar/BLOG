package com.example.blog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class CommentActivity extends AppCompatActivity {

    // Изменить название активити потом getSupportActionBar.setTitle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
    }
}