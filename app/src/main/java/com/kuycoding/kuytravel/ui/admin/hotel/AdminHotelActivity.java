package com.kuycoding.kuytravel.ui.admin.hotel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.adapter.HotelAdapter;
import com.kuycoding.kuytravel.adapter.TravelAdapter;
import com.kuycoding.kuytravel.model.Hotel;
import com.kuycoding.kuytravel.model.Travel;
import com.kuycoding.kuytravel.ui.admin.AdminHomeActivity;

public class AdminHotelActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private HotelAdapter adapter;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_hotel);

        fStore = FirebaseFirestore.getInstance();

        fab = findViewById(R.id.fab_add_hotel);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHotelActivity.this, AddHotelActivity.class));
            }
        });
        loadData();
    }

    private void loadData() {
        recyclerView = findViewById(R.id.rv_hotel);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        Query query = fStore.collection("hotel").orderBy("nama", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Hotel> options = new FirestoreRecyclerOptions.Builder<Hotel>().setQuery(query, Hotel.class).build();

        adapter = new HotelAdapter(options);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.scrollToPosition(adapter.getItemCount());
                //   super.onItemRangeInserted(positionStart, itemCount);
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new HotelAdapter.OnItemClick() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

            }

            @Override
            public void handlerDeleteItem(DocumentSnapshot snapshot) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.stopListening();
    }
}
