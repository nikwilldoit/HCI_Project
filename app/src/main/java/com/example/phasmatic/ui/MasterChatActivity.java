package com.example.phasmatic.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.phasmatic.R;
import com.example.phasmatic.data.ai.OpenAIChatClient;
import com.example.phasmatic.ui.Profile_Menu.ProfileMenuHelper;
import android.graphics.Bitmap;
import com.example.phasmatic.extras.ProfileImageManager;

import java.util.ArrayList;

public class MasterChatActivity extends AppCompatActivity {

    TextView txtChatTitle, txtChatLog;
    EditText edtUserInput;
    Button btnSend, btnVoice;
    ImageButton btnBack;
    ImageView imgProfile;
    private String userId, userFullName, userEmail, userPhone;
    OpenAIChatClient chatClient;
    private ProfileMenuHelper profileMenuHelper;

    private String userExpectations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_master_chat);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userFullName = intent.getStringExtra("userFullName");
        userEmail = intent.getStringExtra("userEmail");
        userPhone = intent.getStringExtra("userPhone");
        userExpectations = intent.getStringExtra("userExpectations");


        imgProfile = findViewById(R.id.imgProfile);

        profileMenuHelper = new ProfileMenuHelper(
                this,
                userId,
                userFullName,
                userEmail,
                userPhone
        );

        imgProfile.setOnClickListener(v -> profileMenuHelper.showProfileMenu(v));
        loadProfilePhoto();

        txtChatTitle = findViewById(R.id.txtChatTitle);
        txtChatLog = findViewById(R.id.txtChatLog);
        edtUserInput = findViewById(R.id.edtUserInput);
        btnSend = findViewById(R.id.btnSend);
        btnVoice = findViewById(R.id.btnVoice);

        txtChatTitle.setText("DECYRA Master Assistant");

        chatClient = new OpenAIChatClient(this);

        BackButtonHelper.attach(this, R.id.btnBack);

        if (userExpectations != null && !userExpectations.isEmpty()) {
            edtUserInput.setText(userExpectations);
            edtUserInput.setSelection(userExpectations.length());
        }

        btnSend.setOnClickListener(v -> {
            String userMsg = edtUserInput.getText().toString().trim();
            if (userMsg.isEmpty()) return;

            appendToChat("You: " + userMsg);
            edtUserInput.setText("");
            btnSend.setEnabled(false);

            chatClient.sendMessage(userMsg, new OpenAIChatClient.ChatCallback() {
                @Override
                public void onSuccess(String reply) {
                    runOnUiThread(() -> {
                        appendToChat("Assistant: " + reply);
                        btnSend.setEnabled(true);
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        appendToChat("Error: " + error);
                        btnSend.setEnabled(true);
                    });
                }
            });
        });

        btnVoice.setOnClickListener(v -> startSpeechRecognizer());
    }

    private void loadProfilePhoto() {
        Bitmap bitmap = ProfileImageManager.loadBitmap(this, userId);
        if (bitmap != null) {
            imgProfile.setImageBitmap(bitmap);
        } else {
            imgProfile.setImageResource(R.drawable.baseline_face_24);
        }
    }

    private void startSpeechRecognizer() {
        int REQUEST_SPEECH_RECOGNIZER = 3000;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "el-GR");

        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "el-GR");


        try {
            startActivityForResult(intent, REQUEST_SPEECH_RECOGNIZER);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Η αναγνώριση φωνής δεν υποστηρίζεται στη συσκευή σας", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int REQUEST_SPEECH_RECOGNIZER = 3000;
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("DEMO-REQUESTCODE", Integer.toString(requestCode));
        Log.i("DEMO-RESULTCODE", Integer.toString(resultCode));

        if (requestCode == REQUEST_SPEECH_RECOGNIZER && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            edtUserInput.setText(text.get(0));

            Log.i("DEMO-ANSWER", text.get(0));

        } else {
            System.out.println("Recognizer API error");
        }
    }

    private void appendToChat(String text) {
        if (txtChatLog.getText().length() == 0) {
            txtChatLog.setText(text);
        } else {
            txtChatLog.append("\n\n" + text);
        }
    }
}
