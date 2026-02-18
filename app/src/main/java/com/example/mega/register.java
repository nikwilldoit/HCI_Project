package com.example.mega;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class register extends AppCompatActivity {

    EditText edtEmailAddressReg, edtPasswordReg, edtFullNameReg, edtDateOfBirthReg, edtPhoneNumberReg, edtBioReg;
    Button btnRegisterReg, btnLoginReg;

    TextView txtDisplayInfoReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtEmailAddressReg = findViewById(R.id.edtEmailAddressReg);
        edtPasswordReg = findViewById(R.id.edtPasswordReg);
        edtFullNameReg = findViewById(R.id.edtFullName);
        edtDateOfBirthReg = findViewById(R.id.edtDateOfBirthReg);
        edtPhoneNumberReg = findViewById(R.id.edtPhoneNumberReg);
        edtBioReg = findViewById(R.id.edtBioReg);

        btnLoginReg = findViewById(R.id.btnLoginReg);
        btnRegisterReg = findViewById(R.id.btnRegisterReg);

        txtDisplayInfoReg = findViewById(R.id.txtDisplayInfoReg);

        btnLoginReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(register.this,MainActivity.class);
                startActivity(i);

            }
        });

        btnRegisterReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = edtFullNameReg.getText().toString().trim();
                String email = edtEmailAddressReg.getText().toString().trim();
                String password = edtPasswordReg.getText().toString().trim();
                String dob = edtDateOfBirthReg.getText().toString().trim();
                String phone = edtPhoneNumberReg.getText().toString().trim();
                String bio = edtBioReg.getText().toString().trim();

                if (fullname.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(register.this, "Full name, email and password are required", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}