package com.example.phasmatic.ui.Forum;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phasmatic.R;
import com.example.phasmatic.data.model.ReviewComment;
import com.example.phasmatic.ui.BackButtonHelper;
import com.example.phasmatic.ui.Profile_Menu.ProfileMenuHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewDetailActivity extends AppCompatActivity {

    ImageView imgProfile;
    ImageButton btnBack, btnSendComment;
    private ProfileMenuHelper profileMenuHelper;

    TextView txtTitle, txtUser, txtCountryUni, txtText, txtLikes;
    RatingBar ratingBar;

    RecyclerView rvComments;
    EditText edtComment;

    String userId, userFullName, userEmail, userPhone;
    long reviewId;

    CommentsAdapter commentsAdapter;
    List<ReviewComment> comments = new ArrayList<>();

    private DatabaseReference reviewCommentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);

        btnBack = findViewById(R.id.btnBack);
        imgProfile = findViewById(R.id.imgProfile);
        txtTitle = findViewById(R.id.txtTitle);
        txtUser = findViewById(R.id.txtUser);
        txtCountryUni = findViewById(R.id.txtCountryUni);
        ratingBar = findViewById(R.id.ratingBar);
        txtText = findViewById(R.id.txtText);
        txtLikes = findViewById(R.id.txtLikes);
        rvComments = findViewById(R.id.rvComments);
        edtComment = findViewById(R.id.edtComment);
        btnSendComment = findViewById(R.id.btnSendComment);

        String type = getIntent().getStringExtra("type");
        String university = getIntent().getStringExtra("university");
        String country = getIntent().getStringExtra("country");
        String userName = getIntent().getStringExtra("userName");
        float rating = getIntent().getFloatExtra("rating", 0f);
        String text = getIntent().getStringExtra("text");
        int likes = getIntent().getIntExtra("likes", 0);
        reviewId = getIntent().getLongExtra("reviewId", -1);

        userId = getIntent().getStringExtra("userId");
        userFullName = getIntent().getStringExtra("userFullName");
        userEmail = getIntent().getStringExtra("userEmail");
        userPhone = getIntent().getStringExtra("userPhone");

        String title = ("erasmus".equals(type) ? "Erasmus · " : "Master · ") + university;
        txtTitle.setText(title);
        txtUser.setText("by " + userName);
        txtCountryUni.setText(country + " · " + university);
        ratingBar.setRating(rating);
        txtText.setText(text);
        txtLikes.setText(likes + " likes");

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

        FirebaseDatabase db = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        reviewCommentsRef = db.getReference("review_comments");

        rvComments.setLayoutManager(new LinearLayoutManager(this));
        commentsAdapter = new CommentsAdapter(comments);
        rvComments.setAdapter(commentsAdapter);

        loadComments();

        btnSendComment.setOnClickListener(v -> {
            String cText = edtComment.getText().toString().trim();
            if (cText.isEmpty()) {
                return;
            }
            postComment(cText);
        });
    }

    private void loadComments() {
        if (reviewId <= 0) return;

        reviewCommentsRef
                .orderByChild("review_id")
                .equalTo(reviewId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        comments.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            ReviewComment c = child.getValue(ReviewComment.class);
                            if (c != null) {
                                c.id = child.getKey();
                                comments.add(c);
                            }
                        }
                        commentsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
    }

    private void postComment(String commentText) {
        if (reviewId <= 0) return;

        btnSendComment.setEnabled(false);

        String key = reviewCommentsRef.push().getKey();
        if (key == null) {
            btnSendComment.setEnabled(true);
            Toast.makeText(this, "Error creating comment", Toast.LENGTH_SHORT).show();
            return;
        }

        String createdAt = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
        ).format(new Date());

        ReviewComment c = new ReviewComment(
                key,
                reviewId,
                userId,
                userFullName,
                commentText,
                createdAt
        );

        reviewCommentsRef.child(key).setValue(c)
                .addOnCompleteListener(task -> {
                    btnSendComment.setEnabled(true);
                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Failed to add comment", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    comments.add(c);
                    commentsAdapter.notifyItemInserted(comments.size() - 1);
                    rvComments.scrollToPosition(comments.size() - 1);
                    edtComment.setText("");

                    Toast.makeText(this, "Comment added", Toast.LENGTH_SHORT).show();
                });
    }
}
