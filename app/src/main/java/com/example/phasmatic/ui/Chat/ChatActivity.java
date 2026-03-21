package com.example.phasmatic.ui.Chat;

import static com.example.phasmatic.ui.BackButtonHelper.attachToGoChat;
import static com.example.phasmatic.ui.BackButtonHelper.attachToGoChats;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phasmatic.R;
import com.example.phasmatic.data.model.Message;
import com.example.phasmatic.extras.ProfileImageManager;
import com.example.phasmatic.ui.BackButtonHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.phasmatic.ui.Profile_Menu.ProfileMenuHelper;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private ImageButton btnBack, btnSend;
    private TextView txtChatWith;
    private EditText etMessage;
    private RecyclerView rvMessages;

    private ImageView imgProfile;

    private final ArrayList<Message> messages = new ArrayList<>();
    private MessagesAdapter adapter;

    private DatabaseReference conversationsRef, messagesRef;

    private String currentUid;
    private String otherUid;
    private String otherName;

    private ProfileMenuHelper profileMenuHelper;

    private String conversationKey = null;

    private final FirebaseDatabase db = FirebaseDatabase.getInstance(
            "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_someone);

        btnBack = findViewById(R.id.btnBack);
        btnSend = findViewById(R.id.btnSend);
        txtChatWith = findViewById(R.id.txtChatWith);
        etMessage = findViewById(R.id.etMessage);
        rvMessages = findViewById(R.id.rvMessages);

        imgProfile = findViewById(R.id.imgProfile);

        btnBack = findViewById(R.id.btnBack);


        currentUid = getIntent().getStringExtra("userId");
        otherUid = getIntent().getStringExtra("otherUid");
        otherName = getIntent().getStringExtra("otherName");

        txtChatWith.setText(otherName != null ? otherName : "Chat");

        conversationsRef = db.getReference("conversations");
        messagesRef = db.getReference("messages");

        adapter = new MessagesAdapter(messages, currentUid);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        rvMessages.setLayoutManager(lm);
        rvMessages.setAdapter(adapter);

        attachToGoChats(this, R.id.btnBack, currentUid, otherUid, otherName);

        btnSend.setOnClickListener(v -> sendMessage());

        imgProfile.setOnClickListener(v -> profileMenuHelper.showProfileMenu(v));
        loadProfilePhoto();

        findOrCreateConversation();

//        Intent i = new Intent(this, UsersActivity.class);
//        i.putExtra("userId", currentUid);
//        i.putExtra("otherUid", otherUid);
//        i.putExtra("otherName", otherName);
//        startActivity(i);
    }

    private void loadProfilePhoto() {
        Bitmap bitmap = ProfileImageManager.loadBitmap(this, currentUid);
        if (bitmap != null) {
            imgProfile.setImageBitmap(bitmap);
        } else {
            imgProfile.setImageResource(R.drawable.baseline_face_24);
        }
    }

    private void findOrCreateConversation() {
        conversationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String left = ds.child("leftUser_id").getValue(String.class);
                    String right = ds.child("rightUser_id").getValue(String.class);

                    boolean match = left != null && right != null &&
                            ((left.equals(currentUid) && right.equals(otherUid)) ||
                                    (left.equals(otherUid) && right.equals(currentUid)));

                    if (match) {
                        conversationKey = ds.getKey();
                        loadMessages();
                        return;
                    }
                }
                createConversation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void createConversation() {
        DatabaseReference newRef = conversationsRef.push();
        conversationKey = newRef.getKey();
        if (conversationKey == null) return;

        java.util.HashMap<String, Object> conv = new java.util.HashMap<>();
        conv.put("id", conversationKey);
        conv.put("lastMessage", "");
        conv.put("leftUser_id", currentUid);
        conv.put("rightUser_id", otherUid);
        conv.put("timeLastMessage", nowString());

        newRef.setValue(conv).addOnSuccessListener(unused -> loadMessages());
    }

    private void loadMessages() {
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String convId = ds.child("conversationId").getValue(String.class);
                    if (conversationKey == null || convId == null || !conversationKey.equals(convId)) continue;

                    Message m = new Message();
                    m.id = ds.child("id").getValue(String.class);
                    m.conversationId = convId;
                    m.sender_id = ds.child("sender_id").getValue(String.class);
                    m.receiver_id = ds.child("receiver_id").getValue(String.class);
                    m.messageText = ds.child("messageText").getValue(String.class);
                    m.timeMessage = ds.child("timeMessage").getValue(String.class);
                    m.statusOfMessage = ds.child("statusOfMessage").getValue(String.class);

                    messages.add(m);
                }

                messages.sort((a, b) -> {
                    if (a.timeMessage == null || b.timeMessage == null) return 0;
                    return a.timeMessage.compareTo(b.timeMessage);
                });

                adapter.notifyDataSetChanged();
                if (!messages.isEmpty()) rvMessages.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(text) || conversationKey == null) return;

        String now = nowString();
        DatabaseReference newMsgRef = messagesRef.push();
        String key = newMsgRef.getKey();
        if (key == null) return;

        java.util.HashMap<String, Object> msg = new java.util.HashMap<>();
        msg.put("id", key);
        msg.put("conversationId", conversationKey);
        msg.put("sender_id", currentUid);
        msg.put("receiver_id", otherUid);
        msg.put("messageText", text);
        msg.put("statusOfMessage", "1");
        msg.put("timeMessage", now);

        newMsgRef.setValue(msg);
        conversationsRef.child(conversationKey).child("lastMessage").setValue(text);
        conversationsRef.child(conversationKey).child("timeLastMessage").setValue(now);

        etMessage.setText("");
    }

    private String nowString() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }
}
