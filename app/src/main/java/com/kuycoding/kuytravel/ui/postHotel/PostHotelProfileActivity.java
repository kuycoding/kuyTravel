package com.kuycoding.kuytravel.ui.postHotel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.model.Users;
import com.kuycoding.kuytravel.ui.home.LoginActivity;
import com.squareup.picasso.Picasso;

public class PostHotelProfileActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRACT_USER = "extract_user";
    private static final String TAG = "tag";
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private ImageView imgProfile;
    private TextView tvNama, tvEmail, tvBio;
    private LinearLayout ll_edit, ll_about, ll_exit;
    private Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_hotel_profile);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        users = getIntent().getParcelableExtra(EXTRACT_USER);

        imgProfile = findViewById(R.id.imgProfile);
        tvNama = findViewById(R.id.tv_nama);
        tvEmail = findViewById(R.id.tv_email);
        tvBio = findViewById(R.id.tv_bio);

        tvNama.setText(users.getNama());
        tvEmail.setText(users.getEmail());
        tvBio.setText(users.getBio());
        Picasso.get()
                .load(users.getImgUrl())
                .placeholder(R.drawable.person_upload)
                .error(R.drawable.person_upload)
                .into(imgProfile);

        ll_edit = findViewById(R.id.ll_editProfile);
        ll_edit.setOnClickListener(this);
        ll_about = findViewById(R.id.ll_about);
        ll_about.setOnClickListener(this);
        ll_exit = findViewById(R.id.ll_exit);
        ll_exit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_editProfile:
                break;
            case R.id.ll_about:
                break;
            case R.id.ll_exit:
                fAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }
}
