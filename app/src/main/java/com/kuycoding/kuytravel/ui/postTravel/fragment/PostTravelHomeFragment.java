package com.kuycoding.kuytravel.ui.postTravel.fragment;

import android.annotation.SuppressLint;
import android.app.ActionBar;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionBarContainer;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.model.Users;
import com.kuycoding.kuytravel.ui.postTravel.PostTravelActivity;
import com.kuycoding.kuytravel.ui.postTravel.PostTravelProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class PostTravelHomeFragment extends Fragment {
    private TextView tvNama, tvTotalTravel,tvTotalBooking;
    private ImageView imgProfile;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;

    public PostTravelHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_travel_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvNama = view.findViewById(R.id.txt_nama);
        tvTotalTravel = view.findViewById(R.id.tv_total_travel);
        tvTotalBooking = view.findViewById(R.id.tv_total_booking);
        imgProfile = view.findViewById(R.id.imgProfile);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        loadDataProfile();
        loadTotal();
        getActivity().setTitle("Home");
    }

    @SuppressLint("SetTextI18n")
    private void loadTotal() {
        String uid = fAuth.getUid();

        Query queryTotalTravel = fStore.collection("travel").whereEqualTo("uid", uid);
        queryTotalTravel.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                int tot = queryDocumentSnapshots.size();
                Log.d("TAG", "onSuccess total hotel: " + tot);
                tvTotalTravel.setText(tot + " Travel");
            } else {
                tvTotalTravel.setText("0");
            }
        }).addOnFailureListener(e -> Log.d("TAG", "onFailure: " + e.toString()));

        Query queryTotalBooking = fStore.collection("booking").whereEqualTo("uid", uid);
        queryTotalBooking.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                int tot = queryDocumentSnapshots.size();
                tvTotalBooking.setText(tot + " Booking");
            } else {
                tvTotalBooking.setText("0");
            }
        }).addOnFailureListener(e -> Log.d("TAG", "onFailure: " + e.toString()));
    }

    private void loadDataProfile() {
        String uid = fAuth.getUid();
        assert uid != null;
        fStore.collection("usersTravel").document(uid).get()
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
                        Log.d("TAG", "onSuccess: " + users.getNama());
                        imgProfile.setOnClickListener(v -> {
                            Intent intent = new Intent(getContext(), PostTravelProfileActivity.class);
                            intent.putExtra(PostTravelProfileActivity.EXTRACT_USER, users);
                            startActivity(intent);
                        });
                    }
                }).addOnFailureListener(e -> Log.d("TAG", "onFailure: " + e.toString()));
    }
    @Override
    public void onResume() {
        super.onResume();

    }
}
