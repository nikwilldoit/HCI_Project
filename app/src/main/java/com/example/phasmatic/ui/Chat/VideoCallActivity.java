package com.example.phasmatic.ui.Chat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.phasmatic.R;

import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.video.VideoCanvas;

public class VideoCallActivity extends AppCompatActivity {

    private RtcEngine agoraEngine;

    private final String APP_ID = "0f9cc9d655b347fb852d60aef0fcf693";
    private final String TOKEN = null; // ή από server

    private String channelName;

    private FrameLayout localContainer;
    private FrameLayout remoteContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        channelName = getIntent().getStringExtra("channelName");

        localContainer = findViewById(R.id.localVideoContainer);
        remoteContainer = findViewById(R.id.remoteVideoContainer);

        findViewById(R.id.btnEndCall).setOnClickListener(v -> {
            finish();
        });

        findViewById(R.id.btnMute).setOnClickListener(v -> {
            agoraEngine.muteLocalAudioStream(true);
        });

        findViewById(R.id.btnSwitchCamera).setOnClickListener(v -> {
            agoraEngine.switchCamera();
        });

        requestPermissions();
    }

    // 🔐 Permissions
    private void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO
                    }, 1);
        } else {
            initAgora();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initAgora();
    }

    // 🔧 Init Agora
    private void initAgora() {
        try {
            agoraEngine = RtcEngine.create(getBaseContext(), APP_ID, rtcHandler);
        } catch (Exception e) {
            throw new RuntimeException("Agora init error");
        }

        agoraEngine.enableVideo();

        setupLocalVideo();
        joinChannel();
    }

    // 📷 Local video
    private void setupLocalVideo() {
        SurfaceView localView = new SurfaceView(getBaseContext());
        localContainer.addView(localView);

        agoraEngine.setupLocalVideo(
                new VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, 0)
        );
    }

    // 🌐 Join channel (core)
    private void joinChannel() {

        ChannelMediaOptions options = new ChannelMediaOptions();
        options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;

        agoraEngine.startPreview();

        agoraEngine.joinChannel(
                TOKEN,
                channelName,
                0,
                options
        );
    }

    // 📡 EVENTS
    private final IRtcEngineEventHandler rtcHandler = new IRtcEngineEventHandler() {

        @Override
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(() -> setupRemoteVideo(uid));
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            runOnUiThread(() -> finish());
        }
    };

    // 👤 Remote video
    private void setupRemoteVideo(int uid) {
        SurfaceView remoteView = new SurfaceView(getBaseContext());
        remoteContainer.addView(remoteView);

        agoraEngine.setupRemoteVideo(
                new VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid)
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (agoraEngine != null) {
            agoraEngine.leaveChannel();
        }
        RtcEngine.destroy();
    }
}