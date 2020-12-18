package com.kuycoding.kuytravel.ui.admin.user;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.ui.admin.user.fragment.HotelFragment;
import com.kuycoding.kuytravel.ui.admin.user.fragment.TravelerFragment;
import com.kuycoding.kuytravel.ui.admin.user.fragment.UsersFragment;

public class AdminUsersActivity extends AppCompatActivity {
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        Fragment fragment;
        switch (item.getItemId()) {
            case R.id.nav_menu_user:
                fragment = new UsersFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_layour, fragment, fragment.getClass().getSimpleName())
                        .commit();
                return true;
            case R.id.nav_menu_traveler:
                fragment = new TravelerFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_layour, fragment, fragment.getClass().getSimpleName())
                        .commit();
                return true;
            case R.id.nav_menu_hotel:
                fragment = new HotelFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_layour, fragment, fragment.getClass().getSimpleName())
                        .commit();
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        BottomNavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (savedInstanceState == null) {
            navigationView.setSelectedItemId(R.id.nav_menu_user);
        }
    }
}
