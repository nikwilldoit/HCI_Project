package com.example.phasmatic.ui;

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

public class ErasmusChatActivity extends AppCompatActivity {

    TextView txtChatTitle, txtChatLog;
    EditText edtUserInput;
    Button btnSend;

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

        btnSend.setOnClickListener(v -> {
            String userMsg = edtUserInput.getText().toString().trim();
            if (userMsg.isEmpty()) return;

            txtChatLog.append("\nYou: " + userMsg);
            edtUserInput.setText("");
        });
    }
}
