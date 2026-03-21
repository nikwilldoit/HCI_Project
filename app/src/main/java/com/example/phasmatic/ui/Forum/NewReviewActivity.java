package com.example.phasmatic.ui.Forum;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
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
import android.graphics.Bitmap;
import com.example.phasmatic.extras.ProfileImageManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewReviewActivity extends AppCompatActivity {

    private Spinner spnType, spnUniversity;
    private AutoCompleteTextView dropCountry;
    private EditText edtText, edtRating;
    private Button btnSave, btnVoice;
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
        btnVoice = findViewById(R.id.btnVoice);

        profileMenuHelper = new ProfileMenuHelper(this, userId, userFullName, userEmail, userPhone);
        imgProfile.setOnClickListener(v -> profileMenuHelper.showProfileMenu(v));
        loadProfilePhoto();

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

        btnVoice.setOnClickListener(v -> startSpeechRecognizer());
    }

    private void loadProfilePhoto() {
        Bitmap bitmap = ProfileImageManager.loadBitmap(this, userId);
        if (bitmap != null) {
            imgProfile.setImageBitmap(bitmap);
        } else {
            imgProfile.setImageResource(R.drawable.baseline_face_24);
        }
    }

    private void startSpeechRecognizer() {
        int REQUEST_SPEECH_RECOGNIZER = 3000;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "el-GR");

        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "el-GR");


        try {
            startActivityForResult(intent, REQUEST_SPEECH_RECOGNIZER);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Η αναγνώριση φωνής δεν υποστηρίζεται στη συσκευή σας", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int REQUEST_SPEECH_RECOGNIZER = 3000;
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("DEMO-REQUESTCODE", Integer.toString(requestCode));
        Log.i("DEMO-RESULTCODE", Integer.toString(resultCode));

        if (requestCode == REQUEST_SPEECH_RECOGNIZER && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            edtText.setText(text.get(0));

            Log.i("DEMO-ANSWER", text.get(0));

        } else {
            System.out.println("Recognizer API error");
        }
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
        String createdAt = new java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                java.util.Locale.getDefault()
        ).format(new java.util.Date());

        ForumReview review = new ForumReview(
                id,
                userId,
                userFullName,
                type,
                university,
                country,
                text,
                rating,
                likes,
                createdAt,
                0
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
