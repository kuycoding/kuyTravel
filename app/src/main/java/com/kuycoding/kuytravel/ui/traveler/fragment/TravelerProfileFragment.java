package com.kuycoding.kuytravel.ui.traveler.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.model.Users;
import com.kuycoding.kuytravel.ui.home.LoginActivity;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class TravelerProfileFragment extends Fragment implements View.OnClickListener {
    private TextView tvNama, tvEmail, tvBio;
    private ImageView imgProfile;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;

    public TravelerProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_traveler_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        tvNama = view.findViewById(R.id.tv_nama);
        tvEmail = view.findViewById(R.id.tv_email);
        tvBio = view.findViewById(R.id.tv_bio);
        imgProfile = view.findViewById(R.id.imgProfile);
        LinearLayout ll_about = view.findViewById(R.id.ll_about);
        LinearLayout ll_edit = view.findViewById(R.id.ll_editProfile);
        LinearLayout ll_exit = view.findViewById(R.id.ll_exit);
        ll_exit.setOnClickListener(this);

        loadData();
    }

    private void loadData() {
        fStore.collection("traveler").document(Objects.requireNonNull(fAuth.getUid())).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Users users = documentSnapshot.toObject(Users.class);
                            assert users != null;
                            tvNama.setText(users.getNama());
                            tvEmail.setText(users.getEmail());
                            tvBio.setText(users.getBio());

                            Picasso.get()
                                    .load(users.getImgUrl())
                                    .placeholder(R.drawable.placeholder)
                                    .into(imgProfile);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "onFailure: " + e.toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_about:
                break;
            case R.id.ll_editProfile:
                break;
            case R.id.ll_exit:
                fAuth.signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                Objects.requireNonNull(getActivity()).finish();
                break;
        }
    }
}
