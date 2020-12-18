package com.kuycoding.kuytravel.ui.traveler.category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.model.Category;
import com.kuycoding.kuytravel.ui.traveler.hotel.TravelerHotelListActivity;

public class TravelDetailCategoryActivity extends AppCompatActivity {

    public static final String EXTRACT_CATEGORY = "extact_category";
    public static final String TAG = "travel_category";
    private FirebaseFirestore fStore;
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_detail_category);
        
        category = getIntent().getParcelableExtra(EXTRACT_CATEGORY);
        Log.d(TAG, "onCreate: " + category.getCategory());
        Log.d(TAG, "onCreate: " + category.getId());


    }
}
