package com.kuycoding.kuytravel.ui.postHotel.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.model.Users;
import com.kuycoding.kuytravel.ui.postHotel.PostHotelMainActivity;
import com.kuycoding.kuytravel.ui.postHotel.PostHotelProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostHotelHomeFragment extends Fragment {
    private static final String TAG = "PostHotelMainFragment";
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;

    private ImageView imgProfile;
    private TextView tvNama;
    private TextView tvJmlHotel, tvJmlBooking;

    public PostHotelHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_hotel_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        tvNama = view.findViewById(R.id.tv_nama);
        imgProfile = view.findViewById(R.id.imgProfile);
        tvJmlBooking = view.findViewById(R.id.tv_total_booking);
        tvJmlHotel = view.findViewById(R.id.tv_total_hotel);
        loadData();
        loadTotal();
    }

    @SuppressLint("SetTextI18n")
    private void loadTotal() {
        //total hotel
        String uid = fAuth.getUid();
        Query queryTotalHotel = fStore.collection("hotel").whereEqualTo("uid", uid);
        queryTotalHotel.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                int tot = queryDocumentSnapshots.size();
                Log.d(TAG, "onSuccess total hotel: " + tot);
                tvJmlHotel.setText(tot + " Hotel");
            } else {
                tvJmlHotel.setText("0");
            }
        });

        Query queryTotalBooking = fStore.collection("booking").whereEqualTo("uid", uid);
        queryTotalBooking.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                int tot = queryDocumentSnapshots.size();
                tvJmlBooking.setText(tot +" Booking");
            } else {
                tvJmlBooking.setText("0");
            }
        });
    }

    private void loadData() {
        fStore.collection("usersHotel").document(Objects.requireNonNull(fAuth.getUid())).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Users users = documentSnapshot.toObject(Users.class);
                        assert users != null;
                        tvNama.setText(users.getNama());
                        Picasso.get()
                                .load(users.getImgUrl())
                                .placeholder(R.drawable.person_upload)
                                .error(R.drawable.person_upload)
                                .into(imgProfile);
                        Log.d(TAG, "onSuccess: " + users.getNama());
                        imgProfile.setOnClickListener(v -> {
                            Intent intent = new Intent(getContext(), PostHotelProfileActivity.class);
                            intent.putExtra(PostHotelProfileActivity.EXTRACT_USER, users);
                            startActivity(intent);
                        });
                    }
                }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.toString()));
    }
    @Override
    public void onResume() {
        super.onResume();

    }
}
