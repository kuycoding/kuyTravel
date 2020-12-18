package com.kuycoding.kuytravel.ui.traveler.hotel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.model.Booking;
import com.kuycoding.kuytravel.model.Hotel;

public class HotelBookingSuccessActivity extends AppCompatActivity {
    public static final String EXTRACT_SUCCESS = "extract_success";
    private static final String TAG = "tag";
    private Button btnBeranda;
    private TextView tvTitle, tvSubTitle;
    private ImageView imgSuccess;
    private Animation app_splash, btt, ttb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_booking_success);

        tvTitle = findViewById(R.id.app_title);
        tvSubTitle = findViewById(R.id.app_subtitle);
        imgSuccess = findViewById(R.id.icon_success);

        btnBeranda = findViewById(R.id.btnBeranda);
        btnBeranda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //loadAnim
        app_splash = AnimationUtils.loadAnimation(this, R.anim.app_splash);
        btt = AnimationUtils.loadAnimation(this,R.anim.bottom_up);
        ttb = AnimationUtils.loadAnimation(this,R.anim.top_down);

        //run animation
        imgSuccess.startAnimation(app_splash);
        tvTitle.startAnimation(ttb);
        tvSubTitle.startAnimation(ttb);
        btnBeranda.startAnimation(btt);
    }
}
