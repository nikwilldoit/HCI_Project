package com.example.phasmatic.ui.Forum;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;

public class ForumActivity extends AppCompatActivity {

    private RecyclerView rvReviews;
    private ForumAdapter adapter;

    private ImageButton btnBack;

    private ImageView imgProfile;
    private final List<ForumReview> reviews = new ArrayList<>();
    private DatabaseReference forumRef;

    private String userId, userFullName, userEmail, userPhone;

    private ProfileMenuHelper profileMenuHelper;

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

        profileMenuHelper = new ProfileMenuHelper(
                this,
                userId,
                userFullName,
                userEmail,
                userPhone
        );

        imgProfile.setOnClickListener(v -> profileMenuHelper.showProfileMenu(v));

        //btnBack = findViewById(R.id.btnBack);

        BackButtonHelper.attachToGoModeSelection(
                this,
                R.id.btnBack,
                userId,
                userFullName,
                userEmail,
                userPhone
        );

        rvReviews = findViewById(R.id.rvReviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ForumAdapter(reviews);
        rvReviews.setAdapter(adapter);

        FirebaseDatabase db = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app");
        forumRef = db.getReference("forum_reviews");

        loadReviews();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.fabAddReview).setOnClickListener(v -> openAddReview());
    }

    private void loadReviews() {
        forumRef.orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        reviews.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            ForumReview r = child.getValue(ForumReview.class);
                            if (r != null) reviews.add(0, r);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override public void onCancelled(DatabaseError error) {}
                });
    }

    private void openAddReview() {
        Intent i = new Intent(this, NewReviewActivity.class);
        i.putExtra("userId", userId);
        i.putExtra("userFullName", userFullName);
        startActivity(i);
    }
}
