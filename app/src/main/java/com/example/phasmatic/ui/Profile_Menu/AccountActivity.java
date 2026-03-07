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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.phasmatic.R;
import com.example.phasmatic.ui.BackButtonHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

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
    private StorageReference storageRef;

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

        userId = getIntent().getStringExtra("userId");

        BackButtonHelper.attachToGoModeSelection(
                this,
                R.id.btnBack,
                userId,
                null,
                null,
                null
        );

        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        usersRef = firebaseDb.getReference("users");
        userInfoRef = firebaseDb.getReference("user_info");

        loadUser();
        loadUserInfo();

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

        imgProfilePhoto = findViewById(R.id.imgProfilePhoto);

        storageRef = FirebaseStorage.getInstance().getReference("profile_images");

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

        if(resultCode == RESULT_OK){

            if(requestCode == PICK_IMAGE){

                imageUri = data.getData();
                imgProfilePhoto.setImageURI(imageUri);

                uploadImage();

            }

            if(requestCode == TAKE_PHOTO){

                Bitmap photo = (Bitmap) data.getExtras().get("data");

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG,100,baos);

                byte[] dataBytes = baos.toByteArray();

                uploadCameraImage(dataBytes);

            }

        }
    }

    private void uploadImage(){

        StorageReference fileRef = storageRef.child(userId + ".jpg");

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {

                            String url = uri.toString();

                            usersRef.child(userId).child("profileImageUrl").setValue(url);

                        }));
    }
    private void uploadCameraImage(byte[] data){

        StorageReference fileRef = storageRef.child(userId + ".jpg");

        fileRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot ->
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {

                            String url = uri.toString();

                            usersRef.child(userId).child("profileImageUrl").setValue(url);

                        }));
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
            txtYearOfStudies.setText("Year of studies: " +
                    (yearOfStudies != null ? yearOfStudies : 0));
            txtAdvisorType.setText("Advisor type: " + (advisorType != null ? advisorType : "-"));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUser();
        loadUserInfo();
    }
}
