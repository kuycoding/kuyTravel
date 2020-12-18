package com.kuycoding.kuytravel.ui.traveler.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.adapter.BookingAdapter;
import com.kuycoding.kuytravel.model.Booking;
import com.kuycoding.kuytravel.ui.traveler.hotel.HotelBookingDetailActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class TravelerBookingFragment extends Fragment {
    private RecyclerView recyclerView;
    private BookingAdapter adapter;

    public TravelerBookingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_traveler_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseFirestore fstore = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.rv_booking);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        Query query = fstore.collection("booking").whereEqualTo("uid", fAuth.getUid()).orderBy("status", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Booking> options = new FirestoreRecyclerOptions.Builder<Booking>().setQuery(query, Booking.class).build();

        adapter = new BookingAdapter(options);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
//                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.scrollToPosition(adapter.getItemCount());
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BookingAdapter.OnItemClickListener() {
            @Override
            public void handleDeleteItem(DocumentSnapshot snapshot) {

            }

            @Override
            public void onItemClick(DocumentSnapshot snapshot, int position) {
                if (snapshot.exists()) {
                    Booking booking = snapshot.toObject(Booking.class);
                    Intent intent = new Intent(getContext(), HotelBookingDetailActivity.class);
                    intent.putExtra(HotelBookingDetailActivity.EXTRACT_BOOKING, booking);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
