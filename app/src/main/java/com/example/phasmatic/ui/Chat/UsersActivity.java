package com.example.phasmatic.ui.Chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phasmatic.R;
import com.example.phasmatic.data.model.Conversation;
import com.example.phasmatic.extras.ProfileImageManager;
import com.example.phasmatic.ui.Profile_Menu.ProfileMenuHelper;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView imgProfile;
    private RecyclerView recyclerView;

    private ProfileMenuHelper profileMenuHelper;

    private DatabaseReference conversationsRef;

    private String userId, userFullName, userEmail, userPhone;

    private List<Conversation> conversationsList = new ArrayList<>();

    private ConversationsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_impl);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userFullName = intent.getStringExtra("userFullName");
        userEmail = intent.getStringExtra("userEmail");
        userPhone = intent.getStringExtra("userPhone");
        btnBack = findViewById(R.id.btnBack);
        imgProfile = findViewById(R.id.imgProfile);
        recyclerView = findViewById(R.id.rvUserMessages);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ConversationsAdapter(conversationsList, userId, this);
        recyclerView.setAdapter(adapter);

        conversationsRef = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        ).getReference("conversations");

        profileMenuHelper = new ProfileMenuHelper(this, userId, userFullName, userEmail, userPhone);
        imgProfile.setOnClickListener(v -> profileMenuHelper.showProfileMenu(v));

        loadProfilePhoto();
        loadConversations();

        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void loadProfilePhoto() {
        Bitmap bitmap = ProfileImageManager.loadBitmap(this, userId);
        if (bitmap != null) {
            imgProfile.setImageBitmap(bitmap);
        } else {
            imgProfile.setImageResource(R.drawable.baseline_face_24);
        }
    }

    private void loadConversations() {
        conversationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                conversationsList.clear();

                for (DataSnapshot child : snapshot.getChildren()) {

                    String left = child.child("leftUser_id").getValue(String.class);
                    String right = child.child("rightUser_id").getValue(String.class);

                    if (userId != null && (userId.equals(left) || userId.equals(right))) {

                        Conversation c = new Conversation();
                        c.id = child.getKey();
                        c.leftUser_id = left;
                        c.rightUser_id = right;
                        c.lastMessage = child.child("lastMessage").getValue(String.class);
                        c.timeLastMessage = child.child("timeLastMessage").getValue(String.class);

                        conversationsList.add(c);
                    }
                }

                conversationsList.sort((a, b) -> {
                    if (a.timeLastMessage == null || b.timeLastMessage == null) return 0;
                    return b.timeLastMessage.compareTo(a.timeLastMessage);
                });

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}