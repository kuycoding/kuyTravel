package com.kuycoding.kuytravel.ui.traveler.hotel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.adapter.HotelAdapter;
import com.kuycoding.kuytravel.model.Hotel;
import com.kuycoding.kuytravel.ui.admin.hotel.DetailHotelActivity;

import java.util.HashMap;
import java.util.Map;

public class TravelerHotelListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HotelAdapter hotelAdapter;
    private FirebaseFirestore fStore;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveler_hotel_list);
        fStore = FirebaseFirestore.getInstance();
        searchView = findViewById(R.id.searchview);

        RecyclerViewHotel();
    }

    private void RecyclerViewHotel() {
        recyclerView = findViewById(R.id.rv_hotel);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        Query query = fStore.collection("hotel").orderBy("rating", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Hotel> options = new FirestoreRecyclerOptions.Builder<Hotel>().setQuery(query, Hotel.class).build();
        hotelAdapter = new HotelAdapter(options);
        hotelAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                //super.onItemRangeChanged(positionStart, itemCount);
                recyclerView.scrollToPosition(hotelAdapter.getItemCount());
            }
        });
        recyclerView.setAdapter(hotelAdapter);
        hotelAdapter.setOnItemClickListener(new HotelAdapter.OnItemClick() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                if (documentSnapshot.exists()) {
                    Hotel hotel = documentSnapshot.toObject(Hotel.class);
                    DocumentReference db = fStore.collection("hotel").document(hotel.getId());
                    final int total = (hotel.getView()+ 1);
                    // String totalView = String.valueOf(total);

                    Map<String, Object> count = new HashMap<>();
                    count.put("view", total);

                    db.update(count).addOnSuccessListener(aVoid -> Log.d("TAG", "onSuccess: " + total)).addOnFailureListener(e -> Log.d("TAG", "onFailure: "));
                    Intent intent = new Intent(TravelerHotelListActivity.this, DetailHotelActivity.class);
                    intent.putExtra(DetailHotelActivity.EXTRACT_HOTEL, hotel);
                    startActivity(intent);
                }
            }

            @Override
            public void handlerDeleteItem(DocumentSnapshot snapshot) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        hotelAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hotelAdapter.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hotelAdapter.startListening();
    }
}
