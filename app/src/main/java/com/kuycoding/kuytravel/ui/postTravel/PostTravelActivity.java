package com.kuycoding.kuytravel.ui.postTravel;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.ui.postTravel.fragment.PostTravelBookingFragment;
import com.kuycoding.kuytravel.ui.postTravel.fragment.PostTravelHomeFragment;
import com.kuycoding.kuytravel.ui.postTravel.fragment.PostTravelListFragment;

public class PostTravelActivity extends AppCompatActivity {
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = item -> {
        Fragment fragment;
        switch (item.getItemId()) {
            case R.id.nav_menu_travel_home:
                fragment = new PostTravelHomeFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_layout, fragment, fragment.getClass().getSimpleName())
                        .commit();
                return true;
            case R.id.nav_menu_travel_post:
                fragment = new PostTravelListFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_layout, fragment, fragment.getClass().getSimpleName())
                        .commit();
                return true;
            case R.id.nav_menu_travel_booking:
                fragment = new PostTravelBookingFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_layout, fragment, fragment.getClass().getSimpleName())
                        .commit();
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_travel);

        BottomNavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        if (savedInstanceState == null) {
            navigationView.setSelectedItemId(R.id.nav_menu_travel_home);
        }
    }
}
