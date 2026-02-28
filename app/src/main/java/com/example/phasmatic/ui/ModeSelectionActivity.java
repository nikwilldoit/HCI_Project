package com.example.phasmatic.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.PopupMenu;
import com.example.phasmatic.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ModeSelectionActivity extends AppCompatActivity {

    Button btnErasmus, btnMaster;
    TextView txtTitle, txtSubtitle;
    ImageView imgProfile;

    private String userId, userFullName, userEmail, userPhone;

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mode_selection);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userFullName = intent.getStringExtra("userFullName");
        userEmail = intent.getStringExtra("userEmail");
        userPhone = intent.getStringExtra("userPhone");

        txtTitle = findViewById(R.id.txtTitleMode);
        txtSubtitle = findViewById(R.id.txtSubtitleMode);
        btnErasmus = findViewById(R.id.btnErasmusMode);
        btnMaster = findViewById(R.id.btnMasterMode);
        imgProfile = findViewById(R.id.imgProfile);

        imgProfile.setOnClickListener(v -> showProfileMenu(v));

        btnErasmus.setOnClickListener(v -> {
            Intent i = new Intent(ModeSelectionActivity.this, ErasmusChatActivity.class);
            startActivity(i);
        });

        btnMaster.setOnClickListener(v -> {
            Intent i = new Intent(ModeSelectionActivity.this, MasterChatActivity.class);
            startActivity(i);
        });

        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        usersRef = firebaseDb.getReference("users");

    }

    private void showProfileMenu(android.view.View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_account) {
                showUserInfoDialog();
                return true;
            } else if (id == R.id.menu_logout) {
                logout();
                return true;
            }
            return false;
        });

        popup.show();
    }


    private void logout() {
        Intent i = new Intent(ModeSelectionActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }


    private void showUserInfoDialog() {
        android.view.LayoutInflater inflater = getLayoutInflater();
        android.view.View dialogView = inflater.inflate(R.layout.dialog_account, null);

        EditText edtName = dialogView.findViewById(R.id.edtAccountName);
        EditText edtEmail = dialogView.findViewById(R.id.edtAccountEmail);
        EditText edtPhone = dialogView.findViewById(R.id.edtAccountPhone);

        edtName.setText(userFullName != null ? userFullName : "");
        edtEmail.setText(userEmail != null ? userEmail : "");
        edtPhone.setText(userPhone != null ? userPhone : "");

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = edtName.getText().toString().trim();
                    String newEmail = edtEmail.getText().toString().trim();
                    String newPhone = edtPhone.getText().toString().trim();

                    //local update
                    userFullName = newName;
                    userEmail = newEmail;
                    userPhone = newPhone;

                    if (userId == null || userId.isEmpty()) {
                        Toast.makeText(this, "User id missing", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //update sto firebase
                    usersRef.child(userId).child("fullName").setValue(newName);
                    usersRef.child(userId).child("email").setValue(newEmail);
                    usersRef.child(userId).child("phoneNumber").setValue(newPhone)
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                            )
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


}

