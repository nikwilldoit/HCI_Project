package com.example.phasmatic.data.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.phasmatic.R;
import com.example.phasmatic.ai.OpenAIChatClient;

public class ErasmusChatActivity extends AppCompatActivity {

    TextView txtChatTitle, txtChatLog;
    EditText edtUserInput;
    Button btnSend;

    OpenAIChatClient chatClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_erasmus_chat);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });

        txtChatTitle  = findViewById(R.id.txtChatTitle);
        txtChatLog    = findViewById(R.id.txtChatLog);
        edtUserInput  = findViewById(R.id.edtUserInput);
        btnSend       = findViewById(R.id.btnSend);

        txtChatTitle.setText("DECYRA Erasmus Assistant");

        chatClient = new OpenAIChatClient(this);

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
    }

    private void appendToChat(String text) {
        if (txtChatLog.getText().length() == 0) {
            txtChatLog.setText(text);
        } else {
            txtChatLog.append("\n\n" + text);
        }
    }
}
