package com.erenuylar.firebaseapp.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.erenuylar.firebaseapp.R;
import com.erenuylar.firebaseapp.databinding.ActivityMyaccountBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.security.KeyStore;
import java.util.HashMap;
import java.util.Objects;

public class MyaccountActivity extends AppCompatActivity {

    private ActivityMyaccountBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityResultLauncher<String> permissionResultLauncher;
    private Intent intentImage;
    private Uri uriData;
    private Boolean photoBoolen;
    private Boolean userBoolen;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyaccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        launchRegister();

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        user = auth.getCurrentUser();
        String uId = user.getUid();
        String uMail = user.getEmail();
        binding.postText.setText(uMail);

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Your Data is Loading");
        dialog.setMessage("Please Wait...");
        dialog.show();

        Query queryPhoto = firebaseFirestore.collection("ProfilePhoto").whereEqualTo("userId", uId);

        queryPhoto.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                            String photo = (String) snapshot.get("profilePhoto");
                            Picasso.get().load(photo).into(binding.imageView2);
                            photoBoolen = true;
                            Toast.makeText(MyaccountActivity.this, "foto var", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    } else {
                        binding.imageView2.setImageResource(R.drawable.account_circle);
                        photoBoolen = false;
                        uriData = null;
                        Toast.makeText(MyaccountActivity.this, "foto yok", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Snackbar.make(binding.getRoot(), Objects.requireNonNull(e.getLocalizedMessage()), Snackbar.LENGTH_LONG).show();
            }
        });

        Query queryUser = firebaseFirestore.collection("Users").whereEqualTo("userId", uId);

        queryUser.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                            binding.nameText.setText((String) snapshot.get("userName"));
                            binding.sNameText.setText((String) snapshot.get("userSname"));
                            userBoolen = true;
                            Toast.makeText(MyaccountActivity.this, "user var", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    } else {
                        userBoolen = false;
                        binding.nameText.setText("");
                        binding.sNameText.setText("");
                        Toast.makeText(MyaccountActivity.this, "user yok", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
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

    public void selectProfile(View view) {
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

    public void myInformation(View view) {
        String uId = user.getUid();
        Query queryUser = firebaseFirestore.collection("Users").whereEqualTo("userId", uId);
        queryUser.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                            String getName = (String) snapshot.get("userName");
                            String getSname = (String) snapshot.get("userSname");
                            if (!Objects.equals(getName, binding.nameText.getText().toString()) || !Objects.equals(getSname, binding.sNameText.getText().toString())) {
                                User();
                            }
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(binding.getRoot(), Objects.requireNonNull(e.getLocalizedMessage()), Snackbar.LENGTH_LONG).show();
            }
        });

        if (!userBoolen) {
            User();
        }

        if (intentImage != null) {
            Image();
        } else {
            if (!photoBoolen) {
                if (userBoolen) {
                    Snackbar.make(binding.getRoot(), "Selected Image", Snackbar.LENGTH_LONG).show();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(binding.getRoot(), "Selected Image", Snackbar.LENGTH_LONG).show();
                        }
                    }, 2500);
                }
            }
        }

        if (!Objects.equals(user.getEmail(), binding.postText.getText().toString())) {
            updateEmail();
        }

        if (!binding.passText.getText().toString().equals("")) {
            updatePassword();
        }
    }

    private void updateEmail() {
        if (!binding.postText.getText().toString().equals("")) {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Uploading");
            dialog.setMessage("Please Wait...");
            dialog.show();

            user.updateEmail(binding.postText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        Toast.makeText(MyaccountActivity.this, "posta ok", Toast.LENGTH_SHORT).show();
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
            binding.postText.setError("Enter Your E-mail Address");
        }
    }

    private void updatePassword() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Uploading");
        dialog.setMessage("Please Wait...");
        dialog.show();

        user.updatePassword(binding.passText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(MyaccountActivity.this, "passw ok", Toast.LENGTH_SHORT).show();
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

    private void User() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Uploading");
        dialog.setMessage("Please Wait...");
        dialog.show();

        String uId = user.getUid();
        String name = binding.nameText.getText().toString();
        String Sname = binding.sNameText.getText().toString();

        HashMap<String, Object> postUser = new HashMap<>();
        postUser.put("userId", uId);
        postUser.put("userName", name);
        postUser.put("userSname", Sname);

        if (userBoolen) {
            if (!name.equals("") && !Sname.equals("")) {
                firebaseFirestore.collection("Users").document(uId).update(postUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        dialog.dismiss();
                        Toast.makeText(MyaccountActivity.this, "user upload upload", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Snackbar.make(binding.getRoot(), Objects.requireNonNull(e.getLocalizedMessage()), Snackbar.LENGTH_LONG).show();
                    }
                });
            } else {
                dialog.dismiss();
                Snackbar.make(binding.getRoot(), "Enter Your Name And Last Name", Snackbar.LENGTH_LONG).show();
                if (name.equals("")) {
                    binding.nameText.setError("Enter Your Name");
                }
                if (Sname.equals("")) {
                    binding.sNameText.setError("Enter Your Last Name");
                }
            }
        } else {
            if (!name.equals("") && !Sname.equals("")) {
                firebaseFirestore.collection("Users").document(uId).set(postUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        dialog.dismiss();
                        Toast.makeText(MyaccountActivity.this, "user set upload", Toast.LENGTH_SHORT).show();
                        userBoolen = true;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Snackbar.make(binding.getRoot(), Objects.requireNonNull(e.getLocalizedMessage()), Snackbar.LENGTH_LONG).show();
                    }
                });
            } else {
                dialog.dismiss();
                Snackbar.make(binding.getRoot(), "Enter Your Name And Last Name", Snackbar.LENGTH_LONG).show();
                if (name.equals("")) {
                    binding.nameText.setError("Enter Your Name");
                }
                if (Sname.equals("")) {
                    binding.sNameText.setError("Enter Your Last Name");
                }
            }
        }
    }

    private void Image() {
        String imageName = "profilePhoto/" + Objects.requireNonNull(auth.getCurrentUser()).getUid() + ".jpg";
        String uId = user.getUid();

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Uploading");
        dialog.setMessage("Please Wait...");
        dialog.show();

        if (photoBoolen) {
            storageReference.child(imageName).putFile(uriData).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        StorageReference newstorageReference = firebaseStorage.getReference(imageName);
                        newstorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String profileImage = uri.toString();

                                HashMap<String, Object> postData = new HashMap<>();
                                postData.put("userId", uId);
                                postData.put("profilePhoto", profileImage);

                                firebaseFirestore.collection("ProfilePhoto").document(uId).update(postData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        dialog.dismiss();
                                        Toast.makeText(MyaccountActivity.this, "photo update upload", Toast.LENGTH_SHORT).show();
                                        Intent intent = getIntent();
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        finish();
                                        startActivity(intent);
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
            if (uriData != null) {
                storageReference.child(imageName).putFile(uriData).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            StorageReference newstorageReference = firebaseStorage.getReference(imageName);
                            newstorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String profileImage = uri.toString();

                                    HashMap<String, Object> postData = new HashMap<>();
                                    postData.put("userId", uId);
                                    postData.put("profilePhoto", profileImage);

                                    firebaseFirestore.collection("ProfilePhoto").document(uId).set(postData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            dialog.dismiss();
                                            Toast.makeText(MyaccountActivity.this, "photo set upload", Toast.LENGTH_SHORT).show();
                                            Intent intent = getIntent();
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            finish();
                                            startActivity(intent);
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
                Snackbar.make(binding.getRoot(), "Selected Image", Snackbar.LENGTH_LONG).show();
            }
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
                        binding.imageView2.setImageURI(uriData);
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