package com.example.phasmatic.ui.Chat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CallManager {

    private final DatabaseReference callsRef;

    public CallManager() {
        callsRef = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        ).getReference("calls");
    }

    public String startCall(String callerId, String receiverId, String channelName) {

        String callId = callsRef.push().getKey();

        if (callId == null) return null;

        HashMap<String, Object> call = new HashMap<>();
        call.put("id", callId);
        call.put("caller_id", callerId);
        call.put("receiver_id", receiverId);
        call.put("channelName", channelName);
        call.put("status", "ringing");
        call.put("created_at", now());

        callsRef.child(callId).setValue(call);

        return callId;
    }

    public void acceptCall(String callId) {
        callsRef.child(callId).child("status").setValue("accepted");
    }

    public void rejectCall(String callId) {
        callsRef.child(callId).child("status").setValue("rejected");
    }

    public void endCall(String callId) {
        callsRef.child(callId).child("status").setValue("ended");
    }

    private String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
    }
}