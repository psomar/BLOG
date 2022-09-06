package com.example.blog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AddPostActivity extends AppCompatActivity {


    private AddPostViewModel viewModel;


    private EditText editTextAddTitle;
    private EditText editTextAddArticle;
    private Button buttonAddArticle;
    private Button buttonAddImage;
    private Button buttonSavePost;
    private ImageView imageViewSeePost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        initViews();
        viewModel = new ViewModelProvider(this).get(AddPostViewModel.class);
        setupOnClickListener();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, AddPostActivity.class);
    }

    public void setupOnClickListener() {
        buttonAddArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextAddArticle.setVisibility(View.VISIBLE);
            }
        });
        buttonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // load image
            }
        });
        buttonSavePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Post post = new Post();
                String id = post.getId();
                String title = editTextAddTitle.getText().toString().trim();
                String article = editTextAddArticle.getText().toString().trim();
                String nickname = post.getUser().getNickname().trim();
                double dataRelease = post.getDataRelease();
                int like = post.getLike();
                int dislike = post.getDislike();
                int urlAuthorPhoto = post.getUrlAuthorPhoto();
                viewModel.addPost(id,
                        title,
                        article,
                        nickname,
                        dataRelease,
                        like,
                        dislike,
                        urlAuthorPhoto);
                Intent intent = MainActivity.newIntent(AddPostActivity.this);
                startActivity(intent);
            }
        });
        imageViewSeePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check in post before publication
            }
        });
    }

    private void initViews() {
        editTextAddTitle = findViewById(R.id.editTextAddTitle);
        editTextAddArticle = findViewById(R.id.editTextAddArticle);
        buttonAddArticle = findViewById(R.id.buttonAddArticle);
        buttonAddImage = findViewById(R.id.buttonAddImage);
        buttonSavePost = findViewById(R.id.buttonSavePost);
        imageViewSeePost = findViewById(R.id.imageViewSeePost);
    }

}