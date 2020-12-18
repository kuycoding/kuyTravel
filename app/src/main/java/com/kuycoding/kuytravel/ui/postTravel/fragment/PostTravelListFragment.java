package com.kuycoding.kuytravel.ui.postTravel.fragment;

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
import com.kuycoding.kuytravel.adapter.TravelAdapter;
import com.kuycoding.kuytravel.model.Travel;
import com.kuycoding.kuytravel.ui.admin.travel.AdminAddTravelActivity;
import com.kuycoding.kuytravel.ui.admin.travel.DetailTravelActivity;
import com.kuycoding.kuytravel.ui.postTravel.PostTravelActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostTravelListFragment extends Fragment {
    private RecyclerView recyclerView;
    private TravelAdapter adapter;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;

    public PostTravelListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_travel_posting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        FloatingActionButton fab = view.findViewById(R.id.fab_add_travel);
        fab.setOnClickListener(v -> startActivity(new Intent(getContext(), AdminAddTravelActivity.class)));
        loadDataTravel();
        getActivity().setTitle("Travel");
    }

    private void loadDataTravel() {
        recyclerView = Objects.requireNonNull(getView()).findViewById(R.id.rv_travel);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        CollectionReference travels = fStore.collection("travel");
        Query query = travels.whereEqualTo("uid", fAuth.getUid());
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
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    Toast.makeText(getContext(), "Hapus", Toast.LENGTH_SHORT).show();
                    TravelAdapter.ViewHolder viewHoder = (TravelAdapter.ViewHolder) viewHolder;
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

                    dbCount.update(count).addOnSuccessListener(aVoid -> Log.d("TAG", "onSuccess: " + total)).addOnFailureListener(e -> Log.d("TAG", "onFailure: "));

                    Intent intent = new Intent(getContext(), DetailTravelActivity.class);
                    intent.putExtra(DetailTravelActivity.EXTRACT_TRAVEL, travel);
                    startActivity(intent);
                } else {
                    Log.d("TAG", "onItemClick: null ");
                }
            }

            @Override
            public void handleDeleteItem(DocumentSnapshot snapshot) {
                final DocumentReference documentReference = snapshot.getReference();
                final Travel travel = snapshot.toObject(Travel.class);

                documentReference.delete()
                        .addOnSuccessListener(aVoid -> Log.d("TAG", "onSuccess: item deleted"));
                Snackbar.make(recyclerView, "Item delete", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> documentReference.set(Objects.requireNonNull(travel))).show();
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
