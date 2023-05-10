package com.example.blog.models;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.blog.BuildConfig;
import com.example.blog.view.ProfileViewModel;
import com.example.blog.R;
import com.example.blog.adapters.ProfileAdapter;
import com.example.blog.pojo.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private Button buttonLogOut;
    private ImageView imageViewBack;
    private RecyclerView recyclerViewProfile;

    private ProfileAdapter adapter;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private ProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initViews();
        adapter = new ProfileAdapter();
        recyclerViewProfile.setAdapter(adapter);
        recyclerViewProfile.setLayoutManager(new LinearLayoutManager(this));
        hideActionBar();
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        observeViewModel();
        setupOnClickListener();
    }

    public void setupOnClickListener() {
        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.logOut();
                finish();
            }
        });
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = MainActivity.newIntent(ProfileActivity.this);
                startActivity(intent);
            }
        });
    }

    public void observeViewModel() {
        viewModel.getUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                adapter.setProfiles(users);
            }
        });
    }

    public static Intent newIntent(Context context, String currentUserId) {
        return new Intent(context, ProfileActivity.class);
    }

    public void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void initViews() {
        buttonLogOut = findViewById(R.id.buttonLogOut);
        imageViewBack = findViewById(R.id.imageViewBack);
        recyclerViewProfile = findViewById(R.id.recyclerViewProfile);
    }
}