package com.kuycoding.kuytravel.ui.traveler.travel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.model.Travel;
import com.kuycoding.kuytravel.model.Users;
import com.kuycoding.kuytravel.ui.traveler.hotel.HotelBookingActivity;
import com.kuycoding.kuytravel.ui.traveler.hotel.HotelBookingSuccessActivity;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TravelBookingActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "tag";
    private DecimalFormat decim = new DecimalFormat("#,###.##");
    public static final String EXTRACT_BOOKING = "extract_booking";
    public static final String EXTRACT_USERS = "extract_users";
    private FirebaseAuth fauth;
    private FirebaseFirestore fstore;
    private Travel travel;
    private Users users, userTravel;

    private ImageView imgTravel;
    private TextView tvTravel, tvHarga, tvTotal, tvTotalOrang;
    private EditText edtTgl, edtNotes;

    private ProgressDialog progressDialog;
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private int jml = 0;
    private int totalHrg;
    private String totalSemua;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_booking);
        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);

        travel = getIntent().getParcelableExtra(EXTRACT_BOOKING);
        users = getIntent().getParcelableExtra(EXTRACT_USERS);

        Log.d(TAG, "onCreate nama: " + travel.getNama());
        Log.d(TAG, "onCreate: nama " + users.getNama());
// inisialisasi
        imgTravel = findViewById(R.id.imgTravel);
        tvTravel = findViewById(R.id.tv_detail_nama);
        tvHarga = findViewById(R.id.tv_detail_harga);
        tvTotal = findViewById(R.id.tv_detail_harga_total);
        tvTotalOrang = findViewById(R.id.tv_total_orang);
        tvTotalOrang.setText("1");
        edtNotes = findViewById(R.id.edtNotes);
        edtTgl = findViewById(R.id.edt_set_tanggal);

        Button btnBooking = findViewById(R.id.btnBooking);
        Button btnMinus = findViewById(R.id.btnMinus);
        Button btnPlus = findViewById(R.id.btnPlus);

        btnBooking.setOnClickListener(this);
        btnMinus.setOnClickListener(this);
        btnPlus.setOnClickListener(this);

        edtTgl.setInputType(InputType.TYPE_NULL);
        edtTgl.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            datePickerDialog = new DatePickerDialog(TravelBookingActivity.this, (view, i, i1, i2) -> {
                calendar.set(i, i1, i2);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd-MM-yyyy");
                String dateString = dateFormat.format(calendar.getTime());
                //editTgl.setText(i2 + "-" + (i1 + 1) + "-" + i);
                edtTgl.setText(dateString);
            }, year, month, day);
            datePickerDialog.show();
        });
        showDetailBooking();
    }

    @SuppressLint("SetTextI18n")
    private void showDetailBooking() {
        Picasso.get()
                .load(travel.getImage())
                .placeholder(R.drawable.placeholder)
                .into(imgTravel);
        tvTravel.setText(travel.getNama());
        tvHarga.setText("IDR " + decim.format(Integer.parseInt(travel.getHarga())));
        tvTotal.setText("IDR " + decim.format(Integer.parseInt(travel.getHarga())));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlus:
                increment();
                break;
            case R.id.btnMinus:
                decrement();
                break;
            case R.id.btnBooking:
                createBooking();
        }
    }

    private void increment() {
        jml++;
        display(jml);
    }

    private void decrement() {
        if (jml>1) {
            jml--;
        }
        display(jml);
    }

    @SuppressLint("SetTextI18n")
    private void display(int jml) {
        String jumlah = String.valueOf(jml);
        tvTotalOrang.setText(jumlah);

        int totalOrg = Integer.parseInt(travel.getHarga());
        totalHrg = totalOrg * jml;
        totalSemua = String.valueOf(totalHrg);
        tvTotal.setText("IDR " + decim.format(totalHrg));
    }

    private void createBooking() {
        String tgl = edtTgl.getText().toString().trim();
        String notes = edtNotes.getText().toString().trim();
        String proses = "Menunggu";
        String jenis = "travel";

        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("EEE, dd/MM/yyyy HH:mm:ss");
        String dateNow = df.format(c);

        String totalHarga = totalSemua;
        int jmlHarga = totalHrg;
        int hargaHotel = Integer.parseInt(travel.getHarga());

        if (TextUtils.isEmpty(tgl)) {
            edtTgl.setError("Tidak boleh kosong!");
        } else if (TextUtils.isEmpty(notes)) {
            edtNotes.setError("Tidak boleh kosong!");
        } else {
            progressDialog.setTitle("Buat pesanan");
            progressDialog.setMessage("Tunggu sebentar, pemesanan akan dibuat");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            DocumentReference booking = fstore.collection("booking").document();
            String id = booking.getId();

            Map<String, Object> post = new HashMap<>();
            post.put("id", id);
            post.put("uid", fauth.getUid());
            post.put("idHotel", travel.getId());
            post.put("postuid", users.getId());
            post.put("namaTravel", travel.getNama());
            post.put("namaTraveler", users.getNama());
            post.put("image", travel.getImage());
            post.put("tgl", tgl);
            post.put("dateCreate", dateNow);
            post.put("catatan", notes);
            post.put("status", proses);
            post.put("jenis", jenis);
            if (jmlHarga > hargaHotel) {
                post.put("total", totalHarga);
            } else {
                post.put("total", travel.getHarga());
            }

            booking.set(post).addOnSuccessListener(aVoid -> {
                progressDialog.dismiss();
                Intent intentSuccess = new Intent(TravelBookingActivity.this, HotelBookingSuccessActivity.class);
                startActivity(intentSuccess);
                finish();
            }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " ));
        }
    }
}
