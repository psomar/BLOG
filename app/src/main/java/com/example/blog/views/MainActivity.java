package com.example.blog.views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blog.R;
import com.example.blog.adapters.PostAdapter;
import com.example.blog.pojo.Post;
import com.example.blog.models.MainViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;//

//  BLOG.java
//
//  Created by Petr Somar
//

public class MainActivity extends AppCompatActivity {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private FloatingActionButton buttonAddPost;
    private RecyclerView recyclerViewPost;
    private ProgressBar progressBarLoading;

    private boolean isFavourite;
    private boolean isLogin = false;

    private MainViewModel viewModel;

    private PostAdapter adapterPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        observeViewModel();
        adapterPost = new PostAdapter();
        recyclerViewPost.setAdapter(adapterPost);
        recyclerViewPost.setLayoutManager(new LinearLayoutManager(this));
        changeActionBar();
        setupAdapterClickListener();
        hideButton();
        deletingPostBySwipeAndAddFavourite();
    }


   /* Если пользователь авторизован, то он может добавить пост. Если нет, то его перенесет
    на страницу авторизации.*/

    private void observeViewModel() {
        viewModel.getLogin().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean login) {
                isLogin = login;
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
                adapterPost.setPosts(posts);
            }
        });
        viewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if (!isLoading) {
                    progressBarLoading.setVisibility(View.VISIBLE);
                } else {
                    progressBarLoading.setVisibility(View.GONE);
                }
            }
        });
        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(MainActivity.this,
                        error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*   При скроле вниз пропадает кнопка добавления поста, если начать скролить наверх, то кнопка
    вновь появляется */

    private void hideButton() {
        recyclerViewPost.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && buttonAddPost.getVisibility() == View.VISIBLE) {
                    buttonAddPost.hide();
                } else if (dy < 0 && buttonAddPost.getVisibility() != View.VISIBLE) {
                    buttonAddPost.show();
                }
            }
        });
    }

    /*    При свайпе влево добавляем пост в избранное, а при свайпе вправо удаляем пост из списка.*/

    public void deletingPostBySwipeAndAddFavourite() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback
                (0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT
                ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder,
                                 int direction) {
                if (auth.getCurrentUser() == null) {
                    adapterPost.notifyItemChanged(viewHolder.getAdapterPosition());
                    return;
                }
                int position = viewHolder.getAdapterPosition();
                Post post = adapterPost.getPosts().get(position);
                boolean isAccessDelete = post.getUserId().equals(auth.getCurrentUser().getUid());
                viewModel.toggleFavorite(post).observe(MainActivity.this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean favourite) {
                        isFavourite = favourite;
                    }
                });
                if (direction == ItemTouchHelper.RIGHT && isAccessDelete) {
                    showDeletePostDialog(post, viewHolder);
                } else if (direction == ItemTouchHelper.LEFT && isFavourite) {
                    showDeleteFromFavoritesDialog(post, position);
                } else if (direction == ItemTouchHelper.LEFT) {
                    viewModel.addPostToFavorites(post).observe(MainActivity.this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean success) {
                            if (success) {
                                Toast.makeText(MainActivity.this,
                                        R.string.post_added_favourite,
                                        Toast.LENGTH_SHORT).show();
                                adapterPost.notifyItemChanged(position);
                                isFavourite = true;
                            }
                        }
                    });
                } else {
                    adapterPost.notifyItemChanged(position);
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerViewPost);
    }

    private void changeActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.mipmap.ic_launcher_round);
            actionBar.setDisplayUseLogoEnabled(true);
        }
    }

    private void showDeleteFromFavoritesDialog(Post post, int position) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.confirm_delete_favourite_title)
                .setMessage(R.string.confirm_delete_favourite_messege)
                .setPositiveButton(R.string.confirm_delete_favourite_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        viewModel.deleteFromFavorites(post);
                        Toast.makeText(MainActivity.this,
                                R.string.favourite_delete,
                                Toast.LENGTH_SHORT).show();
                        adapterPost.notifyItemChanged(position);
                        isFavourite = false;
                    }
                })
                .setNegativeButton(R.string.confirm_delete_favourite_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        adapterPost.notifyItemChanged(position);
                    }
                })
                .show();
    }

    private void showDeletePostDialog(Post post,
                                      RecyclerView.ViewHolder viewHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm_delete_post_title)
                .setMessage(R.string.confirm_delete_post_message)
                .setPositiveButton(R.string.confirm_delete_post_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int position = viewHolder.getAdapterPosition();
                        viewModel.deletePost(post);
                        Toast.makeText(MainActivity.this,
                                R.string.post_delete,
                                Toast.LENGTH_SHORT).show();
                        adapterPost.notifyItemChanged(position);
                    }
                })
                .setNegativeButton(R.string.confirm_delete_post_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int position = viewHolder.getAdapterPosition();
                        adapterPost.getPosts().get(position);
                        adapterPost.notifyItemChanged(position);
                        dialog.dismiss();

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void initViews() {
        buttonAddPost = findViewById(R.id.buttonAddPost);
        recyclerViewPost = findViewById(R.id.recyclerViewPost);
        progressBarLoading = findViewById(R.id.progressBarLoading);
    }

    private void setupAdapterClickListener() {
        adapterPost.setOnClickTitle(new PostAdapter.onClickTitle() {
            @Override
            public void onCLickTitle(Post post) {
                Intent intent = PostDetailActivity.postDetailIntent(MainActivity.this,
                        post);
                startActivity(intent);
            }
        });
        adapterPost.setOnClickComment(new PostAdapter.onClickComment() {
            @Override
            public void onClickComment(Post post) {
                Intent intent;
                if (isLogin) {
                    intent = CommentActivity.commentIntent(MainActivity.this,
                            post);
                } else {
                    intent = LoginActivity.newIntent(MainActivity.this);
                }
                startActivity(intent);
            }
        });
        adapterPost.setOnClickUser(new PostAdapter.onClickUser() {
            @Override
            public void onClickUser(Post post) {
                if (auth.getCurrentUser() != null) {
                    boolean isCurrentUser;
                    isCurrentUser = post.getUserId().equals(auth.getCurrentUser().getUid());
                    Intent intent;
                    if (isCurrentUser) {
                        intent = ProfileActivity.newIntent(MainActivity.this,
                                post.getUserId());
                    } else {
                        intent = ProfileAnotherUserActivity.anotherUserIntent(MainActivity.this,
                                post);
                    }
                    startActivity(intent);
                } else {
                    Intent intent = LoginActivity.newIntent(MainActivity.this);
                    startActivity(intent);
                }
            }
        });
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