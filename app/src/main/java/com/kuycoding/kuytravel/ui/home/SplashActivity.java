package com.kuycoding.kuytravel.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.ui.admin.AdminHomeActivity;
import com.kuycoding.kuytravel.ui.postHotel.PostHotelMainActivity;
import com.kuycoding.kuytravel.ui.postTravel.PostTravelActivity;
import com.kuycoding.kuytravel.ui.traveler.TravelerHomeActivity;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() == null) {
            int waktu = 4000;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }, waktu);
        } else {
            readSession();
            isConnectedToInternet(this);
        }
    }

    public static void isConnectedToInternet(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                networkInfo.isConnectedOrConnecting();
            }
        }
    }
    private void readSession() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        CollectionReference users = fStore.collection("usersTravel");
        CollectionReference admin = fStore.collection("admin");
        CollectionReference traveler = fStore.collection("traveler");
        CollectionReference hoteler = fStore.collection("usersHotel");

        admin.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                assert documentSnapshot != null;
                if (documentSnapshot.exists()) {
                    role = documentSnapshot.getString("role");
                    assert role != null;
                    if (role.equals("0")) {
                        startActivity(new Intent(SplashActivity.this, AdminHomeActivity.class));
                        finish();
                    }
                }
            }
        });
        users.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                assert documentSnapshot != null;
                if (documentSnapshot.exists()) {
                    role = documentSnapshot.getString("role");
                    assert role != null;
                    if (role.equals("1")) {
                        startActivity(new Intent(SplashActivity.this, PostTravelActivity.class));
                        finish();
                    }
                }
            }
        });
        traveler.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    role = documentSnapshot.getString("role");
                    if (role.equals("2")) {
                        startActivity(new Intent(SplashActivity.this, TravelerHomeActivity.class));
                        finish();
                    }
                }
            }
        });
        hoteler.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    role = documentSnapshot.getString("role");
                    if (role.equals("3")) {
                        startActivity(new Intent(SplashActivity.this, PostHotelMainActivity.class));
                        finish();
                    }
                }
            }
        });
    }
}
