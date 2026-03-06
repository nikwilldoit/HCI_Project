package com.example.phasmatic.ui.Forum;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.phasmatic.R;
import com.example.phasmatic.data.model.ForumReview;
import com.example.phasmatic.ui.BackButtonHelper;
import com.example.phasmatic.ui.Profile_Menu.ProfileMenuHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewReviewActivity extends AppCompatActivity {

    private Spinner spnType, spnUniversity;
    private AutoCompleteTextView dropCountry;
    private EditText edtText, edtRating;
    private Button btnSave;
    private ImageButton btnBack;
    private ImageView imgProfile;

    private String userId, userFullName, userEmail, userPhone;
    private DatabaseReference forumRef, countriesRef, universitiesRef;
    private ProfileMenuHelper profileMenuHelper;

    private ArrayAdapter<String> countryAdapter, uniAdapter;
    private final List<String> countryList = new ArrayList<>();
    private final List<String> uniList = new ArrayList<>();

    private String selectedCountry = null;
    private String selectedUniversity = null;

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
        dropCountry = findViewById(R.id.dropCountry);
        spnUniversity = findViewById(R.id.spnUniversity);
        edtRating = findViewById(R.id.edtRating);
        edtText = findViewById(R.id.edtText);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        imgProfile = findViewById(R.id.imgProfile);

        profileMenuHelper = new ProfileMenuHelper(this, userId, userFullName, userEmail, userPhone);
        imgProfile.setOnClickListener(v -> profileMenuHelper.showProfileMenu(v));

        BackButtonHelper.attach(this, R.id.btnBack);

        List<String> types = Arrays.asList("Erasmus", "Master");
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                this, R.layout.item_dropdown_dark, types);
        typeAdapter.setDropDownViewResource(R.layout.item_dropdown_dark);
        spnType.setAdapter(typeAdapter);

        FirebaseDatabase db = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app");
        forumRef = db.getReference("forum_reviews");
        countriesRef = db.getReference("countries");
        universitiesRef = db.getReference("universities");

        setupCountryUniversityDropdowns();

        btnSave.setOnClickListener(v -> saveReview());
    }

    private void setupCountryUniversityDropdowns() {
        countryAdapter = new ArrayAdapter<>(
                this, R.layout.item_dropdown_dark, countryList);
        countryAdapter.setDropDownViewResource(R.layout.item_dropdown_dark);
        dropCountry.setAdapter(countryAdapter);
        dropCountry.setThreshold(0);
        dropCountry.setOnClickListener(v -> dropCountry.showDropDown());

        uniAdapter = new ArrayAdapter<>(
                this, R.layout.item_dropdown_dark, uniList);
        uniAdapter.setDropDownViewResource(R.layout.item_dropdown_dark);
        spnUniversity.setAdapter(uniAdapter);

        countriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                countryList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String name = child.child("name").getValue(String.class);
                    if (name != null) countryList.add(name);
                }
                countryAdapter.notifyDataSetChanged();
            }

            @Override public void onCancelled(DatabaseError error) {}
        });

        dropCountry.setOnItemClickListener((parent, view, position, id) -> {
            selectedCountry = parent.getItemAtPosition(position).toString();

            selectedUniversity = null;
            uniList.clear();
            uniAdapter.notifyDataSetChanged();

            loadUniversitiesForCountry(selectedCountry);
        });

        spnUniversity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < uniList.size()) {
                    selectedUniversity = uniList.get(position);
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {
                selectedUniversity = null;
            }
        });
    }

    private void loadUniversitiesForCountry(String countryName) {
        universitiesRef.orderByChild("country")
                .equalTo(countryName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot snapshot) {
                        uniList.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            String uniCountry = child.child("country").getValue(String.class);
                            String uniName = child.child("name").getValue(String.class);
                            if (uniCountry != null
                                    && uniCountry.equalsIgnoreCase(countryName)
                                    && uniName != null) {
                                uniList.add(uniName);
                            }
                        }
                        uniAdapter.notifyDataSetChanged();
                    }
                    @Override public void onCancelled(DatabaseError error) {}
                });
    }

    private void saveReview() {
        String typeUi = (String) spnType.getSelectedItem();
        String type = "erasmus";
        if ("Master".equalsIgnoreCase(typeUi)) type = "master";

        String country = selectedCountry != null ? selectedCountry : "";
        String university = selectedUniversity != null ? selectedUniversity : "";
        String text = edtText.getText().toString().trim();

        int ratingInt;
        try {
            ratingInt = Integer.parseInt(edtRating.getText().toString().trim());
        } catch (NumberFormatException e) {
            ratingInt = 0;
        }

        if (country.isEmpty() || university.isEmpty() || text.isEmpty()
                || ratingInt < 1 || ratingInt > 5) {
            Toast.makeText(this, "Fill all fields and rating 1-5", Toast.LENGTH_SHORT).show();
            return;
        }

        float rating = ratingInt;

        String id = forumRef.push().getKey();
        if (id == null) {
            Toast.makeText(this, "Error creating review id", Toast.LENGTH_SHORT).show();
            return;
        }

        long likes = 0L;
        long timestamp = System.currentTimeMillis();

        ForumReview review = new ForumReview(
                id,
                userId,
                userFullName,
                type,
                university,
                country,
                text,
                rating,
                timestamp,
                likes
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
