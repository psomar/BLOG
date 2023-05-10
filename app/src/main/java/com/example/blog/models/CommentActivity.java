package com.example.blog.models;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blog.pojo.Post;
import com.example.blog.view.CommentViewModel;
import com.example.blog.R;
import com.example.blog.adapters.CommentAdapter;
import com.example.blog.pojo.Comment;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class CommentActivity extends AppCompatActivity {

    private static final String EXTRA_POST = "postId";

    private RecyclerView recyclerViewComment;
    private EditText editTextComment;
    private ImageView imageViewSendComment;
    private TextView textViewNoneComment;

    private CommentViewModel viewModel;
    private CommentAdapter commentAdapter;
    private Boolean commentDone = true;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ItemTouchHelper itemTouchHelper;

    private String nickname;
    private String profileImage;
    private String commentId;

    private Post post;
    private String idPost;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initViews();
        viewModel = new ViewModelProvider(this).get(CommentViewModel.class);
        commentAdapter = new CommentAdapter();
        recyclerViewComment.setAdapter(commentAdapter);
        setupOnClickListener();
        post = (Post) getIntent().getSerializableExtra(EXTRA_POST);
        idPost = post.getPostId();
        userId = post.getUserId();
        observeViewModel();
        recyclerViewComment.setLayoutManager(new LinearLayoutManager(this));
        getSupportActionBar().setTitle(R.string.title_comment);
        deletingCommentBySwipe();
    }

    public static Intent commentIntent(Context context, Post post) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra(EXTRA_POST, post);
        return intent;
    }

    public void setupOnClickListener() {
        imageViewSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveComment();
            }
        });
    }

    public void saveComment() {
        String comment = editTextComment.getText().toString().trim();
        Date currentTime = Calendar.getInstance().getTime();
        String postId = post.getPostId();
        commentId = UUID.randomUUID().toString();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy H:mm",
                Locale.getDefault());
        String dateText = dateFormat.format(currentTime);
        String userId = auth.getUid();
        if (!comment.isEmpty()) {
            Map<String, Object> commentMap = new HashMap<>();
            commentMap.put("idComment", commentId);
            commentMap.put("comment", comment);
            commentMap.put("timestamp", dateText);
            commentMap.put("postId", post.getPostId());
            commentMap.put("nickname", nickname);
            commentMap.put("userId", userId);
            commentMap.put("urlAuthorPhoto", profileImage);
            if (commentDone) {
                viewModel.addComment(commentMap, postId, commentId);
                viewModel.plusCommentCount(postId);
                editTextComment.getText().clear();
                Toast.makeText(this,
                        R.string.comment_is_release,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        R.string.attention_wrong,
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this,
                    R.string.fill_all_fields_toast,
                    Toast.LENGTH_SHORT).show();
        }
    }


    // При свайпе вправо удаляем комментарий.

    public void deletingCommentBySwipe() {
        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback
                (0,
                        ItemTouchHelper.RIGHT
                ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Comment comment = commentAdapter.getComments().get(position);

                if (comment.getUserId().equals(auth.getCurrentUser().getUid())) {

                    viewModel.deletePost(comment, post);
                    viewModel.minusCommentCount(post.getPostId());
                    Toast.makeText(CommentActivity.this,
                            R.string.comment_delete,
                            Toast.LENGTH_SHORT).show();
                } else {
                    itemTouchHelper.startSwipe(viewHolder);
                    commentAdapter.notifyItemChanged(position);

                }
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerViewComment);
    }

    private void observeViewModel() {
        viewModel.getLogin().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean login) {
                if (!login) {
                    editTextComment.setVisibility(View.GONE);
                    imageViewSendComment.setVisibility(View.GONE);
                } else {
                    editTextComment.setVisibility(View.VISIBLE);
                    imageViewSendComment.setVisibility(View.VISIBLE);
                }
            }
        });
        viewModel.getComments(idPost).observe(this, new Observer<List<Comment>>() {
            @Override
            public void onChanged(List<Comment> comments) {
                if (comments != null) {
                    commentAdapter.setComments(comments);
                    if (comments.size() == 0) {
                        textViewNoneComment.setVisibility(View.VISIBLE);
                    } else {
                        textViewNoneComment.setVisibility(View.GONE);
                    }
                }
            }
        });
        viewModel.getNickname().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String nick) {
                if (nick != null) {
                    nickname = nick;
                }
            }
        });
        viewModel.getProfileImage(userId).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String image) {
                profileImage = image;
            }
        });
    }

    private void initViews() {
        recyclerViewComment = findViewById(R.id.recyclerViewComment);
        editTextComment = findViewById(R.id.editTextComment);
        imageViewSendComment = findViewById(R.id.imageViewSendComment);
        textViewNoneComment = findViewById(R.id.textViewNoneComment);
    }
}