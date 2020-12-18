package com.kuycoding.kuytravel.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.model.Users;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 101;
    private static final String TAG = "tag";
    private EditText edtNama, edtEmail, edtPassword;
    private ProgressDialog dialog;
    private FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    private String email;
    private String imgUrl;
    private Uri uriProfileImage, imgUri;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        dialog = new ProgressDialog(this);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.register);

        image = findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImg();
            }
        });
        edtNama = findViewById(R.id.edtNama);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createdAccount();
            }
        });
    }

    private void uploadImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select image"), CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                image.setImageBitmap(bitmap);
                uploadProfileImage();
                imgUri = data.getData();
                image.setImageURI(imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadProfileImage() {
        final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");
        if (uriProfileImage != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Uploading File...");
            dialog.show();
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(RegisterActivity.this, "Profile pic upload successful", Toast.LENGTH_SHORT).show();
                            profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imgUrl = uri.toString();
                                    Log.d(TAG, "upload gambar : " + imgUrl);
                                }
                            });
                            dialog.dismiss();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progres = 100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                            dialog.setMessage("Uploaded " + (int) progres + " %");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "" +e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void createdAccount() {
        final String nama = edtNama.getText().toString().trim();
        email = edtEmail.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();
        final String imageUrl = imgUrl;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(nama)) {
            edtNama.setError("Nama tidak boleh kosong!");
        } else if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Email tidak boleh kosong!");
        } else if (!email.matches(emailPattern)) {
            edtEmail.setError("Alamat email salah!");
        } else if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Password tidak boleh kosong!");
        } else if (password.length() < 6) {
            edtPassword.setError("Password harus lebih 6 katakter!");
        } else {
            dialog.setTitle(R.string.buat_akun);
            dialog.setMessage("Tunggu sebentar");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            fAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sendEmailVerification();
                                String userid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                                DocumentReference dbRegister = fStore.collection("traveler").document(userid);
                                Map<String, Object> user = new HashMap<>();
                                user.put("id", userid);
                                user.put("nama", nama);
                                user.put("email", email);
                                user.put("password", password);
                                user.put("imgUrl", imageUrl);
                                user.put("bio", "Bio mu");
                                user.put("role", "2");

                                Users users = new Users(userid, nama, email, password, imageUrl, "bio","1");

                                dbRegister.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccess: ");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: " +e.toString());
                                    }
                                });
                                fAuth.signOut();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            } else {
                                dialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Error ! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void sendEmailVerification() {
        final FirebaseUser firebaseUser = fAuth.getCurrentUser();
        assert firebaseUser != null;
        if (!firebaseUser.isEmailVerified()) {
            firebaseUser.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Verification email sent to " + firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                                Log.d("Verification", "Verification email sent to " + firebaseUser.getEmail());
                            } else {
                                Log.e(TAG, "sendEmailVerification", task.getException());
                                Toast.makeText(getApplicationContext(),
                                        "Failed to send verification email.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
