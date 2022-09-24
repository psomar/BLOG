package com.example.blog;

import static com.example.blog.R.string.main_activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.blog.adapters.PostAdapter;
import com.example.blog.pojo.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FieldValue;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FloatingActionButton buttonAddPost;
    private RecyclerView recyclerViewPost;
    private ProgressBar progressBarLoading;

    private MainViewModel viewModel;

    private PostAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        observeViewModel();
        adapter = new PostAdapter();
        recyclerViewPost.setAdapter(adapter);
        recyclerViewPost.setLayoutManager(new LinearLayoutManager(this));
        getSupportActionBar().setTitle(main_activity);
    }


   /* Если пользователь авторизован, то он может добавить пост. Если нет, то его перенесет
    на страницу регистрации.*/

    private void observeViewModel() {
        viewModel.getLogin().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean login) {
                if (login) {
                    buttonAddPost.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = AddPostActivity.newIntent(MainActivity.this);
                            startActivity(intent);
                        }
                    });
                } else {
                    buttonAddPost.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = LoginActivity.newIntent(MainActivity.this);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
        viewModel.getPosts().observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                adapter.setPosts(posts);
            }
        });
    }

    private void initViews() {
        buttonAddPost = findViewById(R.id.buttonAddPost);
        recyclerViewPost = findViewById(R.id.recyclerViewPost);
        progressBarLoading = findViewById(R.id.progressBarLoading);
    }


    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemProfile) {
            Intent intent = LoginActivity.newIntent(MainActivity.this);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }
}