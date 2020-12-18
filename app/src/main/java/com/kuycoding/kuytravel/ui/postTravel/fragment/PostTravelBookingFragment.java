package com.kuycoding.kuytravel.ui.postTravel.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.adapter.BookingAdapter;
import com.kuycoding.kuytravel.model.Booking;
import com.kuycoding.kuytravel.ui.postTravel.PostTravelActivity;
import com.kuycoding.kuytravel.ui.postTravel.PostTravelDetailBookingActivity;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostTravelBookingFragment extends Fragment {
    private RecyclerView recyclerView;
    private BookingAdapter adapter;
    private FirebaseFirestore fstore;

    public PostTravelBookingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_travel_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Booking");
        fstore = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        FloatingActionButton fabScan = view.findViewById(R.id.fab_scan);
        fabScan.setOnClickListener(v -> sacnForFragement());

        recyclerView = view.findViewById(R.id.rv_booking);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        Query query = fstore.collection("booking").whereEqualTo("postuid", fAuth.getUid()).orderBy("status", Query.Direction.DESCENDING);
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
                    Intent intent = new Intent(getContext(), PostTravelDetailBookingActivity.class);
                    intent.putExtra(PostTravelDetailBookingActivity.EXTRACT_BOOKING, booking);
                    startActivity(intent);
                }
            }
        });
    }

    private void sacnForFragement() {
        IntentIntegrator.forSupportFragment(this).initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("TAG", "onActivityResult: " + result.getContents());
                showData(result);
            }
        } else {
            Toast.makeText(getContext(), "ID Tidak ditemukan", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showData(IntentResult result) {
        String uid = result.getContents();
        fstore.collection("booking").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Booking booking = documentSnapshot.toObject(Booking.class);
                        Intent intent = new Intent(getContext(), PostTravelDetailBookingActivity.class);
                        intent.putExtra(PostTravelDetailBookingActivity.EXTRACT_BOOKING, booking);
                        startActivity(intent);
                        assert booking != null;
                        Toast.makeText(getContext(), "" + booking.getNamaHotel(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Log.d("TAG", "onFailure: " + e.toString()));
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

    @Override
    public void onResume() {
        super.onResume();
    }
}
