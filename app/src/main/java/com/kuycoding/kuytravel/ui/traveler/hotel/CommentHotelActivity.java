package com.kuycoding.kuytravel.ui.traveler.hotel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.adapter.CommentAdapter;
import com.kuycoding.kuytravel.model.Comment;
import com.kuycoding.kuytravel.model.Hotel;
import com.kuycoding.kuytravel.model.Travel;
import com.kuycoding.kuytravel.model.Users;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommentHotelActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_COMMENT = "extra_comment";
    private static final String TAG = "tag";
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private EditText editTextComment;
    private Hotel hotel;
    private String namaUser, imgUser, countComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_hotel);
        hotel = getIntent().getParcelableExtra(EXTRA_COMMENT);
        Log.d(TAG, "onCreate: " + hotel.getNama());

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        editTextComment = findViewById(R.id.edit_comment);
        ImageButton btnComment = findViewById(R.id.btnComment);
        btnComment.setOnClickListener(this);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "onMenuItemClick: ");
                return false;
            }
        });
        fStore.collection("hotel").document(hotel.getId()).collection("comment").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int koment = Objects.requireNonNull(task.getResult()).size();
                            countComment = String.valueOf(koment);
                            Log.d("TAG", "onComplete: " + countComment);
                            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
                            getSupportActionBar().setTitle("User reviews ("+countComment+")");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "onFailure: total");
            }
        });

        loadDataUsers();
        loadDataComment();

        DocumentReference userHotel = fStore.collection("usersHotel").document(fAuth.getUid());
        DocumentReference userTravel = fStore.collection("traveler").document(fAuth.getUid());
        if (userHotel != null) {
            userHotel.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Users users = documentSnapshot.toObject(Users.class);
                        namaUser = documentSnapshot.getString("nama");
                        imgUser = users.getImgUrl();
                        Log.d("TAG", "onSuccess nama: " + namaUser);
                        Log.d(TAG, "onSuccess: img" + imgUser);
                    }
                }
            });
        } else if (userTravel != null){
            userTravel.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Users users = documentSnapshot.toObject(Users.class);
                        namaUser = documentSnapshot.getString("nama");
                        imgUser = users.getImgUrl();
                        Log.d("TAG", "onSuccess nama: " + namaUser);
                        Log.d(TAG, "onSuccess: img" + imgUser);
                    }
                }
            });
        }
    }

    private void loadDataComment() {
        recyclerView = findViewById(R.id.rv_comment);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        Query query = fStore.collection("hotel").document(hotel.getId()).collection("comment").orderBy("tgl", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>().setQuery(query, Comment.class).build();

        adapter = new CommentAdapter(options);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.scrollToPosition(adapter.getItemCount());
                //   super.onItemRangeInserted(positionStart, itemCount);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadDataUsers() {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnComment:
                sendComment();
                editTextComment.setText("");
                break;
        }
    }

    private void sendComment() {
        String comment =  editTextComment.getText().toString().trim();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("d-MM-yyyy HH:mm");
        String date = dateFormat.format(Calendar.getInstance().getTime());

        if (TextUtils.isEmpty(comment)) {
            editTextComment.setError("Komentar tidak boleh kosong");
        } else {
            DocumentReference dbComment = fStore.collection("hotel").document(hotel.getId()).collection("comment").document();
            Map<String, Object> komen = new HashMap<>();
            komen.put("id", dbComment.getId());
            komen.put("idUser", fAuth.getUid());
            komen.put("nama", namaUser);
            komen.put("comment", comment);
            komen.put("tgl", date);
            komen.put("imgUrl", imgUser);

            dbComment.set(komen).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("TAG", "onSuccess: Set comment ");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("TAG", "onFailure: ");
                }
            });
        }
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

    @Override
    protected void onPause() {
        super.onPause();
        adapter.stopListening();
    }

}
