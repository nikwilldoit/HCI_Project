package com.example.phasmatic.ui.Profile_Menu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phasmatic.R;
import com.example.phasmatic.extras.ProfileImageManager;
import com.example.phasmatic.ui.Chat.ChatActivity;
import com.google.firebase.database.FirebaseDatabase;

public class PublicProfileActivity extends AppCompatActivity {

    // uid tou user pou vlepeis (other)
    private String profileUid;
    // uid tou logged-in user
    private String currentUserId;
    // full name tou user pou vlepeis
    private String loadedFullName = null;

    // plirofories tou LOGGED-IN user (prepei na erxontai me alla extras)
    private String userFullName, userEmail, userPhone;

    private ImageButton btnBack, btnChat;
    private ImageView imgProfilePhoto;
    private TextView txtFullName, txtEmail, txtPhone;
    private TextView txtUniversity, txtAcademicLevel, txtField,
            txtLanguages, txtGpa, txtBudget, txtYearOfStudies, txtAdvisorType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profile);

        Intent intent = getIntent();

        // PROSOXH: profileUid = o allos, currentUserId = logged-in
        profileUid    = intent.getStringExtra("userId");        // other user
        currentUserId = intent.getStringExtra("currentUserId"); // logged-in

        // info tou logged-in user gia menu κλπ
        userFullName = intent.getStringExtra("userFullName");
        userEmail    = intent.getStringExtra("userEmail");
        userPhone    = intent.getStringExtra("userPhone");

        btnBack         = findViewById(R.id.btnBack);
        btnChat         = findViewById(R.id.btnChat);
        imgProfilePhoto = findViewById(R.id.imgProfilePhoto);
        txtFullName     = findViewById(R.id.txtFullName);
        txtEmail        = findViewById(R.id.txtEmail);
        txtPhone        = findViewById(R.id.txtPhone);
        txtUniversity   = findViewById(R.id.txtUniversity);
        txtAcademicLevel= findViewById(R.id.txtAcademicLevel);
        txtField        = findViewById(R.id.txtField);
        txtLanguages    = findViewById(R.id.txtLanguages);
        txtGpa          = findViewById(R.id.txtGpa);
        txtBudget       = findViewById(R.id.txtBudget);
        txtYearOfStudies= findViewById(R.id.txtYearOfStudies);
        txtAdvisorType  = findViewById(R.id.txtAdvisorType);

        btnBack.setOnClickListener(v -> onBackPressed());

        if (profileUid != null && !profileUid.isEmpty()) {
            loadUserBasic();
            loadUserInfo();
            Bitmap bitmap = ProfileImageManager.loadBitmap(this, profileUid);
            if (bitmap != null) imgProfilePhoto.setImageBitmap(bitmap);
            else imgProfilePhoto.setImageResource(R.drawable.baseline_face_24);
        }


        btnChat.setOnClickListener(v -> {
            android.util.Log.d("PublicProfile", "Chat clicked, currentUserId=" + currentUserId + ", profileUid=" + profileUid);
            if (currentUserId == null || profileUid == null) return;

            Intent i = new Intent(PublicProfileActivity.this, ChatActivity.class);
            i.putExtra("userId", currentUserId);   //logged-in
            i.putExtra("otherUid", profileUid);    //allos

            String nameForChat = loadedFullName != null
                    ? loadedFullName
                    : txtFullName.getText().toString().trim();
            i.putExtra("otherName", nameForChat);
            i.putExtra("userEmail", userEmail);
            i.putExtra("userPhone", userPhone);
            startActivity(i);
        });

    }

    private void loadUserBasic() {
        FirebaseDatabase.getInstance(
                        "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
                ).getReference("users").child(profileUid).get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) return;

                    String fullName = snapshot.child("fullName").getValue(String.class);
                    String email    = snapshot.child("email").getValue(String.class);
                    String phone    = snapshot.child("phoneNumber").getValue(String.class);

                    loadedFullName = fullName;
                    txtFullName.setText(fullName != null ? fullName : "-");
                    txtEmail.setText(email != null ? email : "-");
                    txtPhone.setText(phone != null ? phone : "-");
                });
    }

    private void loadUserInfo() {
        FirebaseDatabase.getInstance(
                        "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
                ).getReference("user_info").child(profileUid).get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) return;

                    String university    = snapshot.child("university").getValue(String.class);
                    String academicLevel = snapshot.child("academicLevel").getValue(String.class);
                    String field         = snapshot.child("field").getValue(String.class);
                    String languages     = snapshot.child("languages").getValue(String.class);
                    Double gpa           = snapshot.child("gpa").getValue(Double.class);
                    Double budgetPerYear = snapshot.child("budgetPerYear").getValue(Double.class);
                    Integer yearOfStudies = snapshot.child("yearOfStudies").getValue(Integer.class);
                    String advisorType   = snapshot.child("advisorType").getValue(String.class);

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
