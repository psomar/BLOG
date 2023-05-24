package com.example.blog.views;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.blog.models.ProfileViewModel;
import com.example.blog.R;
import com.example.blog.adapters.ProfileAdapter;
import com.example.blog.pojo.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

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
        setupAdapterClickListener();
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

    public void setupAdapterClickListener() {
        adapter.setOnClickNickName(new ProfileAdapter.onClickNickName() {
            @Override
            public void onClickNickName(User user) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle(R.string.change_nickname);

                final EditText input = new EditText(ProfileActivity.this);
                input.setText(user.getNickname());
                builder.setView(input);

                builder.setPositiveButton(R.string.nick_change_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        String newNickname = input.getText().toString().trim();
                        if (!newNickname.isEmpty()) {
                            user.setNickname(newNickname);
                            viewModel.changeNick(user, newNickname);
                            int userPosition = adapter.getUserPosition(user);
                            adapter.notifyItemChanged(userPosition);
                            Toast.makeText(ProfileActivity.this,
                                    getString(R.string.nick_sucsess_change) + newNickname,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.cancel();
                            Toast.makeText(ProfileActivity.this,
                                    R.string.fill_all_fields_toast,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton(R.string.nick_change_canceled, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
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
        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(ProfileActivity.this,
                        error,
                        Toast.LENGTH_SHORT).show();
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