package com.kuycoding.kuytravel.ui.traveler.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.adapter.CategoryCircleAdapter;
import com.kuycoding.kuytravel.adapter.HotelAdapter;
import com.kuycoding.kuytravel.adapter.TravelHorizontalAdapter;
import com.kuycoding.kuytravel.helper.LinePagerIndicatorDecoration;
import com.kuycoding.kuytravel.model.Category;
import com.kuycoding.kuytravel.model.Hotel;
import com.kuycoding.kuytravel.model.Travel;
import com.kuycoding.kuytravel.model.Users;
import com.kuycoding.kuytravel.ui.admin.hotel.DetailHotelActivity;
import com.kuycoding.kuytravel.ui.traveler.category.TravelDetailCategoryActivity;
import com.kuycoding.kuytravel.ui.traveler.hotel.TravelerHotelListActivity;
import com.kuycoding.kuytravel.ui.traveler.travel.TravelDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class TravelerHomeFragment extends Fragment {
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView1;
    private CategoryCircleAdapter adapter1;
    private TravelHorizontalAdapter adapter;
    private HotelAdapter hotelAdapter;
    private ImageView imgProfile;
    private TextView tvMore;
    private Hotel hotel;
    private Users users;

    public TravelerHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_traveler_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        imgProfile = view.findViewById(R.id.imgProfile);
        tvMore = view.findViewById(R.id.tv_more);
        tvMore.setOnClickListener(v -> startActivity(new Intent(getContext(), TravelerHotelListActivity.class)));

        detailData();
        recyclerViewTravel();
        recyclerViewCategory();
        recyclerViewHotel();
    }

    private void recyclerViewHotel() {
        recyclerView = getView().findViewById(R.id.rv_hotel);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        Query query = fStore.collection("hotel").orderBy("rating", Query.Direction.DESCENDING).orderBy("view").limit(2);
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

                    hotel = documentSnapshot.toObject(Hotel.class);
                    DocumentReference db = fStore.collection("hotel").document(hotel.getId());
                    final int total = (hotel.getView()+ 1);
                    // String totalView = String.valueOf(total);

                    Map<String, Object> count = new HashMap<>();
                    count.put("view", total);

                    db.update(count).addOnSuccessListener(aVoid -> Log.d("TAG", "onSuccess: " + total)).addOnFailureListener(e -> Log.d("TAG", "onFailure: "));

                    Intent intent = new Intent(getContext(), DetailHotelActivity.class);
                    intent.putExtra(DetailHotelActivity.EXTRACT_HOTEL, hotel);
                    startActivity(intent);

                }
            }

            @Override
            public void handlerDeleteItem(DocumentSnapshot snapshot) {

            }
        });
    }

    private void recyclerViewCategory() {
        recyclerView1 = getView().findViewById(R.id.rv_category);
        recyclerView1.setHasFixedSize(true);
        //  recyclerView1.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, true));
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        recyclerView1.setLayoutManager(manager);
        manager.setStackFromEnd(false);

        Query query = fStore.collection("category").orderBy("category", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Category> options = new FirestoreRecyclerOptions.Builder<Category>().setQuery(query, Category.class).build();
        adapter1 = new CategoryCircleAdapter(options);
        adapter1.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                //   super.onItemRangeInserted(positionStart, itemCount);
                recyclerView1.scrollToPosition(adapter1.getItemCount());
            }
        });
        recyclerView1.setAdapter(adapter1);
        adapter1.onItemClickListener(new CategoryCircleAdapter.OnClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot snapshot, int position) {
                if (snapshot.exists()) {
                    Category category = snapshot.toObject(Category.class);
                    DocumentReference documentReference = fStore.collection("category").document(category.getId());
                    Intent intent = new Intent(getContext(), TravelDetailCategoryActivity.class);
                    intent.putExtra(TravelDetailCategoryActivity.EXTRACT_CATEGORY, category);
                    startActivity(intent);
                }
            }

            @Override
            public void handlerDeleteItem(DocumentSnapshot snapshot) {

            }
        });
    }

    private void recyclerViewTravel() {
        recyclerView = getView().findViewById(R.id.rv_travel_horizontal);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new LinePagerIndicatorDecoration());

        Query query = fStore.collection("travel").orderBy("view", Query.Direction.DESCENDING).limit(5);
        FirestoreRecyclerOptions<Travel> options = new FirestoreRecyclerOptions.Builder<Travel>().setQuery(query, Travel.class).build();
        adapter = new TravelHorizontalAdapter(options);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                 //super.onItemRangeInserted(positionStart, itemCount);
                 recyclerView.scrollToPosition(adapter.getItemCount());
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new TravelHorizontalAdapter.OnItemClickListener() {
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

                    Intent intent = new Intent(getContext(), TravelDetailActivity.class);
                    intent.putExtra(TravelDetailActivity.EXTRACT_TRAVEL, travel);
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

    private void detailData() {
        fStore.collection("traveler").document(fAuth.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Users users = documentSnapshot.toObject(Users.class);
                        if (documentSnapshot.exists()) {
                            Picasso.get()
                                    .load(users.getImgUrl())
                                    .error(R.drawable.placeholder)
                                    .placeholder(R.drawable.placeholder)
                                    .fit()
                                    .centerCrop()
                                    .noFade()
                                    .into(imgProfile);
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        adapter1.startListening();
        hotelAdapter.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.startListening();
        adapter1.startListening();
        hotelAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter1 != null) {
            adapter1.stopListening();
        } else if (adapter != null) {
            adapter.stopListening();
        } else if (hotelAdapter != null) {
            hotelAdapter.stopListening();
        }
    }
}
