package com.kuycoding.kuytravel.ui.admin.travel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.kuytravel.R;

public class EditTravelActivity extends AppCompatActivity {
    private static final String TAG = "tag";
    private EditText edtNama, edtAlamat, edtKeterangan, edtLat, edtLong, edtHarga;
    private Spinner spinner_category;
    private ImageView imgTravel;
    private ProgressDialog dialog;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_travel);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        edtNama = findViewById(R.id.edtNama);
        edtAlamat   = findViewById(R.id.edtAlamat);
        edtKeterangan = findViewById(R.id.edtKeterangan);
        edtLat  = findViewById(R.id.edtLat);
        edtLong = findViewById(R.id.edtLong);
        edtHarga = findViewById(R.id.edtHarga);
        spinner_category = findViewById(R.id.spinner_category);
        imgTravel = findViewById(R.id.imgUpload);
        imgTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postTravel();
            }
        });
    }

    private void postTravel() {

    }
}
