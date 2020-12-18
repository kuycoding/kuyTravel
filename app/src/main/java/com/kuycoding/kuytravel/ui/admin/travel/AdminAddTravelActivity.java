package com.kuycoding.kuytravel.ui.admin.travel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kuycoding.kuytravel.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminAddTravelActivity extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 100;
    private static final String TAG = "tag";
    private EditText edtNama, edtAlamat, edtKeterangan, edtLat, edtLong, edtHarga;
    private Spinner spinner_category;
    private ImageView imgTravel;
    private ProgressDialog dialog;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String profileImageUrl;
    private Uri uriProfileImage, imgUri;
    private String subj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_travel);
        dialog = new ProgressDialog(this);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        edtNama = findViewById(R.id.edtNama);
        edtAlamat   = findViewById(R.id.edtAlamat);
        edtKeterangan = findViewById(R.id.edtKeterangan);
        edtLat  = findViewById(R.id.edtLat);
        edtLong = findViewById(R.id.edtLong);
        edtHarga = findViewById(R.id.edtHarga);
        spinner_category = findViewById(R.id.spinner_category);
        imgTravel = findViewById(R.id.imgUpload);
        imgTravel.setOnClickListener(v -> uploadImage());
        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postTravel();
                clearText();
            }
        });

        CollectionReference user = fStore.collection("usersTravel");
        Query query = user.whereEqualTo("id", fAuth.getUid()).whereEqualTo("role", "1");
        if (query != null) {
            CollectionReference reference = fStore.collection("category");
            final List<String> subjek = new ArrayList<>();
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, subjek);
            spinner_category.setAdapter(arrayAdapter);

            Query cate = reference.orderBy("category");
            cate.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        subj = queryDocumentSnapshot.getString("category");
                        subjek.add(subj);
                        Log.d(TAG, "onComplete: " + subj);
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            });
        } else {
            Log.d(TAG, "tidak sukses");
        }
/*
        CollectionReference reference = fStore.collection("category");
        final List<String> subjek = new ArrayList<>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, subjek);
        spinner_category.setAdapter(arrayAdapter);
        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    subj = queryDocumentSnapshot.getString("category").trim();
                    subjek.add(subj);
                    Log.d(TAG, "onComplete: " + subj);
                }
                arrayAdapter.notifyDataSetChanged();
            }
        });*/
    }

    private void clearText() {
        edtNama.setText("");
        edtAlamat.setText("");
        edtHarga.setText("");
        edtKeterangan.setText("");
        edtLat.setText("");
        edtLong.setText("");
        imgTravel.setImageDrawable(getDrawable(R.drawable.ic_image_placeholder));
    }

    private void uploadImage() {
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
                imgTravel.setImageBitmap(bitmap);
                uploadProfileImage();
                imgUri = data.getData();
                imgTravel.setImageURI(imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadProfileImage() {
        final StorageReference imgUpload = FirebaseStorage.getInstance().getReference("ImageTravel/" + System.currentTimeMillis() + ".jpg");
        if (uriProfileImage != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Uploading File...");
            dialog.show();
            imgUpload.putFile(uriProfileImage)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(AdminAddTravelActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                        imgUpload.getDownloadUrl().addOnSuccessListener(uri -> profileImageUrl = uri.toString());
                        dialog.dismiss();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        dialog.setMessage("Uploaded " + (int) progress + " %");
                    })
                    .addOnFailureListener(e -> Log.d(TAG, "onFailure: "));
        }
    }

    private void postTravel() {
        String nama = edtNama.getText().toString().trim();
        String alamat = edtAlamat.getText().toString().trim();
        String keterangan = edtKeterangan.getText().toString().trim();
        String latitude = edtLat.getText().toString().trim();
        String longitude = edtLong.getText().toString().trim();
        String harga = edtHarga.getText().toString().trim();
        String spinner = subj;
        String imageUrl = profileImageUrl;
        String uid = fAuth.getUid();

        if (TextUtils.isEmpty(nama)) {
            edtNama.setError("Tidak boleh kosong");
        } else if (TextUtils.isEmpty(alamat)) {
            edtAlamat.setError("Tidak boleh kosong");
        } else if (TextUtils.isEmpty(keterangan)) {
            edtKeterangan.setError("Tidak boleh kosong");
        } else if (TextUtils.isEmpty(latitude)) {
            edtLat.setError("Tidak boleh kosong");
        } else if (TextUtils.isEmpty(longitude)) {
            edtLong.setError("Tidak boleh kosong");
        } else if (spinner.equals("Pilih salah satu")) {
        } else {
            dialog.setMessage(getString(R.string.please_wait));
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();

            DocumentReference postTravel = fStore.collection("travel").document();
            String idPost = postTravel.getId();
            assert uid != null;
            DocumentReference dbFav = fStore.collection("travel").document(idPost).collection("favorite").document(uid);
            Map<String, Object> favorite = new HashMap<>();
            favorite.put("favorite", false);
            dbFav.set(favorite).addOnSuccessListener(aVoid -> {
                dialog.dismiss();
                Log.d(TAG, "onSuccess: ");
                startActivity(new Intent(AdminAddTravelActivity.this, AdminTravelActivity.class));
            });
            Map<String, Object> post = new HashMap<>();
            post.put("id", postTravel.getId());
            post.put("uid", uid);
            post.put("nama", nama);
            post.put("alamat", alamat);
            post.put("keterangan", keterangan);
            post.put("lat", latitude);
            post.put("lng", longitude);
            post.put("harga", harga);
            post.put("category", spinner);
            post.put("image", imageUrl);
            post.put("view", 0);
            post.put("like", 0);

            postTravel.set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: ");
                    dialog.dismiss();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: "+e.toString());
                    dialog.dismiss();
                    finish();
                }
            });
        }
    }
}
