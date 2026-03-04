package com.example.phasmatic.ui.Profile_Menu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phasmatic.R;
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
