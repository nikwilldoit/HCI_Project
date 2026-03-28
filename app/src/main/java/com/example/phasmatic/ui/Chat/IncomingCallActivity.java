package com.example.phasmatic.ui.Chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phasmatic.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class IncomingCallActivity extends AppCompatActivity {

    private String callId;
    private String channelName;
    private DatabaseReference callsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        callId = getIntent().getStringExtra("callId");
        channelName = getIntent().getStringExtra("channelName");

        callsRef = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        ).getReference("calls");

        findViewById(R.id.btnAccept).setOnClickListener(v -> acceptCall());
        findViewById(R.id.btnReject).setOnClickListener(v -> rejectCall());
    }

    private void acceptCall() {

        CallManager callManager = new CallManager();
        callManager.acceptCall(callId);

        Intent intent = new Intent(this, VideoCallActivity.class);
        intent.putExtra("channelName", channelName);
        intent.putExtra("callId", callId);
        startActivity(intent);
        finish();
    }

    private void rejectCall() {
        new CallManager().rejectCall(callId);
        finish();
    }
}
