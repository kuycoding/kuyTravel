package com.kuycoding.kuytravel.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.ui.postHotel.PostHotelMainActivity;
import com.kuycoding.kuytravel.ui.postTravel.PostTravelActivity;
import com.kuycoding.kuytravel.ui.admin.AdminHomeActivity;
import com.kuycoding.kuytravel.ui.traveler.TravelerHomeActivity;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtEmail, edtPassword;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        Button btnForgot = findViewById(R.id.btnForgorPass);
        btnForgot.setOnClickListener(this);
        Button btnRegis = findViewById(R.id.btnCreateAccount);
        btnRegis.setOnClickListener(this);
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnForgorPass:
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                finish();
                break;
            case R.id.btnCreateAccount:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
                break;
            case R.id.btnLogin:
                login();
                break;
                default:
                    break;
        }
    }

    private void login() {
        final String email = edtEmail.getText().toString().trim();
        final String passwd = edtPassword.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Email harus diisi");
        } if (!email.matches(emailPattern)) {
            edtEmail.setError("Alamat email salah");
        } if (TextUtils.isEmpty(passwd)) {
            edtPassword.setError("Password tidak boleh kosong");
        } if (passwd.length() < 6) {
            edtPassword.setError("Password harus lebih 6 karakter");
        } else {
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            fAuth.signInWithEmailAndPassword(email, passwd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Login not successfull", Toast.LENGTH_SHORT).show();
                            } else {
                                String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                                final CollectionReference admin = fStore.collection("admin");
                                final CollectionReference user = fStore.collection("usersTravel");
                                final CollectionReference traveler = fStore.collection("traveler");
                                final CollectionReference hotel = fStore.collection("usersHotel");

                                admin.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser admin1 = FirebaseAuth.getInstance().getCurrentUser();
                                            if (admin1 == null) {
                                                progressDialog.dismiss();
                                                Toast.makeText(LoginActivity.this, "Email not verificarion, please verify email now!", Toast.LENGTH_SHORT).show();
                                                fAuth.signOut();
                                            } else {
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                assert documentSnapshot != null;
                                                if (documentSnapshot.exists()) {
                                                    String role = documentSnapshot.getString("role");
                                                    assert role != null;
                                                    if (role.equals("0")) {
                                                        progressDialog.dismiss();
                                                        startActivity(new Intent(LoginActivity.this, AdminHomeActivity.class));
                                                        finish();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                                user.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                                            if (user1 == null) {
                                                progressDialog.dismiss();
                                                Toast.makeText(LoginActivity.this, "Email not verificarion, please verify email now!", Toast.LENGTH_SHORT).show();
                                                fAuth.signOut();
                                            } else {
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                assert documentSnapshot != null;
                                                if (documentSnapshot.exists()) {
                                                    String role = documentSnapshot.getString("role");
                                                    assert role != null;
                                                    if (role.equals("1")) {
                                                        progressDialog.dismiss();
                                                        startActivity(new Intent(LoginActivity.this, PostTravelActivity.class));
                                                        finish();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                                traveler.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser traveler = FirebaseAuth.getInstance().getCurrentUser();
                                            if (traveler == null) {
                                                progressDialog.dismiss();
                                                Toast.makeText(LoginActivity.this, "Email not verificarion, please verify email now!", Toast.LENGTH_SHORT).show();
                                                fAuth.signOut();
                                            } else {
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                assert documentSnapshot != null;
                                                if (documentSnapshot.exists()) {
                                                    String role = documentSnapshot.getString("role");
                                                    assert role != null;
                                                    if (role.equals("2")) {
                                                        progressDialog.dismiss();
                                                        startActivity(new Intent(LoginActivity.this, TravelerHomeActivity.class));
                                                        finish();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                                hotel.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser traveler = FirebaseAuth.getInstance().getCurrentUser();
                                            if (traveler == null) {
                                                progressDialog.dismiss();
                                                Toast.makeText(LoginActivity.this, "Email not verificarion, please verify email now!", Toast.LENGTH_SHORT).show();
                                                fAuth.signOut();
                                            } else {
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                assert documentSnapshot != null;
                                                if (documentSnapshot.exists()) {
                                                    String role = documentSnapshot.getString("role");
                                                    assert role != null;
                                                    if (role.equals("3")) {
                                                        progressDialog.dismiss();
                                                        startActivity(new Intent(LoginActivity.this, PostHotelMainActivity.class));
                                                        finish();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                         }
                    });
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
