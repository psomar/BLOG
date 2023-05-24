package com.example.blog.views;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.blog.R;
import com.example.blog.adapters.ProfileAdapter;
import com.example.blog.pojo.Post;
import com.example.blog.pojo.User;
import com.example.blog.models.ProfileAnotherUserViewModel;

import java.util.List;


public class ProfileAnotherUserActivity extends AppCompatActivity {


    private static final String EXTRA_POST = "post";

    private ProfileAnotherUserViewModel viewModel;
    private RecyclerView recyclerViewProfile;
    private ProfileAdapter profileAdapter;

    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_another_user);
        initViews();
        viewModel = new ViewModelProvider(this).get(ProfileAnotherUserViewModel.class);
        profileAdapter = new ProfileAdapter();
        recyclerViewProfile.setAdapter(profileAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerViewProfile.setLayoutManager(layoutManager);
        hideActionBar();
        post = (Post) getIntent().getSerializableExtra(EXTRA_POST);
        observeViewModel();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void observeViewModel() {
        String userId = post.getUserId();
        viewModel.getUsers(userId).observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                profileAdapter.setProfiles(users);
            }
        });
        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(ProfileAnotherUserActivity.this,
                        error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static Intent anotherUserIntent(Context context, Post post) {
        Intent intent = new Intent(context, ProfileAnotherUserActivity.class);
        intent.putExtra(EXTRA_POST, post);
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Переход к предыдущей Activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        recyclerViewProfile = findViewById(R.id.recyclerViewProfile);
    }
}