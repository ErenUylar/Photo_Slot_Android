package com.erenuylar.firebaseapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.erenuylar.firebaseapp.R;
import com.erenuylar.firebaseapp.adapter.PostAdapter;
import com.erenuylar.firebaseapp.databinding.ActivityMyfeedBinding;
import com.erenuylar.firebaseapp.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyfeedActivity extends AppCompatActivity {

    private ActivityMyfeedBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<Post> postArrayList;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyfeedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        postArrayList = new ArrayList<>();
        postArrayList.clear();

        binding.recylerview.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(postArrayList);
        binding.recylerview.setAdapter(postAdapter);
    }

    private void getData() {

    }
}