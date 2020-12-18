package com.kuycoding.kuytravel.ui.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.ui.admin.category.AdminCategoryActivity;
import com.kuycoding.kuytravel.ui.admin.hotel.AdminHotelActivity;
import com.kuycoding.kuytravel.ui.admin.user.AdminUsersActivity;
import com.kuycoding.kuytravel.ui.admin.travel.AdminTravelActivity;
import com.kuycoding.kuytravel.ui.home.LoginActivity;

import java.util.Calendar;
import java.util.Objects;

public class AdminHomeActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "tag";
    private TextView tvName, tvUcapan;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        tvName = findViewById(R.id.tv_nama);
        tvUcapan = findViewById(R.id.tvUcapan);
        CardView cardViewAdmin = findViewById(R.id.card_admin);
        cardViewAdmin.setOnClickListener(this);
        CardView cardViewCategory = findViewById(R.id.card_category);
        cardViewCategory.setOnClickListener(this);
        CardView cardViewHotel = findViewById(R.id.card_hotel);
        cardViewHotel.setOnClickListener(this);
//        CardView cardViewKuliner = findViewById(R.id.card_kuliner);
//        cardViewKuliner.setOnClickListener(this);
        CardView cardViewLogout = findViewById(R.id.card_logout);
        cardViewLogout.setOnClickListener(this);
        CardView cardViewPostTravel = findViewById(R.id.card_travel);
        cardViewPostTravel.setOnClickListener(this);
        CardView cardViewUser = findViewById(R.id.card_user);
        cardViewUser.setOnClickListener(this);
        ucapan();
        readData();
    }

    private void readData() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        fStore.collection("admin").document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String nama = Objects.requireNonNull(task.getResult()).getString("nama");
                            tvName.setText(nama);
                            Log.d(TAG, "onComplete: " + task.getResult().getString("nama"));
                        } else {
                            Log.d(TAG, "onNull");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });
    }

    private void ucapan() {
        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        String greeting = null;
        if (timeOfDay >= 0 && timeOfDay < 12) {
            greeting = "Selamat Pagi";
        } else if (timeOfDay >= 12 && timeOfDay <= 16) {
            greeting = "Selamat Siang";
        } else if (timeOfDay >= 16 && timeOfDay <= 18) {
            greeting = "Selamat Sore";
        } else {
            greeting = "Selamat Malam";
        }
        tvUcapan.setText(greeting);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_logout:
                startActivity(new Intent(AdminHomeActivity.this, LoginActivity.class));
                fAuth.signOut();
                break;
            case R.id.card_category:
                startActivity(new Intent(AdminHomeActivity.this, AdminCategoryActivity.class));
                break;
            case R.id.card_travel:
                startActivity(new Intent(AdminHomeActivity.this, AdminTravelActivity.class));
                break;
            case R.id.card_user:
                startActivity(new Intent(AdminHomeActivity.this, AdminUsersActivity.class));
                break;
            case R.id.card_admin:
                startActivity(new Intent(AdminHomeActivity.this, AdminListActivity.class));
                break;
            case R.id.card_hotel:
                startActivity(new Intent(AdminHomeActivity.this, AdminHotelActivity.class));
                break;
        }
    }
}
