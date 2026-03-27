package com.example.phasmatic.ui.Chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.phasmatic.R;
import com.example.phasmatic.data.model.Conversation;
import com.example.phasmatic.extras.ProfileImageManager;
import com.example.phasmatic.ui.BackButtonHelper;
import com.example.phasmatic.ui.Profile_Menu.ProfileMenuHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView rvConversations;
    private final ArrayList<Conversation> conversations = new ArrayList<>();
    private ConversationsAdapter adapter;

    private String userId, userFullName, userEmail, userPhone;

    private ImageButton btnBack;
    private ImageView imgProfile;
    private ProfileMenuHelper profileMenuHelper;

    private String currentUid;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance(
            "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
    );
    private DatabaseReference conversationsRef, usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        Intent intent = getIntent();
        userId       = intent.getStringExtra("userId");
        userFullName = intent.getStringExtra("userFullName");
        userEmail    = intent.getStringExtra("userEmail");
        userPhone    = intent.getStringExtra("userPhone");

        imgProfile = findViewById(R.id.imgProfile);
        btnBack    = findViewById(R.id.btnBack);

        BackButtonHelper.attachToGoModeSelection(this, R.id.btnBack, userId, userFullName, userEmail, userPhone);

        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        usersRef = firebaseDb.getReference("users");

        profileMenuHelper = new ProfileMenuHelper(
                this,
                userId,
                userFullName,
                userEmail,
                userPhone
        );

        imgProfile.setOnClickListener(v -> profileMenuHelper.showProfileMenu(v));
        loadProfilePhoto();

        rvConversations = findViewById(R.id.rvConversations);
        rvConversations.setLayoutManager(new LinearLayoutManager(this));

        conversationsRef = db.getReference("conversations");
        usersRef         = db.getReference("users");

        adapter = new ConversationsAdapter(conversations, userId, conversation -> {
            String otherUid;
            if (userId.equals(conversation.leftUser_id)) {
                otherUid = conversation.rightUser_id;
            } else {
                otherUid = conversation.leftUser_id;
            }
            loadUserNameAndOpenChat(otherUid);
        });

        rvConversations.setAdapter(adapter);

        loadConversations();
    }

    private void loadProfilePhoto() {
        if (userId == null || userId.isEmpty()) {
            imgProfile.setImageResource(R.drawable.baseline_face_24);
            return;
        }

        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        DatabaseReference usersRefLocal = firebaseDb.getReference("users");

        usersRefLocal.child(userId).get().addOnSuccessListener(snapshot -> {
            String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);

            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                String displayUrl = profileImageUrl + "?t=" + System.currentTimeMillis();

                Glide.with(this)
                        .load(displayUrl)
                        .placeholder(R.drawable.baseline_face_24)
                        .error(R.drawable.baseline_face_24)
                        .into(imgProfile);
            } else {
                Bitmap bitmap = ProfileImageManager.loadBitmap(this, userId);
                if (bitmap != null) {
                    imgProfile.setImageBitmap(bitmap);
                } else {
                    imgProfile.setImageResource(R.drawable.baseline_face_24);
                }
            }
        });
    }

    private void loadConversations() {
        conversationsRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                conversations.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Conversation c = ds.getValue(Conversation.class);
                    if (c == null) continue;

                    if (!TextUtils.isEmpty(userId) &&
                            (userId.equals(c.leftUser_id) || userId.equals(c.rightUser_id))) {
                        conversations.add(c);
                    }
                }

                conversations.sort((a, b) -> {
                    if (a.timeLastMessage == null || b.timeLastMessage == null) return 0;
                    return b.timeLastMessage.compareTo(a.timeLastMessage);
                });

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void loadUserNameAndOpenChat(String otherUid) {
        usersRef.child(otherUid).get().addOnSuccessListener(snapshot -> {
            String otherName = "User";
            if (snapshot.exists()) {
                String name = snapshot.child("fullName").getValue(String.class);
                if (!TextUtils.isEmpty(name)) otherName = name;
            }

            Intent i = new Intent(this, ChatActivity.class);
            i.putExtra("userId", userId);
            i.putExtra("otherUid", otherUid);
            i.putExtra("otherName", otherName);
            i.putExtra("userEmail", userEmail);
            i.putExtra("userPhone", userPhone);
            startActivity(i);
        });
    }
}
