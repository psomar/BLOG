package com.example.blog.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.blog.R;
import com.example.blog.adapters.CommentAdapter;
import com.example.blog.adapters.PostDetailAdapter;
import com.example.blog.pojo.Comment;
import com.example.blog.pojo.Post;
import com.example.blog.models.PostDetailViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    private static final String EXTRA_POST = "post";

    private RecyclerView recyclerViewComments;
    private RecyclerView recyclerViewPostsDetail;
    private TextView textViewNoneComments;
    private NestedScrollView nestedScrollView;

    private PostDetailAdapter postDetailAdapter;
    private CommentAdapter commentAdapter;
    private PostDetailViewModel viewModel;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private Post post;
    private boolean isLogin;
    private boolean isAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        initViews();
        viewModel = new ViewModelProvider(this).get(PostDetailViewModel.class);
        commentAdapter = new CommentAdapter();
        postDetailAdapter = new PostDetailAdapter();
        recyclerViewPostsDetail.setAdapter(postDetailAdapter);
        recyclerViewPostsDetail.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewComments.setAdapter(commentAdapter);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
        post = (Post) getIntent().getSerializableExtra(EXTRA_POST);
        observeViewModel();
        setupOnClickListener();
        getSupportActionBar().setTitle(post.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void observeViewModel() {
        String postId = post.getPostId();
        viewModel.getComments(postId).observe(this, new Observer<List<Comment>>() {
            @Override
            public void onChanged(List<Comment> comments) {
                commentAdapter.setComments(comments);
                if (comments.size() == 0) {
                    textViewNoneComments.setVisibility(View.VISIBLE);
                } else {
                    textViewNoneComments.setVisibility(View.GONE);
                }
            }
        });
        viewModel.getPosts(postId).observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                postDetailAdapter.setPosts(posts);
            }
        });
        viewModel.getLogin().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean login) {
                isLogin = login;
            }
        });
    }


    private void setupOnClickListener() {
        textViewNoneComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (isLogin) {
                    intent = CommentActivity.commentIntent(PostDetailActivity.this, post);
                } else {
                    intent = LoginActivity.newIntent(PostDetailActivity.this);
                }
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        if (!(auth.getCurrentUser() == null)) {
            isAccess = auth.getCurrentUser().getUid().equals(post.getUserId());
        }
        if (item.getItemId() == R.id.itemEditor && isAccess) {
            intent = PostEditorActivity.postEditorIntent(PostDetailActivity.this, post);
            startActivity(intent);
        } else if (item.getItemId() == R.id.itemEditor && !isAccess) {
            intent = LoginActivity.newIntent(PostDetailActivity.this);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.itemAddComment && isLogin) {
            intent = CommentActivity.commentIntent(PostDetailActivity.this, post);
            startActivity(intent);
        } else if (item.getItemId() == R.id.itemAddComment && !isLogin) {
            intent = LoginActivity.newIntent(PostDetailActivity.this);
            startActivity(intent);
        }
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initViews() {
        recyclerViewPostsDetail = findViewById(R.id.recyclerViewPostsDetail);
        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        textViewNoneComments = findViewById(R.id.textViewNoneComments);
        nestedScrollView = findViewById(R.id.nestedScrollView);
    }

    public static Intent postDetailIntent(Context context, Post post) {
        Intent intent = new Intent(context, PostDetailActivity.class);
        intent.putExtra(EXTRA_POST, post);
        return intent;
    }
}