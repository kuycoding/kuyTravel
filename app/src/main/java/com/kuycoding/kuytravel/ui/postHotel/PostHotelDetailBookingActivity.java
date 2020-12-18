package com.kuycoding.kuytravel.ui.postHotel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.model.Booking;
import com.kuycoding.kuytravel.ui.home.ForgotPasswordActivity;
import com.kuycoding.kuytravel.ui.home.LoginActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PostHotelDetailBookingActivity extends AppCompatActivity {
    public static final String EXTRACT_BOOKING = "extract_booking";
    private static final String TAG = "tag";
    private DecimalFormat decim = new DecimalFormat("#,###.##");
    private Booking booking;
    private ProgressDialog loadingBar;
    private FirebaseAuth fauth;
    private FirebaseFirestore fstore;
    private AutoCompleteTextView edtStatus;
    private TextView tvTanggal, tvJam, tvNotes, tvHarga, tvId, tvProses, tvNamaTraveler, tvNamaHotel;
    private Button btnSave;
    private ImageButton btnDelete;
    private ImageView imgQr;
    private AlertDialog.Builder alertDialog;
    private String proses;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_hotel_detail_booking);

        loadingBar = new ProgressDialog(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tiket Hotel");

        fstore = FirebaseFirestore.getInstance();
        fauth = FirebaseAuth.getInstance();
        booking = getIntent().getParcelableExtra(EXTRACT_BOOKING);

        tvNamaHotel = findViewById(R.id.tv_nama_hotel);
        tvNamaTraveler = findViewById(R.id.tv_nama_traveler);
        tvTanggal = findViewById(R.id.tv_tanggal);
        tvJam = findViewById(R.id.tv_jam);
        tvNotes = findViewById(R.id.tv_notes);
        tvProses = findViewById(R.id.tv_proses);
        tvId = findViewById(R.id.tv_id);
        imgQr = findViewById(R.id.imgQr);
        tvHarga = findViewById(R.id.tv_detail_harga_total);
        edtStatus = findViewById(R.id.edt_pesanan);
        String[] item = new String[] {
                "Dibatalkan",
                "Menunggu",
                "Lunas"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, item);
        edtStatus.setAdapter(adapter);
        edtStatus.setOnTouchListener((v, event) -> {
            edtStatus.showDropDown();
            edtStatus.requestFocus();
            return false;
        });

        btnSave = findViewById(R.id.btnSimpan);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editStatus();
            }
        });
        btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemDelete();
            }
        });

        loadDataBooking();
        createBarcode();
    }

    private void editStatus() {
        loadingBar.setMessage("Tunggu sebentar");
        loadingBar.show();
        final String status = edtStatus.getText().toString().trim();
        DocumentReference edit = fstore.collection("booking").document(booking.getId());
        Map<String, Object> ubah = new HashMap<>();
        ubah.put("status", status);
        edit.update(ubah).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss();
                Log.d(TAG, "onComplete: ");
                finish();
            }
        });
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
                Toast.makeText(PostHotelDetailBookingActivity.this, "Sukses hapus tiket", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton(android.R.string.no, null);
        alertDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void loadDataBooking() {
        tvTanggal.setText(booking.getTgl());
        tvJam.setText(booking.getJam());
        tvNotes.setText(booking.getCatatan());

        String stHarga = booking.getTotal();
        int intHarga = Integer.parseInt(stHarga);

        tvHarga.setText("IDR " +decim.format(intHarga));

        tvProses.setText(booking.getStatus());
        edtStatus.setText(booking.getStatus());
        tvNamaTraveler.setText(booking.getNamaTraveler());
        tvNamaHotel.setText(booking.getNamaHotel());

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
