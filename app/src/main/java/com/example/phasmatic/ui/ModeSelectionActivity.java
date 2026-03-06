package com.example.phasmatic.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.net.Uri;
import android.widget.VideoView;
import android.widget.MediaController;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.phasmatic.R;
import com.example.phasmatic.ui.Forum.ForumActivity;
import com.example.phasmatic.ui.Profile_Menu.ProfileMenuHelper;


public class ModeSelectionActivity extends AppCompatActivity {

    Button btnErasmus, btnMaster, btnForum;
    ImageButton btnBack;
    TextView txtTitle, txtSubtitle;
    ImageView imgProfile;
    private String userId, userFullName, userEmail, userPhone;
    private ProfileMenuHelper profileMenuHelper;
    private VideoView videoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mode_selection);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userFullName = intent.getStringExtra("userFullName");
        userEmail = intent.getStringExtra("userEmail");
        userPhone = intent.getStringExtra("userPhone");

        imgProfile = findViewById(R.id.imgProfile);

        profileMenuHelper = new ProfileMenuHelper(
                this,
                userId,
                userFullName,
                userEmail,
                userPhone
        );

        imgProfile.setOnClickListener(v -> profileMenuHelper.showProfileMenu(v));

        txtTitle = findViewById(R.id.txtTitleMode);
        txtSubtitle = findViewById(R.id.txtSubtitleMode);
        btnErasmus = findViewById(R.id.btnErasmusMode);
        btnMaster = findViewById(R.id.btnMasterMode);
        videoView = findViewById(R.id.videoView);
        btnForum = findViewById(R.id.btnForum);



        BackButtonHelper.attachToGoModeSelection(
                this,
                R.id.btnBack,
                userId,
                userFullName,
                userEmail,
                userPhone
        );


        btnErasmus.setOnClickListener(v -> {
            Intent i = new Intent(ModeSelectionActivity.this, QuestionnaireActivity.class);
            i.putExtra("userId", userId);
            i.putExtra("userFullName", userFullName);
            i.putExtra("userEmail", userEmail);
            i.putExtra("userPhone", userPhone);
            i.putExtra("modeType", "erasmus");
            startActivity(i);
        });

        btnMaster.setOnClickListener(v -> {
            Intent i = new Intent(ModeSelectionActivity.this, QuestionnaireActivity.class);
            i.putExtra("userId", userId);
            i.putExtra("userFullName", userFullName);
            i.putExtra("userEmail", userEmail);
            i.putExtra("userPhone", userPhone);
            i.putExtra("modeType", "master");
            startActivity(i);
        });

        btnForum.setOnClickListener(v -> {
            Intent i = new Intent(ModeSelectionActivity.this, ForumActivity.class);
            i.putExtra("userId", userId);
            i.putExtra("userFullName", userFullName);
            i.putExtra("userEmail", userEmail);
            i.putExtra("userPhone", userPhone);
            //i.putExtra("modeType", "master");
            startActivity(i);
        });

        String path = "android.resource://" + getPackageName() + "/" + R.raw.indian_video;
        Uri uri = Uri.parse(path);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();

        videoView.setOnCompletionListener(mp -> videoView.start());
    }
}

