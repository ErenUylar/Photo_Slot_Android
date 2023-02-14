package com.erenuylar.firebaseapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.erenuylar.firebaseapp.R;
import com.erenuylar.firebaseapp.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(MainActivity.this, FeedActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right, R.anim.left);
            finish();
        }
    }

    public void signIn(View view) {
        String mail = binding.mailText.getText().toString();
        String password = binding.passwordText.getText().toString();

        if (mail.equals("") || password.equals("")) {
            Snackbar.make(binding.getRoot(), "Fill in the Fields", Snackbar.LENGTH_LONG).show();
        } else {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Sign In");
            dialog.setMessage("Please Wait...");
            dialog.show();

            auth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        Intent intent = new Intent(MainActivity.this, FeedActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right, R.anim.left);
                        finish();
                        Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Snackbar.make(binding.getRoot(), Objects.requireNonNull(e.getLocalizedMessage()), Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }
}