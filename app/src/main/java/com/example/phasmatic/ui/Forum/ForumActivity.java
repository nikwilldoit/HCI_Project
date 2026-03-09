package com.example.phasmatic.ui.Forum;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phasmatic.R;
import com.example.phasmatic.data.model.ForumReview;
import com.example.phasmatic.ui.BackButtonHelper;
import com.example.phasmatic.ui.Profile_Menu.ProfileMenuHelper;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ForumActivity extends AppCompatActivity {

    private RecyclerView rvReviews;
    private ForumAdapter adapter;

    private ImageButton btnBack;
    private ImageView imgProfile;
    private MaterialButtonToggleGroup toggleType;
    private AutoCompleteTextView dropCountry;
    private Spinner spnUniversity;

    private final List<ForumReview> reviews = new ArrayList<>();
    private final List<ForumReview> allReviews = new ArrayList<>();
    private final List<ForumReview> filteredReviews = new ArrayList<>();

    private DatabaseReference forumRef;

    private String userId, userFullName, userEmail, userPhone;
    private String selectedType = null;
    private String selectedCountry = null;
    private String selectedUniversity = null;

    private ProfileMenuHelper profileMenuHelper;

    private DatabaseReference countriesRef, universitiesRef;

    private ArrayAdapter<String> countryAdapter, uniAdapter;
    private final List<String> countryList = new ArrayList<>();
    private final List<String> uniList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userFullName = intent.getStringExtra("userFullName");
        userEmail = intent.getStringExtra("userEmail");
        userPhone = intent.getStringExtra("userPhone");

        imgProfile = findViewById(R.id.imgProfile);
        profileMenuHelper = new ProfileMenuHelper(this, userId, userFullName, userEmail, userPhone);
        imgProfile.setOnClickListener(v -> profileMenuHelper.showProfileMenu(v));

        BackButtonHelper.attachToGoModeSelection(this, R.id.btnBack, userId, userFullName, userEmail, userPhone);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvReviews = findViewById(R.id.rvReviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ForumAdapter(filteredReviews, userId, review -> {
            Intent i = new Intent(ForumActivity.this, ReviewDetailActivity.class);

            i.putExtra("userId", userId);
            i.putExtra("userFullName", userFullName);
            i.putExtra("userEmail", userEmail);
            i.putExtra("userPhone", userPhone);
            i.putExtra("modeType", "master");
            i.putExtra("type", review.type);
            i.putExtra("university", review.university);
            i.putExtra("country", review.country);
            i.putExtra("userName", review.user_name);
            i.putExtra("rating", review.rating);
            i.putExtra("text", review.text);
            i.putExtra("likes", review.likes);
            i.putExtra("reviewId", review.id);
            i.putExtra("Views",review.views);

            startActivity(i);

        });
        rvReviews.setAdapter(adapter);


        toggleType = findViewById(R.id.toggleType);
        dropCountry = findViewById(R.id.dropCountry);
        spnUniversity = findViewById(R.id.spnUniversity);

        FirebaseDatabase db = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app");
        forumRef = db.getReference("forum_reviews");
        countriesRef = db.getReference("countries");
        universitiesRef = db.getReference("universities");

        setupDropdowns();
        setupTypeFilter();
        loadReviews();

        findViewById(R.id.fabAddReview).setOnClickListener(v -> openAddReview());
    }

    private void setupDropdowns() {

        countryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                countryList
        );
        dropCountry.setAdapter(countryAdapter);
        dropCountry.setThreshold(0);
        dropCountry.setOnClickListener(v -> dropCountry.showDropDown());

        uniAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                uniList
        );
        uniAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
            applyFilters();
        });

        spnUniversity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position >= 0 && position < uniList.size()) {
                    selectedUniversity = uniList.get(position);
                    applyFilters();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedUniversity = null;
                applyFilters();
            }
        });
    }

    private void loadUniversitiesForCountry(String countryName) {

        universitiesRef
                .orderByChild("country")
                .equalTo(countryName)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

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

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }

    private void setupTypeFilter() {
        toggleType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) {
                selectedType = null;
                applyFilters();
                return;
            }

            if (checkedId == R.id.btnErasmus) selectedType = "erasmus";
            else if (checkedId == R.id.btnMaster) selectedType = "master";

            applyFilters();
        });
    }

    private void loadReviews() {
        forumRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                reviews.clear();
                allReviews.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    ForumReview r = child.getValue(ForumReview.class);
                    if (r != null) {
                        reviews.add(0, r);
                        allReviews.add(0, r);
                    }
                }
                applyFilters();
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void openAddReview() {
        Intent i = new Intent(this, NewReviewActivity.class);
        i.putExtra("userId", userId);
        i.putExtra("userFullName", userFullName);
        startActivity(i);
    }

    private void applyFilters() {
        filteredReviews.clear();
        for (ForumReview r : allReviews) {
            boolean ok = true;
            if (selectedType != null && !selectedType.equals(r.type)) ok = false;
            if (selectedCountry != null && !selectedCountry.equals(r.country)) ok = false;
            if (selectedUniversity != null && !selectedUniversity.equals(r.university)) ok = false;
            if (ok) filteredReviews.add(r);
        }
        adapter.notifyDataSetChanged();
    }
}
