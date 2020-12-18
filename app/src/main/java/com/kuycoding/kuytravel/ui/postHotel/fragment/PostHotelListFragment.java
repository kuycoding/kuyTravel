package com.kuycoding.kuytravel.ui.postHotel.fragment;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.adapter.HotelAdapter;
import com.kuycoding.kuytravel.model.Hotel;
import com.kuycoding.kuytravel.ui.admin.hotel.AddHotelActivity;
import com.kuycoding.kuytravel.ui.admin.hotel.DetailHotelActivity;
import com.kuycoding.kuytravel.ui.postHotel.PostHotelMainActivity;

import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostHotelListFragment extends Fragment {
    private RecyclerView recyclerView;
    private HotelAdapter adapter;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;

    public PostHotelListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_hotel_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        FloatingActionButton fab = view.findViewById(R.id.fab_add_hotel);
        fab.setOnClickListener(v -> startActivity(new Intent(getContext(), AddHotelActivity.class)));
        loadDataHotel();
    }

    private void loadDataHotel() {
        recyclerView = Objects.requireNonNull(getView()).findViewById(R.id.rv_hotel);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        CollectionReference hotels = fStore.collection("hotel");
        Query query = hotels.whereEqualTo("uid", fAuth.getUid());
        FirestoreRecyclerOptions<Hotel> options = new FirestoreRecyclerOptions.Builder<Hotel>().setQuery(query, Hotel.class).build();

        adapter = new HotelAdapter(options);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.scrollToPosition(adapter.getItemCount());
                //super.onItemRangeInserted(positionStart, itemCount);
            }
        });
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    Toast.makeText(getContext(), "Hapus", Toast.LENGTH_SHORT).show();
                    HotelAdapter.ViewHolder viewHoder = (HotelAdapter.ViewHolder) viewHolder;
                    viewHoder.deleteItem();
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.red))
                        .addActionIcon(R.drawable.ic_delete)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new HotelAdapter.OnItemClick() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                if (documentSnapshot.exists()) {
                    Hotel hotel = documentSnapshot.toObject(Hotel.class);
                    Intent i = new Intent(getContext(), DetailHotelActivity.class);
                    i.putExtra(DetailHotelActivity.EXTRACT_HOTEL, hotel);
                    startActivity(i);
                }
            }

            @Override
            public void handlerDeleteItem(DocumentSnapshot snapshot) {
                final DocumentReference documentReference = snapshot.getReference();
                final Hotel hotel = snapshot.toObject(Hotel.class);

                documentReference.delete()
                        .addOnSuccessListener(aVoid -> Log.d("TAG", "onSuccess: item deleted"));
                Snackbar.make(recyclerView, "Item delete", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> documentReference.set(Objects.requireNonNull(hotel))).show();
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

    @Override
    public void onResume() {
        super.onResume();

    }
}
