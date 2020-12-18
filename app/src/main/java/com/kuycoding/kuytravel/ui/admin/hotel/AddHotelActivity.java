package com.kuycoding.kuytravel.ui.admin.hotel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kuycoding.kuytravel.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddHotelActivity extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 100;
    private static final String TAG = "tag";
    private EditText edtNama, edtAlamat, edtKeterangan, edtLat, edtLong, edtHarga, edtRating, edtDetailKamar;
    private CheckBox checkBox_tv, checkBox_ac, checkBox_wifi, checkBox_kolam, checkBox_parkir, checkBox_resepsionis, checkBox_resto, checkBox_spa;
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
        setContentView(R.layout.activity_add_hotel);

        dialog = new ProgressDialog(this);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        edtNama = findViewById(R.id.edtNama);
        edtAlamat = findViewById(R.id.edtAlamat);
        edtKeterangan = findViewById(R.id.edtKeterangan);
        edtLat = findViewById(R.id.edtLat);
        edtLong = findViewById(R.id.edtLong);
        edtHarga = findViewById(R.id.edtHarga);
        edtRating = findViewById(R.id.edtRating);
        edtDetailKamar = findViewById(R.id.edtDetailKamar);
        imgTravel = findViewById(R.id.imgUpload);
        imgTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        checkBox_ac = findViewById(R.id.checkbox_ac);
        checkBox_kolam = findViewById(R.id.checkbox_kolam);
        checkBox_parkir = findViewById(R.id.checkbox_parkir);
        checkBox_resepsionis = findViewById(R.id.checkbox_resepsionis);
        checkBox_resto = findViewById(R.id.checkbox_resto);
        checkBox_spa = findViewById(R.id.checkbox_spa);
        checkBox_tv = findViewById(R.id.checkbox_tv);
        checkBox_wifi = findViewById(R.id.checkbox_wifi);
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
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
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(AddHotelActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                            imgUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    profileImageUrl = uri.toString();
                                }
                            });
                            dialog.dismiss();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            dialog.setMessage("Uploaded " + (int) progress + " %");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: ");
                        }
                    });
        }
    }

    private void saveData() {
        String nama = edtNama.getText().toString().trim();
        String alamat = edtAlamat.getText().toString().trim();
        String keterangan = edtKeterangan.getText().toString().trim();
        String lat = edtLat.getText().toString().trim();
        String lng = edtLong.getText().toString().trim();
        String harga = edtHarga.getText().toString().trim();
        String rate = edtRating.getText().toString().trim();
        String detailKamar = edtDetailKamar.getText().toString().trim();

        String imageUrl = profileImageUrl;
        String uid = fAuth.getUid();

        if (TextUtils.isEmpty(nama)) {
            edtNama.setError("Tidak boleh kosong");
        } else if (TextUtils.isEmpty(alamat)) {
            edtAlamat.setError("Tidak boleh kosong");
        } else if (TextUtils.isEmpty(keterangan)) {
            edtKeterangan.setError("Tidak boleh kosong");
        } else if (TextUtils.isEmpty(lat)) {
            edtLat.setError("Tidak boleh kosong");
        } else if (TextUtils.isEmpty(lng)) {
            edtLong.setError("Tidak boleh kosong");
        } else if (TextUtils.isEmpty(rate)) {
            edtRating.setError("Tidak boleh kosong");
        } else if (TextUtils.isEmpty(detailKamar)) {
            edtDetailKamar.setError("Tidak boleh kosong");
        } else {
            dialog.setMessage(getString(R.string.please_wait));
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();

            DocumentReference documentReference = fStore.collection("hotel").document();
            String id = documentReference.getId();

            Map<String, Object> hotel = new HashMap<>();
            hotel.put("id", id);
            hotel.put("uid", uid);
            hotel.put("nama", nama);
            hotel.put("alamat", alamat);
            hotel.put("keterangan", keterangan);
            hotel.put("lat", lat);
            hotel.put("lng", lng);
            hotel.put("harga", harga);
            hotel.put("rating", rate);
            hotel.put("view", 0);
            hotel.put("detailKamar", detailKamar);
            hotel.put("image", imageUrl);
            if (checkBox_ac.isChecked()) {
                checkBox_ac.setChecked(true);
                hotel.put("ac", true);
            } else if (!checkBox_ac.isChecked()) {
                checkBox_ac.setChecked(false);
                hotel.put("ac", false);
            }
            if (checkBox_kolam.isChecked()) {
                checkBox_kolam.setChecked(true);
                hotel.put("kolam", true);
            } else if (!checkBox_kolam.isChecked()) {
                checkBox_kolam.setChecked(false);
                hotel.put("kolam", false);
            }
            if (checkBox_parkir.isChecked()) {
                checkBox_parkir.setChecked(true);
                hotel.put("parkir", true);
            } else if (!checkBox_parkir.isChecked()) {
                checkBox_parkir.setChecked(false);
                hotel.put("parkir", false);
            }
            if (checkBox_resepsionis.isChecked()) {
                checkBox_resepsionis.setChecked(true);
                hotel.put("resepsionis", true);
            } else if (!checkBox_resepsionis.isChecked()) {
                checkBox_resepsionis.setChecked(false);
                hotel.put("resepsionis", false);
            }
            if (checkBox_resto.isChecked()) {
                checkBox_resto.setChecked(true);
                hotel.put("resto", true);
            } else if (!checkBox_resto.isChecked()) {
                checkBox_resto.setChecked(false);
                hotel.put("resto", false);
            }
            if (checkBox_spa.isChecked()) {
                checkBox_spa.setChecked(true);
                hotel.put("spa", true);
            } else if (!checkBox_spa.isChecked()) {
                checkBox_spa.setChecked(false);
                hotel.put("spa", false);
            }
            if (checkBox_tv.isChecked()) {
                checkBox_tv.setChecked(true);
                hotel.put("tv", true);
            } else if (!checkBox_tv.isChecked()) {
                checkBox_tv.setChecked(false);
                hotel.put("tv", false);
            }
            if (checkBox_wifi.isChecked()) {
                checkBox_wifi.setChecked(true);
                hotel.put("wifi", true);
            } else if (!checkBox_wifi.isChecked()) {
                checkBox_wifi.setChecked(false);
                hotel.put("wifi", false);
            }

            documentReference.set(hotel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dialog.dismiss();
                    Log.d(TAG, "onSuccess: ");
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Log.d(TAG, "onFailure: ");
                    startActivity(new Intent(AddHotelActivity.this, AdminHotelActivity.class));
                    finish();
                }
            });
        }
    }
}
