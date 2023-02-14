package com.erenuylar.firebaseapp.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.erenuylar.firebaseapp.R;
import com.erenuylar.firebaseapp.databinding.ActivityUploadBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    private ActivityUploadBinding binding;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityResultLauncher<String> permissionResultLauncher;
    private Intent intentImage;
    private Uri uriData;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseUser user;
    private String getName;
    private String getSname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        launchRegister();
        binding.buttonUpload.setEnabled(false);

        binding.commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (binding.commentText.getText().toString().equals("")) {
                    binding.buttonUpload.setEnabled(false);
                } else {
                    binding.buttonUpload.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.commentText.getText().toString().equals("")) {
                    binding.buttonUpload.setEnabled(false);
                } else {
                    binding.buttonUpload.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.commentText.getText().toString().equals("")) {
                    binding.buttonUpload.setEnabled(false);
                } else {
                    binding.buttonUpload.setEnabled(true);
                }
            }
        });

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        user = auth.getCurrentUser();
    }

    public void selectImage(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(binding.getRoot(), "Permission Needed for Images!", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        permissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            } else {
                permissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentGallery);
        }
    }

    public void uploadButton(View view) {
        String uId = user.getUid();
        Query queryUser = firebaseFirestore.collection("Users").whereEqualTo("userId", uId);

        queryUser.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                            getName = (String) snapshot.get("userName");
                            getSname = (String) snapshot.get("userSname");
                            updatePost();
                        }
                    } else {
                        Snackbar.make(binding.getRoot(), "Complete Your Information", Snackbar.LENGTH_INDEFINITE).setAction("Complete", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(UploadActivity.this, MyaccountActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                overridePendingTransition(R.anim.right, R.anim.left);
                            }
                        }).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(binding.getRoot(), Objects.requireNonNull(e.getLocalizedMessage()), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void updatePost() {
        if (uriData != null) {
            UUID uuid = UUID.randomUUID();
            String imageName = "images/" + uuid + ".jpg";

            binding.buttonUpload.setEnabled(false);
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Uploading Images");
            dialog.setMessage("Please Wait...");
            dialog.show();

            storageReference.child(imageName).putFile(uriData).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        StorageReference newstorageReference = firebaseStorage.getReference(imageName);
                        newstorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                user = auth.getCurrentUser();

                                String uId = user.getUid();
                                String uMail = user.getEmail();
                                String uName = getName;
                                String uSname = getSname;
                                String downloadUrl = uri.toString();
                                String comment = binding.commentText.getText().toString();

                                Map<String, Object> map = new HashMap<>();
                                map.put("userId", uId);
                                map.put("userMail", uMail);
                                map.put("userName", uName);
                                map.put("userSname", uSname);
                                map.put("downloadUrl", downloadUrl);
                                map.put("comment", comment);
                                map.put("date", FieldValue.serverTimestamp());

                                firebaseFirestore.collection("Posts").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()) {
                                            dialog.dismiss();
                                            Snackbar.make(binding.getRoot(), "Post Uploaded Successful", Snackbar.LENGTH_LONG).show();
                                            binding.imageView.setImageResource(R.drawable.image);
                                            binding.commentText.setText("");
                                            uriData = null;
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(binding.getRoot(), Objects.requireNonNull(e.getLocalizedMessage()), Snackbar.LENGTH_LONG).show();
                                    }
                                });
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
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Snackbar.make(binding.getRoot(), Objects.requireNonNull(e.getLocalizedMessage()), Snackbar.LENGTH_LONG).show();
                }
            });
        } else {
            Snackbar.make(binding.getRoot(), "Selected Images!", Snackbar.LENGTH_LONG).show();
        }
    }

    private void launchRegister() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    intentImage = result.getData();
                    if (intentImage != null) {
                        uriData = intentImage.getData();
                        binding.imageView.setImageURI(uriData);
                    }
                }
            }
        });

        permissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentGallery);
                } else {
                    Snackbar.make(binding.getRoot(), "Permission Needed for Images!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}