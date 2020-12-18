package com.kuycoding.kuytravel.ui.admin.travel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.handler.GPSHandler;
import com.kuycoding.kuytravel.model.Travel;
import com.kuycoding.kuytravel.model.Users;
import com.kuycoding.kuytravel.ui.traveler.travel.TravelBookingActivity;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class DetailTravelActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    public static final String EXTRACT_TRAVEL = "extra_travel";
    private final static String TAG = "Pesan";
    private TextView tvNama;
    private TextView tvAlamat;
    private TextView tvCountFav;
    private TextView tvKeterangan;
    private TextView tvJarak;
    private TextView tvCountView;
    private TextView tvHarga;
    private ImageView imgDetail, imgLike, imgFav;
    private Button btnBooking;
    private DecimalFormat decim = new DecimalFormat("#,###.##");

    private GoogleMap mMap;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private GPSHandler gpsHandler;

    private boolean isFavorite = false;
    private double startLat;
    private double endLat;
    private double startLng;
    private double endLng;
    private Travel travel;
    private Menu menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_travel);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        travel = getIntent().getParcelableExtra(EXTRACT_TRAVEL);

        tvNama = findViewById(R.id.tv_detail_nama_place);
        tvAlamat = findViewById(R.id.tv_detail_alamat);
        tvCountView = findViewById(R.id.tv_detail_count_view);
        tvCountFav = findViewById(R.id.tv_detail_count_favorite);
        tvJarak = findViewById(R.id.tv_detail_jarak);
        tvKeterangan = findViewById(R.id.tv_detail_informasi);
        tvHarga = findViewById(R.id.tv_detail_harga);
        imgDetail = findViewById(R.id.imgDetail);
//        imgLike = findViewById(R.id.img_detail_add_like);
//        imgLike.setOnClickListener(this);
//        imgFav = findViewById(R.id.img_detail_add_favorite);

        CardView cardComment = findViewById(R.id.card_comment);
        cardComment.setOnClickListener(this);
        CardView cardRoute = findViewById(R.id.card_route);
        cardRoute.setOnClickListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        btnBooking = findViewById(R.id.btnBooking);
        btnBooking.setOnClickListener(this);
        loadDataDetail();
        favoriteState();
    }

    @SuppressLint("SetTextI18n")
    private void loadDataDetail() {
        travel = getIntent().getParcelableExtra(EXTRACT_TRAVEL);
        assert travel != null;
        tvNama.setText(travel.getNama());
        tvAlamat.setText(travel.getAlamat());
        tvKeterangan.setText(travel.getKeterangan());

        int harga = Integer.parseInt(travel.getHarga());
        tvHarga.setText("IDR "+ decim.format(harga));
        Picasso.get()
                .load(travel.getImage())
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .into(imgDetail);

        tvCountView.setText(String.valueOf(travel.getView()));
     //   tvCountFav.setText(String.valueOf(travel.getLike()));

        gpsHandler = new GPSHandler(this);
        startLat = gpsHandler.getLatitude();
        startLng = gpsHandler.getLongitude();
        endLat = Double.parseDouble(travel.getLat());
        endLng = Double.parseDouble(travel.getLng());

        Double distance = calculateDistance(startLat, startLng, endLat, endLng);
        @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", distance);
        tvJarak.setText(distanceFormat + " Km");
    }

    //hitung jarak
    private double calculateDistance(double startLat, double startlng, double endlat, double endLng) {
        double earthRadius = 6371;
        double latDiff = Math.toRadians(startLat - endlat);
        double lngDiff = Math.toRadians(startlng - endLng);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endlat)) *
                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return (distance * meterConversion / 1000);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        double latitude = Double.parseDouble(travel.getLat());
        double longitude = Double.parseDouble(travel.getLng());
        Log.d(TAG, "onMapReady: " + latitude);
        Log.d(TAG, "onMapReady: " + longitude);
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).title(travel.getNama()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_comment:
                Intent intent = new Intent(this, CommentTravelActivity.class);
                intent.putExtra(CommentTravelActivity.EXTRA_COMMENT, travel);
                startActivity(intent);
                break;
            case R.id.card_route:
                Intent intentRoute = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + startLat + "," + startLng + "&daddr=" + endLat + "," + endLng));
                startActivity(intentRoute);
                break;
            case R.id.btnBooking:
                createBooking();
                Log.d(TAG, "onClick: to intent");
                Log.d(TAG, "onClick: " + travel.getId());
                break;
            default:
                break;
        }
    }

    private void createBooking() {
        fStore.collection("usersTravel").document(travel.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Users users = documentSnapshot.toObject(Users.class);
                            Intent intent = new Intent(DetailTravelActivity.this, TravelBookingActivity.class);
                            intent.putExtra(TravelBookingActivity.EXTRACT_BOOKING, travel);
                            intent.putExtra(TravelBookingActivity.EXTRACT_USERS, users);
                            startActivity(intent);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: gagal intent");
            }
        });
    }

    private void favoriteState() {
        final String uid = fAuth.getUid();
        DocumentReference dbFav = fStore.collection("travel").document(travel.getId()).collection("favorite").document(uid);
        dbFav.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    assert documentSnapshot != null;
                    if (documentSnapshot.getBoolean("favorite")) {
                        isFavorite = true;
                        menuItem.getItem(0).setIcon(R.drawable.ic_bookmark);
                    } else if (!documentSnapshot.getBoolean("favorite")) {
                        menuItem.getItem(0).setIcon(R.drawable.ic_bookmark_border);
                    }
                } else {
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorite_menu, menu);
        menuItem = menu;
        setFavorite();
        return true;
    }

    private void setFavorite() {
        if (isFavorite) {
            menuItem.getItem(0).setIcon(R.drawable.ic_bookmark);
        } else {
            menuItem.getItem(0).setIcon(R.drawable.ic_bookmark_border);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_favorite) {
            if (isFavorite) {
                removeFav();
            } else {
                addFav();
            }
            isFavorite = !isFavorite;
            setFavorite();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addFav() {
        final String uid = fAuth.getUid();
        DocumentReference dbfav = fStore.collection("travel").document(travel.getId()).collection("favorite").document(uid);
        Map<String, Object> fav = new HashMap<>();
        fav.put("favorite", true);
        dbfav.set(fav).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: add favorite");
            }
        });
    }

    private void removeFav() {
        final String uid = fAuth.getUid();
        DocumentReference dbfav = fStore.collection("travel").document(travel.getId()).collection("favorite").document(uid);
        Map<String, Object> fav = new HashMap<>();
        fav.put("favorite", false);
        dbfav.set(fav).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: add favorite");
            }
        });
    }
}
