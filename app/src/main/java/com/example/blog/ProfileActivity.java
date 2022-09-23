package com.example.blog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.blog.adapters.ProfileAdapter;
import com.example.blog.pojo.User;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private Button buttonLogOut;
    private ImageView imageViewBack;
    private RecyclerView recyclerViewProfile;
    private TextView textViewVersionNumber;

    private ProfileAdapter adapter;

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
        setupOnClickListener();
        observeViewModel();
        setTextTextView();
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

    private void setTextTextView() {
        textViewVersionNumber.setText(String.valueOf(BuildConfig.VERSION_CODE));
        // textview other
    }

    private void initViews() {
        buttonLogOut = findViewById(R.id.buttonLogOut);
        imageViewBack = findViewById(R.id.imageViewBack);
        recyclerViewProfile = findViewById(R.id.recyclerViewProfile);
        textViewVersionNumber = findViewById(R.id.textViewVersionNumber);
    }
}