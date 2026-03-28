package com.example.phasmatic.ui.Chat;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.database.*;

public class CallListener {

    private final DatabaseReference callsRef;
    private final String currentUid;
    private final Context context;

    public CallListener(Context context, String currentUid) {
        this.context = context;
        this.currentUid = currentUid;

        callsRef = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        ).getReference("calls");
    }

    public void start() {
        callsRef.addValueEventListener(listener);
    }

    public void stop() {
        callsRef.removeEventListener(listener);
    }

    private final ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            for (DataSnapshot ds : snapshot.getChildren()) {

                String receiverId = ds.child("receiver_id").getValue(String.class);
                String status = ds.child("status").getValue(String.class);

                if (currentUid.equals(receiverId) && "ringing".equals(status)) {

                    String channelName = ds.child("channelName").getValue(String.class);
                    String callId = ds.child("id").getValue(String.class);

                    Intent intent = new Intent(context, IncomingCallActivity.class);
                    intent.putExtra("channelName", channelName);
                    intent.putExtra("callId", callId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    context.startActivity(intent);
                    break;
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {}
    };
}