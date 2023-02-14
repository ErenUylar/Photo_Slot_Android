package com.erenuylar.firebaseapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.erenuylar.firebaseapp.R;
import com.erenuylar.firebaseapp.adapter.PostAdapter;
import com.erenuylar.firebaseapp.databinding.ActivityFeedBinding;
import com.erenuylar.firebaseapp.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class FeedActivity extends AppCompatActivity {

    private ActivityFeedBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private ArrayList<Post> postArrayList;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        postArrayList = new ArrayList<>();
        postArrayList.clear();

        getData();

        binding.recylerview.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(postArrayList);
        binding.recylerview.setAdapter(postAdapter);
    }

    private void getData() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_add:
                Intent intentUpload = new Intent(FeedActivity.this, UploadActivity.class);
                startActivity(intentUpload);
                overridePendingTransition(R.anim.right, R.anim.left);
                break;
            case R.id.option_myAccount:
                Intent intentmyAccount = new Intent(FeedActivity.this, MyaccountActivity.class);
                startActivity(intentmyAccount);
                overridePendingTransition(R.anim.right, R.anim.left);
                break;
            case R.id.option_myFeed:
                Intent intentmyFeed = new Intent(FeedActivity.this, MyfeedActivity.class);
                startActivity(intentmyFeed);
                overridePendingTransition(R.anim.right, R.anim.left);
                break;
            case R.id.option_logout:
                auth.signOut();
                Intent intentMain = new Intent(FeedActivity.this, MainActivity.class);
                startActivity(intentMain);
                intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                overridePendingTransition(R.anim.right, R.anim.left);
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}