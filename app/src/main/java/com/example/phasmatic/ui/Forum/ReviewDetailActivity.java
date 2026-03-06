package com.example.phasmatic.ui.Forum;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phasmatic.R;
import com.example.phasmatic.ui.BackButtonHelper;
import com.example.phasmatic.ui.Profile_Menu.ProfileMenuHelper;

public class ReviewDetailActivity extends AppCompatActivity {

    ImageView imgProfile;
    ImageButton btnBack;
    private ProfileMenuHelper profileMenuHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);

        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageView imgProfile = findViewById(R.id.imgProfile);
        TextView txtTitle = findViewById(R.id.txtTitle);
        TextView txtUser = findViewById(R.id.txtUser);
        TextView txtCountryUni = findViewById(R.id.txtCountryUni);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        TextView txtText = findViewById(R.id.txtText);
        TextView txtLikes = findViewById(R.id.txtLikes);

        String type = getIntent().getStringExtra("type");
        String university = getIntent().getStringExtra("university");
        String country = getIntent().getStringExtra("country");
        String userName = getIntent().getStringExtra("userName");
        float rating = getIntent().getFloatExtra("rating", 0f);
        String text = getIntent().getStringExtra("text");
        int likes = getIntent().getIntExtra("likes", 0);

        String userId = getIntent().getStringExtra("userId");
        String userFullName = getIntent().getStringExtra("userFullName");
        String userEmail = getIntent().getStringExtra("userEmail");
        String userPhone = getIntent().getStringExtra("userPhone");

        String title = ("erasmus".equals(type) ? "Erasmus · " : "Master · ") + university;
        txtTitle.setText(title);
        txtUser.setText("by " + userName);
        txtCountryUni.setText(country + " · " + university);
        ratingBar.setRating(rating);
        txtText.setText(text);
        txtLikes.setText(likes + " likes");

        BackButtonHelper.attachToGoForum(
                this,
                R.id.btnBack,
                userId,
                userFullName,
                userEmail,
                userPhone
        );

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userFullName = intent.getStringExtra("userFullName");
        userEmail = intent.getStringExtra("userEmail");
        userPhone = intent.getStringExtra("userPhone");

        imgProfile = findViewById(R.id.imgProfile);

        profileMenuHelper = new ProfileMenuHelper(
                this,
                userId,
                userFullName,
                userEmail,
                userPhone
        );

        imgProfile.setOnClickListener(v -> profileMenuHelper.showProfileMenu(v));

        BackButtonHelper.attachToGoForum(
                this,
                R.id.btnBack,
                userId,
                userFullName,
                userEmail,
                userPhone
        );
    }
}
