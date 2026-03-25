package com.example.phasmatic.ui.Profile_Menu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.phasmatic.R;
import com.example.phasmatic.extras.ProfileImageManager;
import com.example.phasmatic.extras.SupabaseStorageHelper;
import com.example.phasmatic.ui.BackButtonHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AccountActivity extends AppCompatActivity {

    ImageButton btnBack;
    private Button btnEditProfile, btnEditAcademic;
    private TextView txtFullName, txtEmail, txtPhone;
    private TextView txtUniversity, txtAcademicLevel, txtField,
            txtLanguages, txtGpa, txtBudget, txtYearOfStudies, txtAdvisorType;

    private String userId;
    private String userFullName, userEmail, userPhone;

    private ImageView imgProfilePhoto;
    private Uri imageUri;

    private static final int PICK_IMAGE = 1;
    private static final int TAKE_PHOTO = 2;

    private DatabaseReference usersRef;
    private DatabaseReference userInfoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        btnBack = findViewById(R.id.btnBack);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnEditAcademic = findViewById(R.id.btnEditAcademic);

        txtFullName = findViewById(R.id.txtFullName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);

        txtUniversity = findViewById(R.id.txtUniversity);
        txtAcademicLevel = findViewById(R.id.txtAcademicLevel);
        txtField = findViewById(R.id.txtField);
        txtLanguages = findViewById(R.id.txtLanguages);
        txtGpa = findViewById(R.id.txtGpa);
        txtBudget = findViewById(R.id.txtBudget);
        txtYearOfStudies = findViewById(R.id.txtYearOfStudies);
        txtAdvisorType = findViewById(R.id.txtAdvisorType);

        imgProfilePhoto = findViewById(R.id.imgProfilePhoto);

        userId = getIntent().getStringExtra("userId");

        BackButtonHelper.attachToGoModeSelection(
                this,
                R.id.btnBack,
                userId,
                userFullName,
                userEmail,
                userPhone
        );

        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );

        usersRef = firebaseDb.getReference("users");
        userInfoRef = firebaseDb.getReference("user_info");

        loadUser();
        loadUserInfo();
        loadProfilePhotoFromSupabase();

        btnEditProfile.setOnClickListener(v -> {
            Intent i = new Intent(AccountActivity.this, EditProfileActivity.class);
            i.putExtra("userId", userId);
            i.putExtra("userFullName", userFullName);
            i.putExtra("userEmail", userEmail);
            i.putExtra("userPhone", userPhone);
            startActivity(i);
        });

        btnEditAcademic.setOnClickListener(v -> {
            Intent i = new Intent(AccountActivity.this, EditUserInfoActivity.class);
            i.putExtra("userId", userId);
            startActivity(i);
        });

        imgProfilePhoto.setOnClickListener(v -> showImageOptions());
    }

    private void showImageOptions() {
        String[] options = {"Take photo", "Choose from gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile Photo");

        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, TAKE_PHOTO);
            }

            if (which == 1) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, PICK_IMAGE);
            }
        });

        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {

            if (requestCode == PICK_IMAGE) {
                try {
                    imageUri = data.getData();
                    if (imageUri != null) {
                        // local cache (optional)
                        ProfileImageManager.saveUri(this, userId, imageUri);
                        // upload στο Supabase και αποθήκευση URL στο Firebase
                        uploadToSupabaseAndSaveUrl(imageUri);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (requestCode == TAKE_PHOTO) {
                try {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    if (photo != null) {
                        // local cache (optional)
                        ProfileImageManager.saveBitmap(this, userId, photo);

                        byte[] bytes = ProfileImageManager.bitmapToBytes(photo);
                        if (bytes != null) {
                            uploadBytesToSupabaseAndSaveUrl(bytes);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void uploadToSupabaseAndSaveUrl(Uri uri) {
        if (userId == null || userId.isEmpty()) return;

        new Thread(() -> {
            try {
                java.io.InputStream is = getContentResolver().openInputStream(uri);
                if (is == null) return;
                byte[] bytes = SupabaseStorageHelper.readAllBytes(is);
                is.close();

                uploadBytesToSupabaseAndSaveUrl(bytes);

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(AccountActivity.this,
                                "Error reading image", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void uploadBytesToSupabaseAndSaveUrl(byte[] bytes) {
        if (userId == null || userId.isEmpty()) return;

        String path = "profiles/" + userId + ".jpg";

        String publicUrl = SupabaseStorageHelper.uploadImageBytes(bytes, path);

        if (publicUrl != null) {
            usersRef.child(userId).child("profileImageUrl").setValue(publicUrl)
                    .addOnSuccessListener(unused -> runOnUiThread(() -> {
                        Glide.with(AccountActivity.this)
                                .load(publicUrl)
                                .placeholder(R.drawable.baseline_face_24)
                                .into(imgProfilePhoto);
                        Toast.makeText(AccountActivity.this,
                                "Profile photo updated", Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e -> runOnUiThread(() ->
                            Toast.makeText(AccountActivity.this,
                                    "Failed to save URL", Toast.LENGTH_SHORT).show()));
        } else {
            runOnUiThread(() ->
                    Toast.makeText(AccountActivity.this,
                            "Upload failed", Toast.LENGTH_SHORT).show());
        }
    }

    private void loadUser() {
        if (userId == null || userId.isEmpty()) return;

        usersRef.child(userId).get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) return;

            userFullName = snapshot.child("fullName").getValue(String.class);
            userEmail = snapshot.child("email").getValue(String.class);
            userPhone = snapshot.child("phoneNumber").getValue(String.class);

            txtFullName.setText(userFullName != null ? userFullName : "-");
            txtEmail.setText(userEmail != null ? userEmail : "-");
            txtPhone.setText(userPhone != null ? userPhone : "-");
        });
    }

    private void loadUserInfo() {
        if (userId == null || userId.isEmpty()) return;

        userInfoRef.child(userId).get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) return;

            String university = snapshot.child("university").getValue(String.class);
            String academicLevel = snapshot.child("academicLevel").getValue(String.class);
            String field = snapshot.child("field").getValue(String.class);
            String languages = snapshot.child("languages").getValue(String.class);
            Double gpa = snapshot.child("gpa").getValue(Double.class);
            Double budgetPerYear = snapshot.child("budgetPerYear").getValue(Double.class);
            Integer yearOfStudies = snapshot.child("yearOfStudies").getValue(Integer.class);
            String advisorType = snapshot.child("advisorType").getValue(String.class);

            txtUniversity.setText("University: " + (university != null ? university : "-"));
            txtAcademicLevel.setText("Level: " + (academicLevel != null ? academicLevel : "-"));
            txtField.setText("Field: " + (field != null ? field : "-"));
            txtLanguages.setText("Languages: " + (languages != null ? languages : "-"));
            txtGpa.setText("GPA: " + (gpa != null ? gpa : 0.0));
            txtBudget.setText("Budget per year: " + (budgetPerYear != null ? budgetPerYear : 0.0));
            txtYearOfStudies.setText("Year of studies: " + (yearOfStudies != null ? yearOfStudies : 0));
            txtAdvisorType.setText("Advisor type: " + (advisorType != null ? advisorType : "-"));
        });
    }

    private void loadProfilePhotoFromSupabase() {
        if (userId == null || userId.isEmpty()) return;

        usersRef.child(userId).child("profileImageUrl").get().addOnSuccessListener(snapshot -> {
            String url = snapshot.getValue(String.class);
            if (url != null && !url.isEmpty()) {
                Glide.with(AccountActivity.this)
                        .load(url)
                        .placeholder(R.drawable.baseline_face_24)
                        .error(R.drawable.baseline_face_24)
                        .into(imgProfilePhoto);
            } else {
                imgProfilePhoto.setImageResource(R.drawable.baseline_face_24);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUser();
        loadUserInfo();
        loadProfilePhotoFromSupabase();
    }
}