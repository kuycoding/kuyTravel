package com.kuycoding.kuytravel.ui.admin.travel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.adapter.TravelAdapter;
import com.kuycoding.kuytravel.model.Category;
import com.kuycoding.kuytravel.model.Travel;

import java.util.HashMap;
import java.util.Map;

public class AdminTravelActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private RecyclerView recyclerView;
    private TravelAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_travel);
        fStore = FirebaseFirestore.getInstance();
        fAuth  = FirebaseAuth.getInstance();

        fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> startActivity(new Intent(AdminTravelActivity.this, AdminAddTravelActivity.class)));

        loadTravel();
    }

    private void loadTravel() {
        recyclerView = findViewById(R.id.rv_travel);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        Query query = fStore.collection("travel").orderBy("nama", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Travel> options = new FirestoreRecyclerOptions.Builder<Travel>().setQuery(query, Travel.class).build();

        adapter = new TravelAdapter(options);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.scrollToPosition(adapter.getItemCount());
                //   super.onItemRangeInserted(positionStart, itemCount);
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new TravelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                if (documentSnapshot.exists()) {
                    Travel travel = documentSnapshot.toObject(Travel.class);

                    assert travel != null;
                    DocumentReference dbCount = fStore.collection("travel").document(travel.getId());

                    final int total = (travel.getView()+ 1);
                   // String totalView = String.valueOf(total);

                    Map<String, Object> count = new HashMap<>();
                    count.put("view", total);

                    dbCount.update(count).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG", "onSuccess: " + total);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", "onFailure: ");
                        }
                    });

                    Intent intent = new Intent(AdminTravelActivity.this, DetailTravelActivity.class);
                    intent.putExtra(DetailTravelActivity.EXTRACT_TRAVEL, travel);
                    startActivity(intent);
                } else {
                    Log.d("TAG", "onItemClick: null ");
                }
            }

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
        adapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.startListening();
    }
}
