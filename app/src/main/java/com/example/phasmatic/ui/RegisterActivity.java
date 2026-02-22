package com.example.phasmatic.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.phasmatic.R;
import com.example.phasmatic.data.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText edtEmailAddressReg, edtPasswordReg, edtFullNameReg,
            edtDateOfBirthReg, edtPhoneNumberReg, edtBioReg;
    Button btnRegisterReg, btnLoginReg;
    TextView txtDisplayInfoReg;

    DatabaseReference usersRef;

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

        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        usersRef = firebaseDb.getReference("users");

        btnLoginReg.setOnClickListener(v -> {
            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        });

        btnRegisterReg.setOnClickListener(v -> {
            Toast.makeText(RegisterActivity.this,
                    "Register clicked", Toast.LENGTH_SHORT).show();

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

            //ws id pernaw to firebase key
            String userId = usersRef.push().getKey();
            Toast.makeText(RegisterActivity.this,
                    "userId = " + userId, Toast.LENGTH_SHORT).show();

            if (userId != null) {
                // 2) Το περνάμε και μέσα στο User
                User user = new User(userId, fullname, email, password, dob, phone, bio);

                usersRef.child(userId).setValue(user)
                        .addOnSuccessListener(unused -> {
                            android.util.Log.d("FIREBASE_TEST", "SUCCESS WRITE");
                            Toast.makeText(RegisterActivity.this,
                                    "Registration successful",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(
                                    RegisterActivity.this,
                                    LoginActivity.class
                            );
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            android.util.Log.e("FIREBASE_TEST",
                                    "FAIL WRITE", e);
                            Toast.makeText(RegisterActivity.this,
                                    "Firebase error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        });
            } else {
                Toast.makeText(RegisterActivity.this,
                        "Could not generate user id",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
}
