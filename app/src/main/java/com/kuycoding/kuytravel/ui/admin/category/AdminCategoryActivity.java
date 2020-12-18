package com.kuycoding.kuytravel.ui.admin.category;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.adapter.CategoryAdapter;
import com.kuycoding.kuytravel.model.Category;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AdminCategoryActivity extends AppCompatActivity {
    private static final String TAG = "tag";
    private static final int CHOOSE_IMAGE = 100;
    private FirebaseFirestore fStore;
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private ImageView imgCategory;
    private EditText edtCateory;
    private String profileImageUrl;
    private Uri uriProfileImage, imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        fStore = FirebaseFirestore.getInstance();
        edtCateory = findViewById(R.id.edtCategory);
        imgCategory = findViewById(R.id.imgCategory);
        imgCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImgCategory();
            }
        });
        Button btnAdd = findViewById(R.id.btnAdd);
        setUpRc();
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory();
                edtCateory.setText("");
            }
        });
    }

    private void uploadImgCategory() {
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
                imgCategory.setImageBitmap(bitmap);
                uploadProfileImage();
                imgUri = data.getData();
                imgCategory.setImageURI(imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadProfileImage() {
        final StorageReference imgUpload = FirebaseStorage.getInstance().getReference("ImageCategory/" + System.currentTimeMillis() + ".jpg");
        if (uriProfileImage != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Uploading File...");
            dialog.show();
            imgUpload.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(AdminCategoryActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
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

    private void addCategory() {
        final String nama = edtCateory.getText().toString().trim();
        final String imgUrl = profileImageUrl;
        if (TextUtils.isEmpty(nama)) {
            edtCateory.setError("Harus di isi");
        } else {
            DocumentReference documentSnapshot = fStore.collection("category").document();
            Map<String, Object> category = new HashMap<>();
            category.put("id", documentSnapshot.getId());
            category.put("category", nama);
            category.put("image", imgUrl);

            documentSnapshot.set(category).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: ");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: "+ e.toString());
                }
            });

            finish();
            startActivity(getIntent());
        }
    }

    private void setUpRc() {
        recyclerView = findViewById(R.id.rv_category);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        Query query = FirebaseFirestore.getInstance().collection("category").orderBy("category",Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Category> options = new FirestoreRecyclerOptions.Builder<Category>().setQuery(query, Category.class).build();
        adapter = new CategoryAdapter(options);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.scrollToPosition(adapter.getItemCount());
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void handleDeleteItem(DocumentSnapshot snapshot) {

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
