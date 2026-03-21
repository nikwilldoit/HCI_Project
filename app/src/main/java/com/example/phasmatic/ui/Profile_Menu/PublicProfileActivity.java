package com.example.phasmatic.ui.Profile_Menu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phasmatic.R;
import com.example.phasmatic.extras.ProfileImageManager;
import com.example.phasmatic.ui.BackButtonHelper;
import com.google.firebase.database.FirebaseDatabase;

public class PublicProfileActivity extends AppCompatActivity {

    private String userId;

    private ImageButton btnBack, btnChat;
    private ImageView imgProfilePhoto;
    private TextView txtFullName, txtEmail, txtPhone;
    private String userEmail;
    private String userPhone, userFullName;
    private TextView txtUniversity, txtAcademicLevel, txtField,
            txtLanguages, txtGpa, txtBudget, txtYearOfStudies, txtAdvisorType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profile);


        Intent intent = getIntent();
        userId = getIntent().getStringExtra("userId");
        btnBack = findViewById(R.id.btnBack);
        btnChat = findViewById(R.id.btnChat);
        imgProfilePhoto = findViewById(R.id.imgProfilePhoto);
        txtFullName = findViewById(R.id.txtFullName);
        userEmail = intent.getStringExtra("userEmail");
        userPhone = intent.getStringExtra("userPhone");
        userFullName = intent.getStringExtra("userFullName");
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

        btnBack.setOnClickListener(v -> onBackPressed());

        if (userId != null && !userId.isEmpty()) {
            loadUserBasic();
            loadUserInfo();
            Bitmap bitmap = ProfileImageManager.loadBitmap(this, userId);
            if (bitmap != null) {
                imgProfilePhoto.setImageBitmap(bitmap);
            } else {
                imgProfilePhoto.setImageResource(R.drawable.baseline_face_24);
            }
        }

        btnChat.setOnClickListener(v-> onBackPressed());
        BackButtonHelper.attachToGoChat(this, R.id.btnChat, userId, userFullName, userEmail, userPhone);
    }


    private void loadUserBasic() {
        FirebaseDatabase db = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );

        db.getReference("users").child(userId).get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) return;

                    String fullName = snapshot.child("fullName").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String phone = snapshot.child("phoneNumber").getValue(String.class);

                    txtFullName.setText(fullName != null ? fullName : "-");
                    txtEmail.setText(email != null ? email : "-");
                    txtPhone.setText(phone != null ? phone : "-");
                });
    }

    private void loadUserInfo() {
        FirebaseDatabase db = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );

        db.getReference("user_info").child(userId).get()
                .addOnSuccessListener(snapshot -> {
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
}
