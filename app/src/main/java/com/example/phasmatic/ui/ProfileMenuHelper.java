package com.example.phasmatic.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;

import com.example.phasmatic.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileMenuHelper {

    private final Activity activity;
    private final DatabaseReference usersRef;

    private String userId;
    private String userFullName;
    private String userEmail;
    private String userPhone;

    public ProfileMenuHelper(Activity activity,
                             String userId,
                             String userFullName,
                             String userEmail,
                             String userPhone) {

        this.activity = activity;
        this.userId = userId;
        this.userFullName = userFullName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;

        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        usersRef = firebaseDb.getReference("users");
    }

    public void showProfileMenu(View anchor) {
        PopupMenu popup = new PopupMenu(activity, anchor);
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

    private void showUserInfoDialog() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_account, null);

        EditText edtName = dialogView.findViewById(R.id.edtAccountName);
        EditText edtEmail = dialogView.findViewById(R.id.edtAccountEmail);
        EditText edtPhone = dialogView.findViewById(R.id.edtAccountPhone);

        edtName.setText(userFullName != null ? userFullName : "");
        edtEmail.setText(userEmail != null ? userEmail : "");
        edtPhone.setText(userPhone != null ? userPhone : "");

        new AlertDialog.Builder(activity)
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = edtName.getText().toString().trim();
                    String newEmail = edtEmail.getText().toString().trim();
                    String newPhone = edtPhone.getText().toString().trim();

                    userFullName = newName;
                    userEmail = newEmail;
                    userPhone = newPhone;

                    if (userId == null || userId.isEmpty()) {
                        Toast.makeText(activity, "User id missing", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    usersRef.child(userId).child("fullName").setValue(newName);
                    usersRef.child(userId).child("email").setValue(newEmail);
                    usersRef.child(userId).child("phoneNumber").setValue(newPhone)
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(activity, "Profile updated", Toast.LENGTH_SHORT).show()
                            )
                            .addOnFailureListener(e ->
                                    Toast.makeText(activity, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void logout() {
        Intent i = new Intent(activity, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(i);
        activity.finish();
    }
}
