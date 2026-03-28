package com.example.phasmatic.ui.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.phasmatic.R;
import com.example.phasmatic.extras.ProfileImageManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.graphics.Bitmap;

public class OutgoingCallActivity extends AppCompatActivity {

    private String callId;
    private String channelName;
    private String otherUid;
    private String otherName;

    private TextView txtStatus, txtName;
    private ImageButton btnCancel;
    private ImageView imgCallee;

    private DatabaseReference callRef;
    private ValueEventListener callListener;

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_call);

        txtStatus = findViewById(R.id.txtStatus);
        txtName   = findViewById(R.id.txtName);
        btnCancel = findViewById(R.id.btnCancel);
        imgCallee = findViewById(R.id.imgCallee);

        // Παράμετροι από ChatActivity
        callId      = getIntent().getStringExtra("callId");
        channelName = getIntent().getStringExtra("channelName");
        otherUid    = getIntent().getStringExtra("otherUid");
        otherName   = getIntent().getStringExtra("otherName");

        txtStatus.setText("Calling…");
        txtName.setText(otherName != null ? otherName : "User");

        FirebaseDatabase db = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        callRef  = db.getReference("calls").child(callId);
        usersRef = db.getReference("users");

        loadCalleePhoto();

        btnCancel.setOnClickListener(v -> {
            new CallManager().endCall(callId);
            finish();
        });

        listenCallStatus();
    }

    private void loadCalleePhoto() {
        if (otherUid == null || otherUid.isEmpty()) {
            imgCallee.setImageResource(R.drawable.baseline_face_24);
            return;
        }

        usersRef.child(otherUid).get().addOnSuccessListener(snapshot -> {
            String url = snapshot.child("profileImageUrl").getValue(String.class);

            if (url != null && !url.isEmpty()) {
                String displayUrl = url + "?t=" + System.currentTimeMillis();
                Glide.with(this)
                        .load(displayUrl)
                        .placeholder(R.drawable.baseline_face_24)
                        .error(R.drawable.baseline_face_24)
                        .into(imgCallee);
            } else {
                Bitmap bmp = ProfileImageManager.loadBitmap(this, otherUid);
                if (bmp != null) imgCallee.setImageBitmap(bmp);
                else imgCallee.setImageResource(R.drawable.baseline_face_24);
            }
        });
    }

    private void listenCallStatus() {
        callListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.child("status").getValue(String.class);
                if (status == null) return;

                if ("accepted".equals(status)) {
                    txtStatus.setText("Connected");

                    Intent i = new Intent(OutgoingCallActivity.this, VideoCallActivity.class);
                    i.putExtra("channelName", channelName);
                    i.putExtra("callId", callId);
                    startActivity(i);
                    finish();
                } else if ("rejected".equals(status) || "ended".equals(status)) {
                    txtStatus.setText("Call ended");
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };

        callRef.addValueEventListener(callListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (callListener != null && callRef != null) {
            callRef.removeEventListener(callListener);
        }
    }
}