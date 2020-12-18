package com.kuycoding.kuytravel.ui.admin.hotel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.handler.GPSHandler;
import com.kuycoding.kuytravel.model.Hotel;
import com.kuycoding.kuytravel.model.Users;
import com.kuycoding.kuytravel.ui.traveler.hotel.CommentHotelActivity;
import com.kuycoding.kuytravel.ui.traveler.hotel.HotelBookingActivity;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class DetailHotelActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    public static final String EXTRACT_HOTEL = "extract_hotel";
    private DecimalFormat decim = new DecimalFormat("#,###.##");
    private Hotel hotel;
    private Users users;
    private FirebaseFirestore fstore;
    private FirebaseAuth fAuth;
    private static final String TAG = "tag";

    private TextView tvNama;
    private TextView tvAlamat;
    private TextView tvInformasi;
    private TextView tvHarga;
    private TextView tvDetailKamar;
    private ImageView imgCover;
    private RatingBar rate;

    //maps
    private GoogleMap mMap;
    private GPSHandler gpsHandler;
    private double startLat;
    private double endLat;
    private double startLng;
    private double endLng;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_hotel);

        fstore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        hotel = getIntent().getParcelableExtra(EXTRACT_HOTEL);


        tvNama = findViewById(R.id.tv_detail_nama);
        tvAlamat = findViewById(R.id.tv_detail_alamat);
        tvInformasi = findViewById(R.id.tv_detail_informasi);
        tvHarga = findViewById(R.id.tv_detail_harga);
        imgCover = findViewById(R.id.imgHotel);

        CardView cardView_review = findViewById(R.id.card_comment);
        cardView_review.setOnClickListener(this);
        CardView cardView_route = findViewById(R.id.card_route);
        cardView_route.setOnClickListener(this);
        tvDetailKamar = findViewById(R.id.tv_detail_kamar);
        rate = findViewById(R.id.rateHotel);
        Button btnBooking = findViewById(R.id.btnBooking);
        btnBooking.setOnClickListener(this);
        loadData();
    }

    @SuppressLint("SetTextI18n")
    private void loadData() {
        Picasso.get()
                .load(hotel.getImage())
                .placeholder(R.drawable.placeholder)
                .into(imgCover);

        tvNama.setText(hotel.getNama());
        tvAlamat.setText(hotel.getAlamat());
        tvInformasi.setText(hotel.getKeterangan());

        int intHarga = Integer.parseInt(hotel.getHarga());
        tvHarga.setText("IDR "+ decim.format(intHarga));
        tvDetailKamar.setText(hotel.getDetailKamar());

        //ratingBar
        float rating = Float.parseFloat(hotel.getRating());
        rate.setRating(rating);
        //maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        gpsHandler = new GPSHandler(this);
        startLat = gpsHandler.getLatitude();
        startLng = gpsHandler.getLongitude();
        endLat = Double.parseDouble(hotel.getLat());
        endLng = Double.parseDouble(hotel.getLng());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.maps_syle
            ));
            if (!success) {
                Log.d(TAG, "Style parsing failed ");
            }
        } catch (Resources.NotFoundException e) {
            Log.d(TAG, "Can't find style. Error: ", e);
        }
        mMap = googleMap;
        double latitude = Double.parseDouble(hotel.getLat());
        double longitude = Double.parseDouble(hotel.getLng());
        Log.d(TAG, "onMapReady: " + latitude);
        Log.d(TAG, "onMapReady: " + longitude);
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).title(hotel.getNama())).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.location));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBooking:
                createBooking();
                Log.d(TAG, "onClick: " + hotel.getUid());
                break;
            case R.id.card_route:
                routeMaps();
                break;
            case R.id.card_comment:
                commentHotel();
                break;

            default:
                break;
        }
    }

    private void commentHotel() {
        Intent intent = new Intent(this, CommentHotelActivity.class);
        intent.putExtra(CommentHotelActivity.EXTRA_COMMENT, hotel);
        startActivity(intent);
    }

    private void routeMaps() {
        Intent intentRoute = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + startLat + "," + startLng + "&daddr=" + endLat + "," + endLng));
        startActivity(intentRoute);
    }

    private void createBooking() {
        fstore.collection("traveler").document(fAuth.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Users users = documentSnapshot.toObject(Users.class);
                        Log.d(TAG, "createBooking: " + users.getNama());
                        Intent intent = new Intent(DetailHotelActivity.this, HotelBookingActivity.class);
                        intent.putExtra(HotelBookingActivity.EXTRACT_BOOKING, hotel);
                        intent.putExtra(HotelBookingActivity.EXTRACT_USERS, users);
                        startActivity(intent);
                    }
                }).addOnFailureListener(e -> Log.d(TAG, "onFailure: "));
    }
}
