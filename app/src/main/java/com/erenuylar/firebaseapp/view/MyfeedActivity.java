package com.erenuylar.firebaseapp.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.erenuylar.firebaseapp.adapter.PostAdapter;
import com.erenuylar.firebaseapp.databinding.ActivityMyfeedBinding;
import com.erenuylar.firebaseapp.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MyfeedActivity extends AppCompatActivity {

    private ActivityMyfeedBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<Post> postArrayList;
    private PostAdapter postAdapter;
    private Boolean getDataBoolen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyfeedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        getDataBoolen = false;

        postArrayList = new ArrayList<>();
        postArrayList.clear();

        getData();

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                query();
            }
        });

        binding.recylerview.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(postArrayList);
        binding.recylerview.setAdapter(postAdapter);
        query();
    }

    private void getData() {

        Query query = firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Snackbar.make(binding.getRoot(), Objects.requireNonNull(error.getLocalizedMessage()), Snackbar.LENGTH_LONG).show();
                }

                if (value != null) {
                    postArrayList.clear();
                    postAdapter.notifyDataSetChanged();
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Map<String, Object> objectMap = snapshot.getData();

                        String uMail = (String) objectMap.get("userMail");
                        String uId = (String) objectMap.get("userId");
                        String uName = (String) objectMap.get("userName");
                        String uSname = (String) objectMap.get("userSname");
                        String downloadUrl = (String) objectMap.get("downloadUrl");
                        String comment = (String) objectMap.get("comment");

                        Date date = snapshot.getDate("date", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy  HH:mm", Locale.getDefault());
                        String newdate = dateFormat.format(Objects.requireNonNull(date));


                        Query queryPhoto = firebaseFirestore.collection("ProfilePhoto").whereEqualTo("userId", uId);

                        queryPhoto.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        for (DocumentSnapshot snapshot2 : task.getResult().getDocuments()) {
                                            String photo = (String) snapshot2.get("profilePhoto");
                                            if (Objects.equals(uMail, Objects.requireNonNull(auth.getCurrentUser()).getEmail())) {
                                                getDataBoolen = true;
                                                query();
                                                Post post = new Post(uName, uSname, downloadUrl, photo, newdate, comment);
                                                postArrayList.add(post);
                                                postAdapter.notifyDataSetChanged();
                                            } else {
                                                getDataBoolen = false;
                                                query();
                                                getData();
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
        binding.swipeRefresh.setRefreshing(false);
    }

    private void query() {
        if (!getDataBoolen) {
            binding.emptyImage.setVisibility(View.VISIBLE);
            binding.emptyText.setVisibility(View.VISIBLE);
        } else {
            binding.emptyImage.setVisibility(View.GONE);
            binding.emptyText.setVisibility(View.GONE);
        }
    }
}