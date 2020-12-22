package com.kuycoding.kuytravel.ui.traveler.hotel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.model.Booking;
import com.kuycoding.kuytravel.ui.postHotel.PostHotelDetailBookingActivity;

import java.text.DecimalFormat;
import java.util.Objects;

public class HotelBookingDetailActivity extends AppCompatActivity {
    public static final String EXTRACT_BOOKING = "extract_booking";
    private static final String TAG = "tag";
    private DecimalFormat decim = new DecimalFormat("#,###.##");
    private Booking booking;
    private ScrollView scrollView;
    private TextView tvEmpty;
    private ProgressDialog loadingBar;
    private FirebaseAuth fauth;
    private FirebaseFirestore fstore;
    private TextView tvTanggal, tvJam, tvNotes, tvHarga, tvProses, tvId;
    private Button btnBack;
    private ImageButton btnDelete;
    private ImageView imgQr;
    private AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_booking_detail);

        loadingBar = new ProgressDialog(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tiket Hotel");

        fstore = FirebaseFirestore.getInstance();
        fauth = FirebaseAuth.getInstance();
        booking = getIntent().getParcelableExtra(EXTRACT_BOOKING);

        scrollView = findViewById(R.id.scrollView);
        tvEmpty = findViewById(R.id.text_nodata);
        tvTanggal = findViewById(R.id.tv_tanggal);
        tvJam = findViewById(R.id.tv_jam);
        tvNotes = findViewById(R.id.tv_notes);
        tvProses = findViewById(R.id.tv_proses);
        tvId = findViewById(R.id.tv_id);
        imgQr = findViewById(R.id.imgQr);
        tvHarga = findViewById(R.id.tv_detail_harga_total);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemDelete();
            }
        });

        if (booking.getId() == null) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.INVISIBLE);
            scrollView.setVisibility(View.VISIBLE);
            loadDataBooking();
        }
        createBarcode();
    }

    private void itemDelete() {
        booking = getIntent().getParcelableExtra(EXTRACT_BOOKING);
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Hapus tiket");
        alertDialog.setMessage("Anda yakin akan hapus tiket ini?");
        alertDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fstore.collection("booking").document(booking.getId()).delete();
                finish();
                Toast.makeText(HotelBookingDetailActivity.this, "Sukses hapus tiket", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton(android.R.string.no, null);
        alertDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void loadDataBooking() {
        tvTanggal.setText(booking.getTgl());
        tvJam.setText(booking.getJam());
        if (booking.getCatatan().isEmpty()) {
            tvNotes.setText("Tidak ada catatan");
        } else {
            tvNotes.setText(booking.getCatatan());
        }

        tvHarga.setText("IDR " +decim.format(Integer.parseInt(booking.getTotal())));
        tvProses.setText(booking.getStatus());
        tvId.setText(booking.getId());
    }

    private void createBarcode() {
        String id = booking.getId().trim();
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(id, BarcodeFormat.QR_CODE, 400, 400);
            imgQr.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
