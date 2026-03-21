package com.example.phasmatic.ui.Chat;

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

    ImageView imgProfile;
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
        userId = intent.getStringExtra("userId");
        userFullName = intent.getStringExtra("userFullName");
        userEmail = intent.getStringExtra("userEmail");
        userPhone = intent.getStringExtra("userPhone");

        imgProfile = findViewById(R.id.imgProfile);

        BackButtonHelper.attachToGoModeSelection(this, R.id.btnBack, userId,userFullName, userEmail, userPhone);
//        btnBack.setOnClickListener(v -> finish());

        imgProfile.setOnClickListener(v -> profileMenuHelper.showProfileMenu(v));
        loadProfilePhoto();

        currentUid = getIntent().getStringExtra("userId");

        rvConversations = findViewById(R.id.rvConversations);
        rvConversations.setLayoutManager(new LinearLayoutManager(this));

        conversationsRef = db.getReference("conversations");
        usersRef = db.getReference("users");

        adapter = new ConversationsAdapter(conversations, currentUid, conversation -> {
            String otherUid = currentUid.equals(conversation.leftUser_id)
                    ? conversation.rightUser_id
                    : conversation.leftUser_id;

            loadUserNameAndOpenChat(otherUid);
        });

        rvConversations.setAdapter(adapter);

        loadConversations();
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
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                conversations.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Conversation c = ds.getValue(Conversation.class);
                    if (c == null) continue;

                    if (!TextUtils.isEmpty(currentUid) &&
                            (currentUid.equals(c.leftUser_id) || currentUid.equals(c.rightUser_id))) {
                        conversations.add(c);
                    }
                }

                //sort me vash to time last message
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
            i.putExtra("userId", currentUid);
            i.putExtra("otherUid", otherUid);
            i.putExtra("otherName", otherName);
            startActivity(i);
        });
    }
}