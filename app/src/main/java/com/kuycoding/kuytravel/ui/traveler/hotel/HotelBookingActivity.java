package com.kuycoding.kuytravel.ui.traveler.hotel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.model.Hotel;
import com.kuycoding.kuytravel.model.Users;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HotelBookingActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRACT_BOOKING = "extract_booking";
    public static final String EXTRACT_USERS = "extract_users";
    private DecimalFormat decim = new DecimalFormat("#,###.##");
    private Hotel hotel;
    private Users users, userTravel;
    private FirebaseAuth fauth;
    private FirebaseFirestore fstore;
    private ImageView imgHotel;
    private TextView tvHotel, tvHarga, tvTotal, edtTotal;
    private RatingBar ratingBar;
    private EditText edtJam, edtTgl, edtNotes;
    private Button btnBooking, btnPlus, btnMinus;
    private ProgressDialog progressDialog;
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private int jml = 0;
    private int totalHrg;
    private String totalSemua;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_booking);

        fstore = FirebaseFirestore.getInstance();
        fauth = FirebaseAuth.getInstance();
        hotel = getIntent().getParcelableExtra(EXTRACT_BOOKING);
        users = getIntent().getParcelableExtra(EXTRACT_USERS);
        progressDialog = new ProgressDialog(this);

        Log.d("TAG", "onCreate: " + users.getNama());
        imgHotel = findViewById(R.id.imgHotel);
        tvHotel = findViewById(R.id.tv_detail_nama);
        tvHarga = findViewById(R.id.tv_detail_harga);
        ratingBar = findViewById(R.id.rateHotel);
        tvTotal = findViewById(R.id.tv_detail_harga_total);
        edtTotal = findViewById(R.id.edt_total);
        edtTotal.setText("1");
        btnMinus = findViewById(R.id.btnMinus);
        btnMinus.setOnClickListener(this);
        btnPlus = findViewById(R.id.btnPlus);
        btnPlus.setOnClickListener(this);
        btnBooking = findViewById(R.id.btnBooking);
        btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakeBookingHotel();
            }
        });

        edtJam = findViewById(R.id.edt_set_jam);
        edtJam.setInputType(InputType.TYPE_NULL);
        edtJam.setOnClickListener(v -> {
            calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            timePickerDialog = new TimePickerDialog(HotelBookingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    edtJam.setText(updateTime(hourOfDay, minute));
                }
            },hour,minute,false);
            timePickerDialog.show();
        });

        edtTgl = findViewById(R.id.edt_set_tanggal);
        edtTgl.setInputType(InputType.TYPE_NULL);
        edtTgl.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            datePickerDialog = new DatePickerDialog(HotelBookingActivity.this, (view, i, i1, i2) -> {
                calendar.set(i, i1, i2);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd-MM-yyyy");
                String dateString = dateFormat.format(calendar.getTime());
                //editTgl.setText(i2 + "-" + (i1 + 1) + "-" + i);
                edtTgl.setText(dateString);
            }, year, month, day);
            datePickerDialog.show();
        });
        edtNotes = findViewById(R.id.edtNotes);

        Picasso.get()
                .load(hotel.getImage())
                .placeholder(R.drawable.placeholder)
                .into(imgHotel);
        tvHotel.setText(hotel.getNama());
        int intHarga = Integer.parseInt(hotel.getHarga());
        tvHarga.setText("IDR " + decim.format(intHarga));

        int intTotal = Integer.parseInt(hotel.getHarga());
        tvTotal.setText("IDR " + decim.format(intTotal));
        float rate = Float.parseFloat(hotel.getRating());
        ratingBar.setRating(rate);
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
        edtTotal.setText(jumlah);
        int total = Integer.parseInt(hotel.getHarga());
        totalHrg = total * jml;
        totalSemua = String.valueOf(totalHrg);
        tvTotal.setText("IDR "+ decim.format(totalHrg));
    }

    private String updateTime(int hours, int mins) {
        String timeSet;
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";
        String minutes;
        if (mins < 10) {
            minutes = "0" + mins;
        } else
            minutes = String.valueOf(mins);
        // Append in a StringBuilder
        return String.valueOf(hours) + ':' +
                minutes + " " + timeSet;
    }

    private void MakeBookingHotel() {
        String tgl = edtTgl.getText().toString().trim();
        String jam = edtJam.getText().toString().trim();
        String note = edtNotes.getText().toString().trim();
        String proses = "Menunggu";

        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("EEE, dd/MM/yyyy HH:mm:ss");
        String dateNow = df.format(c);

        String totalHarga = totalSemua;
        int jmlHarga = totalHrg;
        int hargaHotel = Integer.parseInt(hotel.getHarga());

        if (TextUtils.isEmpty(tgl)) {
            edtTgl.setError("Harus di isi");
        } else if (TextUtils.isEmpty(jam)) {
            edtJam.setError("Harus di isi");
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
            post.put("idHotel", hotel.getId());
            post.put("postuid", users.getId());
            post.put("namaHotel", hotel.getNama());
            post.put("namaTraveler", users.getNama());
            post.put("image", hotel.getImage());
            post.put("tgl", tgl);
            post.put("jam", jam);
            post.put("dateCreate", dateNow);
            post.put("catatan", note);
            post.put("status", proses);
            if (jmlHarga > hargaHotel) {
                post.put("total", totalHarga);
            } else {
                post.put("total", hotel.getHarga());
            }

            fstore.collection("usersHotel").document(Objects.requireNonNull(fauth.getUid())).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                userTravel = documentSnapshot.toObject(Users.class);
                                assert userTravel != null;
                                post.put("namaUser", userTravel.getNama());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("TAG", "onFailure: no data ");
                }
            });

            booking.set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("TAG", "onSuccess: ");
                    progressDialog.dismiss();
                    Intent intent = new Intent(HotelBookingActivity.this, HotelBookingSuccessActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("TAG", "onFailure: ");
                    progressDialog.dismiss();
                    Toast.makeText(HotelBookingActivity.this, "Gagal booking hotel", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
