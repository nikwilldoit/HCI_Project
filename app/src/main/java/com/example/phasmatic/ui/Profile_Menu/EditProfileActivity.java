package com.example.phasmatic.ui.Profile_Menu;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phasmatic.R;
import com.example.phasmatic.ui.BackButtonHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPhone;
    private Button btnSave;
    private ImageButton btnBack;

    private String userId, userFullName, userEmail, userPhone;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        edtName = findViewById(R.id.edtEditName);
        edtEmail = findViewById(R.id.edtEditEmail);
        edtPhone = findViewById(R.id.edtEditPhone);
        btnSave = findViewById(R.id.btnSaveEditProfile);
        btnBack = findViewById(R.id.btnBack);

        userId = getIntent().getStringExtra("userId");
        userFullName = getIntent().getStringExtra("userFullName");
        userEmail = getIntent().getStringExtra("userEmail");
        userPhone = getIntent().getStringExtra("userPhone");

        edtName.setText(userFullName != null ? userFullName : "");
        edtEmail.setText(userEmail != null ? userEmail : "");
        edtPhone.setText(userPhone != null ? userPhone : "");

        BackButtonHelper.attach(this, R.id.btnBack);

        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        usersRef = firebaseDb.getReference("users");

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String newName = edtName.getText().toString().trim();
        String newEmail = edtEmail.getText().toString().trim();
        String newPhone = edtPhone.getText().toString().trim();

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User id missing", Toast.LENGTH_SHORT).show();
            return;
        }

        usersRef.child(userId).child("fullName").setValue(newName);
        usersRef.child(userId).child("email").setValue(newEmail);
        usersRef.child(userId).child("phoneNumber").setValue(newPhone)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );

    }
}
