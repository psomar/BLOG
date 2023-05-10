package com.example.blog.models;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.blog.R;
import com.example.blog.pojo.Post;
import com.example.blog.view.MainViewModel;

import java.util.Objects;

public class PostDetailActivity extends AppCompatActivity {

    private static final String EXTRA_POST = "post";

    private TextView textViewTitleDetail;
    private TextView textViewArticleDetail;
    private TextView textViewAuthorDetail;
    private ImageView imageViewPhotoAuthorDetail;
    private TextView textViewDataReleaseDetail;
    private ImageView imageViewPosterDetail;

    private MainViewModel viewModel;

    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        initViews();
        post = (Post) getIntent().getSerializableExtra(EXTRA_POST);
        setTextViews();
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        getSupportActionBar().hide();
    }


    private void setTextViews() {

        textViewTitleDetail.setText(post.getTitle());
        textViewArticleDetail.setText(post.getArticle());
        textViewDataReleaseDetail.setText(post.getTimestamp());
        textViewAuthorDetail.setText(post.getNickname());
        Glide.with(this)
                .load(post.getUrlAuthorPhoto())
                .into(imageViewPhotoAuthorDetail);
        Glide.with(this)
                .load(post.getUrlImage())
                .into(imageViewPosterDetail);

    }


    private void initViews() {
        textViewTitleDetail = findViewById(R.id.textViewTitleDetail);
        textViewArticleDetail = findViewById(R.id.textViewArticleDetail);
        textViewAuthorDetail = findViewById(R.id.textViewAuthorDetail);
        imageViewPhotoAuthorDetail = findViewById(R.id.imageViewPhotoAuthorDetail);
        textViewDataReleaseDetail = findViewById(R.id.textViewDataReleaseDetail);
        imageViewPosterDetail = findViewById(R.id.imageViewPosterDetail);
    }

    public static Intent postDetailIntent(Context context, Post post) {
        Intent intent = new Intent(context, PostDetailActivity.class);
        intent.putExtra(EXTRA_POST, post);
        return intent;
    }
}