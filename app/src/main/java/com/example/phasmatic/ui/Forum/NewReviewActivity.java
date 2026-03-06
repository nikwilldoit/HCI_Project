package com.example.phasmatic.ui.Forum;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.phasmatic.R;
import com.example.phasmatic.data.model.ForumReview;
import com.example.phasmatic.ui.BackButtonHelper;
import com.example.phasmatic.ui.Profile_Menu.ProfileMenuHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class NewReviewActivity extends AppCompatActivity {

    private Spinner spnType;
    private EditText edtUniversity, edtCountry, edtText;
    private RatingBar ratingBar;
    private Button btnSave;
    private ImageButton btnBack;

    private ImageView imgProfile;

    private String userId, userFullName, userEmail, userPhone;
    private DatabaseReference forumRef;
    private ProfileMenuHelper profileMenuHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_review);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userFullName = intent.getStringExtra("userFullName");
        userEmail = intent.getStringExtra("userEmail");
        userPhone = intent.getStringExtra("userPhone");

        spnType = findViewById(R.id.spnType);
        edtUniversity = findViewById(R.id.edtUniversity);
        edtCountry = findViewById(R.id.edtCountry);
        edtText = findViewById(R.id.edtText);
        ratingBar = findViewById(R.id.ratingBar);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        imgProfile = findViewById(R.id.imgProfile);

        profileMenuHelper = new ProfileMenuHelper(
                this,
                userId,
                userFullName,
                userEmail,
                userPhone
        );

        imgProfile.setOnClickListener(v -> profileMenuHelper.showProfileMenu(v));

        BackButtonHelper.attach(
                this,
                R.id.btnBack
        );

        List<String> types = Arrays.asList("Erasmus", "Master");
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                types
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnType.setAdapter(typeAdapter);

        FirebaseDatabase db = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app");
        forumRef = db.getReference("forum_reviews");

        btnSave.setOnClickListener(v -> saveReview());
    }

    private void saveReview() {
        String typeUi = (String) spnType.getSelectedItem();
        String type = "erasmus";
        if ("Master".equalsIgnoreCase(typeUi)) type = "master";

        String university = edtUniversity.getText().toString().trim();
        String country = edtCountry.getText().toString().trim();
        String text = edtText.getText().toString().trim();
        float rating = ratingBar.getRating();

        if (university.isEmpty() || country.isEmpty() || text.isEmpty() || rating == 0f) {
            Toast.makeText(this, "Fill all fields and rating", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = forumRef.push().getKey();
        if (id == null) {
            Toast.makeText(this, "Error creating review id", Toast.LENGTH_SHORT).show();
            return;
        }

        ForumReview review = new ForumReview(
                id,
                userId,
                userFullName,
                type,
                university,
                country,
                text,
                rating,
                System.currentTimeMillis()
        );

        forumRef.child(id)
                .setValue(review)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Review posted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}

