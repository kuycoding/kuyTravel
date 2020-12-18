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
import com.google.firebase.auth.FirebaseAuth;
import com.kuycoding.kuytravel.R;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText edtEmail;
    private Button btnSend;
    private ProgressDialog loadingBar;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        fAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            }
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.forgot_password));
        
        edtEmail = findViewById(R.id.edtEmail);
        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendForgotPass();
                edtEmail.setText("");

            }
        });
    }

    private void sendForgotPass() {
        final String email = edtEmail.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Email is required");
        } if (!email.matches(emailPattern)) {
            edtEmail.setError("Invalid email address");
        } else {
            loadingBar.setMessage(getString(R.string.please_wait));
            loadingBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loadingBar.setCancelable(false);
            loadingBar.setIndeterminate(false);
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            fAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        loadingBar.dismiss();
                        Toast.makeText(getApplicationContext(),"Password reset link was sent your email address",Toast.LENGTH_SHORT).show();
                    } else {
                        loadingBar.dismiss();
                        Toast.makeText(getApplicationContext(), "Mail sending error", Toast.LENGTH_SHORT).show();
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
