package com.example.phasmatic.ui.Profile_Menu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phasmatic.R;
import com.example.phasmatic.data.model.User;
import com.example.phasmatic.ui.LoginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgetActivity extends AppCompatActivity {

    Button btnChangePassword, btnVerifyViaEmail;
    EditText editNewPassword, editConfirmNewPassword, editEmailForMapping;
    ImageButton btnBack;

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        btnChangePassword = findViewById(R.id.btnchangePassword);
        btnVerifyViaEmail = findViewById(R.id.btnVerifyViaEmail);

        editNewPassword = findViewById(R.id.newPasswordfirst);
        editConfirmNewPassword = findViewById(R.id.newPasswordsecond);
        editEmailForMapping = findViewById(R.id.editEmailForMap);

        btnBack = findViewById(R.id.btnBack);

        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );

        usersRef = firebaseDb.getReference("users");

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnChangePassword.setOnClickListener(v -> {
            changePassword();
        });
    }

    private void changePassword(){

        String firstTimePass = editNewPassword.getText().toString().trim();
        String secondTimePass = editConfirmNewPassword.getText().toString().trim();
        String emailForMap = editEmailForMapping.getText().toString().trim();

        if(emailForMap.isEmpty()){
            Toast.makeText(this, "Enter your email to recover your password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (firstTimePass.isEmpty() || secondTimePass.isEmpty()) {
            Toast.makeText(this, "Fill in both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!firstTimePass.equals(secondTimePass)){
            Toast.makeText(this, "Passwords must match", Toast.LENGTH_SHORT).show();
            return;
        }

        usersRef.orderByChild("email").equalTo(emailForMap)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        if (!snapshot.exists()) {
                            Toast.makeText(ForgetActivity.this, "No user found with this email", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (DataSnapshot child : snapshot.getChildren()) {

                            User user = child.getValue(User.class);

                            if (user != null) {

                                String userId = child.getKey();
                                String oldPassword = user.getPassword();

                                if(firstTimePass.equals(oldPassword)){
                                    Toast.makeText(ForgetActivity.this,
                                            "New password must be different from old",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                //update password
                                usersRef.child(userId).child("password").setValue(firstTimePass)
                                        .addOnCompleteListener(updateTask -> {
                                            if(updateTask.isSuccessful()){
                                                Toast.makeText(ForgetActivity.this,
                                                        "Password changed successfully",
                                                        Toast.LENGTH_SHORT).show();

                                                startActivity(new Intent(ForgetActivity.this, LoginActivity.class));
                                                finish();

                                            }else{
                                                Toast.makeText(ForgetActivity.this,
                                                        "Error updating password",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                return;
                            }
                        }

                        Toast.makeText(ForgetActivity.this, "Error reading user data", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(ForgetActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}