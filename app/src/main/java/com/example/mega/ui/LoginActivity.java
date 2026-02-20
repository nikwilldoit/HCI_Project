package com.example.mega.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mega.R;
import com.example.mega.data.db.DbConnect;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmailAddressLog, edtPasswordLog;
    Button btnRegisterLog, btnLoginLog;
    TextView txtDisplayInfoLog;

    DbConnect db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtEmailAddressLog = findViewById(R.id.edtEmailAddressLog);
        edtPasswordLog = findViewById(R.id.edtPasswordLog);
        btnLoginLog = findViewById(R.id.btnLoginLog);
        btnRegisterLog = findViewById(R.id.btnRegisterLog);
        txtDisplayInfoLog = findViewById(R.id.txtDisplayInfoLog);

        db = new DbConnect(this);

        btnRegisterLog.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
        });

        btnLoginLog.setOnClickListener(v -> {
            String email = edtEmailAddressLog.getText().toString().trim();
            String password = edtPasswordLog.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                txtDisplayInfoLog.setText("Please enter email and password");
                return;
            }

            boolean ok = db.checkUserCredentials(email, password);

            if (ok) {
                txtDisplayInfoLog.setText("Login successful");
            } else {
                txtDisplayInfoLog.setText("Incorrect email or password");
            }
        });
    }
}
