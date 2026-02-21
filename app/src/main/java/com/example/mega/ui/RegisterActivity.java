package com.example.mega.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mega.R;
import com.example.mega.data.db.DbConnect;
import com.example.mega.data.model.User;

public class RegisterActivity extends AppCompatActivity {

    EditText edtEmailAddressReg, edtPasswordReg, edtFullNameReg,
            edtDateOfBirthReg, edtPhoneNumberReg, edtBioReg;
    Button btnRegisterReg, btnLoginReg;
    TextView txtDisplayInfoReg;

    DbConnect db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        edtEmailAddressReg = findViewById(R.id.edtEmailAddressReg);
        edtPasswordReg = findViewById(R.id.edtPasswordReg);
        edtFullNameReg = findViewById(R.id.edtFullName);
        edtDateOfBirthReg = findViewById(R.id.edtDateOfBirthReg);
        edtPhoneNumberReg = findViewById(R.id.edtPhoneNumberReg);
        edtBioReg = findViewById(R.id.edtBioReg);

        btnLoginReg = findViewById(R.id.btnLoginReg);
        btnRegisterReg = findViewById(R.id.btnRegisterReg);
        txtDisplayInfoReg = findViewById(R.id.txtDisplayInfoReg);

        db = new DbConnect(this);

        btnLoginReg.setOnClickListener(v -> {
            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(i);
        });

        btnRegisterReg.setOnClickListener(v -> {
            String fullname = edtFullNameReg.getText().toString().trim();
            String email = edtEmailAddressReg.getText().toString().trim();
            String password = edtPasswordReg.getText().toString().trim();
            String dob = edtDateOfBirthReg.getText().toString().trim();
            String phone = edtPhoneNumberReg.getText().toString().trim();
            String bio = edtBioReg.getText().toString().trim();

            if (fullname.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this,
                        "Full name, email and password are required",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(0, fullname, email, password, dob, phone, bio);
            db.insertUser(user);

            txtDisplayInfoReg.setText("User registered: " + fullname);
            Toast.makeText(RegisterActivity.this,
                    "Registration successful", Toast.LENGTH_SHORT).show();
        });
    }
}
