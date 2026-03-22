package com.example.phasmatic.ui.Forum;

import android.content.Intent;
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
import com.example.phasmatic.data.model.User;
import com.example.phasmatic.data.model.UserInfo;
import com.example.phasmatic.ui.BackButtonHelper;
import com.example.phasmatic.ui.Profile_Menu.AccountActivity;
import com.example.phasmatic.ui.Profile_Menu.ProfileMenuHelper;
import com.example.phasmatic.ui.Profile_Menu.PublicProfileActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.graphics.Bitmap;
import com.example.phasmatic.extras.ProfileImageManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import java.util.Locale;
import java.util.Map;

public class ReviewDetailActivity extends AppCompatActivity {

    ImageView imgProfile;
    ImageButton btnBack, btnSendComment;
    private ProfileMenuHelper profileMenuHelper;

    TextView txtTitle, txtUser, txtCountryUni, txtText, txtLikes;
    RatingBar ratingBar;

    RecyclerView rvComments;
    EditText edtComment;

    String userId, userFullName, userEmail, userPhone;
    String reviewId;

    CommentsAdapter commentsAdapter;
    private int viewCounter = 0;
    private DatabaseReference reviewViewsRef;

    List<ReviewComment> comments = new ArrayList<>();

    private DatabaseReference reviewCommentsRef;
    private DatabaseReference usersRef;
    private DatabaseReference userInfoRef;

    private final Map<String, String> userNameMap = new HashMap<>();
    private final Map<String, String> userAcademicMap = new HashMap<>();

    int commentsCount = 0;
    private DatabaseReference forumRef;


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

        userId = getIntent().getStringExtra("userId");
        userFullName = getIntent().getStringExtra("userFullName");
        userEmail = getIntent().getStringExtra("userEmail");
        userPhone = getIntent().getStringExtra("userPhone");
        reviewId = getIntent().getStringExtra("reviewId");

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
        loadProfilePhoto();

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
        usersRef = db.getReference("users");
        userInfoRef = db.getReference("user_info");

        forumRef = db.getReference("forum_reviews");
        commentsCount = getIntent().getIntExtra("comments", 0);

        reviewViewsRef = db.getReference("review_views");
        if (reviewId != null && !reviewId.isEmpty()
                && userId != null && !userId.isEmpty()) {
            registerView();
        }


        rvComments.setLayoutManager(new LinearLayoutManager(this));
        commentsAdapter = new CommentsAdapter(
                comments,
                userNameMap,
                userAcademicMap,
                comment -> {
                    Intent i = new Intent(ReviewDetailActivity.this, PublicProfileActivity.class);
                    //currentUserId = o xrhsths pou einai logged-in (userId tou ReviewDetailActivity)
                    i.putExtra("currentUserId", userId);
                    //userId = o xrhsths tou comment pou vlepeis (profileUid sto PublicProfile)
                    i.putExtra("userId", comment.user_id);

                    //plirofories tou logged-in gia menu/profile
                    i.putExtra("userFullName", userFullName);
                    i.putExtra("userEmail", userEmail);
                    i.putExtra("userPhone", userPhone);

                    startActivity(i);
                }
        );
        rvComments.setAdapter(commentsAdapter);

        loadAllUsersAndInfoThenComments();

        btnSendComment.setOnClickListener(v -> {
            String cText = edtComment.getText().toString().trim();
            if (cText.isEmpty()) return;
            postComment(cText);
        });
    }

    private void loadProfilePhoto() {
        Bitmap bitmap = ProfileImageManager.loadBitmap(this, userId);
        if (bitmap != null) {
            imgProfile.setImageBitmap(bitmap);
        } else {
            imgProfile.setImageResource(R.drawable.baseline_face_24);
        }
    }

    private void registerView() {
        String viewKey = reviewId + "_" + userId;
        DatabaseReference viewRef = reviewViewsRef.child(viewKey);

        viewRef.get().addOnSuccessListener(snap -> {
            if (snap.exists()) return; //to exei dei hdh o xrhsts

            String viewedAt = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
            ).format(new Date());

            viewRef.child("review_id").setValue(reviewId);
            viewRef.child("user_id").setValue(userId);
            viewRef.child("viewed_at").setValue(viewedAt);

            //aujhsh counter sto forum_reviews
            forumRef.child(reviewId).child("views")
                    .get()
                    .addOnSuccessListener(countSnap -> {
                        Long current = countSnap.getValue(Long.class);
                        long newVal = (current != null ? current : 0) + 1;
                        forumRef.child(reviewId).child("views").setValue(newVal);
                    });
        });
    }


    private void loadAllUsersAndInfoThenComments() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userSnap) {
                userNameMap.clear();
                for (DataSnapshot child : userSnap.getChildren()) {
                    User u = child.getValue(User.class);
                    if (u != null) {
                        userNameMap.put(child.getKey(), u.getFullName());
                    }
                }

                userInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot infoSnap) {
                        userAcademicMap.clear();
                        for (DataSnapshot child : infoSnap.getChildren()) {
                            UserInfo ui = child.getValue(UserInfo.class);
                            if (ui != null) {
                                String uid = ui.getUserId();
                                if (uid == null) uid = child.getKey();

                                String uni = ui.getUniversity() != null ? ui.getUniversity() : "-";
                                String level = ui.getAcademicLevel() != null ? ui.getAcademicLevel() : "";
                                String field = ui.getField() != null ? ui.getField() : "";

                                String academic = "";
                                if (!field.isEmpty()) academic = field + " · " + uni;
                                else academic = uni;
                                if (!level.isEmpty()) academic = level + " · " + academic;

                                userAcademicMap.put(uid, academic);
                            }
                        }

                        loadComments();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) { }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) { }
        });
    }

    private void loadComments() {
        if (reviewId == null || reviewId.isEmpty()) return;

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

                        commentsCount = comments.size();
                        if (reviewId != null) {
                            forumRef.child(reviewId).child("comments")
                                    .setValue(commentsCount);
                        }

                        commentsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) { }
                });
    }


    private void postComment(String commentText) {
        if (reviewId == null || reviewId.isEmpty()) return;

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

        ReviewComment c = new ReviewComment();
        c.id = key;
        c.review_id = reviewId;
        c.user_id = userId;
        c.user_name = userFullName;
        c.academic_profile = userAcademicMap.get(userId);
        c.comment_text = commentText;
        c.created_at = createdAt;

        reviewCommentsRef.child(key).setValue(c)
                .addOnCompleteListener(task -> {
                    btnSendComment.setEnabled(true);
                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Failed to add comment", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    edtComment.setText("");
                    rvComments.scrollToPosition(Math.max(0, comments.size() - 1));

                    Toast.makeText(this, "Comment added", Toast.LENGTH_SHORT).show();
                });
    }

}
