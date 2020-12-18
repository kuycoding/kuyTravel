package com.kuycoding.kuytravel.ui.admin.travel;

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
import android.view.Window;
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
import com.kuycoding.kuytravel.adapter.TravelAdapter;
import com.kuycoding.kuytravel.model.Comment;
import com.kuycoding.kuytravel.model.Travel;
import com.kuycoding.kuytravel.model.Users;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommentTravelActivity extends AppCompatActivity {
    public static final String EXTRA_COMMENT = "extra_comment";
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private EditText editTextComment;
    private Travel travel;
    private String namaUser, imgUser, countComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_travel);
        travel = getIntent().getParcelableExtra(EXTRA_COMMENT);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        fStore.collection("travel").document(travel.getId()).collection("comment").get()
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

        editTextComment = findViewById(R.id.edit_comment);
        ImageButton btnComment = findViewById(R.id.btnComment);
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment();
                editTextComment.setText("");
            }
        });
        loadDataUsers();
        loadDataComment();
    }

    private void loadDataUsers() {
        String uid = fAuth.getUid();
        DocumentReference dUserTravel = fStore.collection("users").document(uid);
    }

    private void loadDataComment() {
        recyclerView = findViewById(R.id.rv_comment);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        Query query = fStore.collection("travel").document(travel.getId()).collection("comment").orderBy("tgl", Query.Direction.ASCENDING);
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

    private void sendComment() {
        String comment =  editTextComment.getText().toString().trim();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("d-MM-yyyy HH:mm");
        String date = dateFormat.format(Calendar.getInstance().getTime());

        if (TextUtils.isEmpty(comment)) {
            editTextComment.setError("Komentar tidak boleh kosong");
        } else {
            DocumentReference dbCommentUser = fStore.collection("admin").document(Objects.requireNonNull(fAuth.getUid())).collection("comment").document();
            DocumentReference dbComment = fStore.collection("travel").document(travel.getId()).collection("comment").document();
            Map<String, Object> komen = new HashMap<>();
            komen.put("id", dbComment.getId());
            komen.put("idUser", fAuth.getUid());
            komen.put("nama", namaUser);
            komen.put("comment", comment);
            komen.put("tgl", date);
            komen.put("image", imgUser);

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

            dbCommentUser.set(komen).addOnSuccessListener(new OnSuccessListener<Void>() {
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
}
