package com.example.phasmatic.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phasmatic.R;

public class MasterChatActivity extends AppCompatActivity {

    TextView txtChatTitle, txtChatLog;
    EditText edtUserInput;
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_chat);

        txtChatTitle = findViewById(R.id.txtChatTitle);
        txtChatLog = findViewById(R.id.txtChatLog);
        edtUserInput = findViewById(R.id.edtUserInput);
        btnSend = findViewById(R.id.btnSend);

        txtChatTitle = findViewById(R.id.txtChatTitle);

        btnSend.setOnClickListener(v -> {
            String userMsg = edtUserInput.getText().toString().trim();
            if (userMsg.isEmpty()) return;

            txtChatLog.append("\nYou: " + userMsg);
            edtUserInput.setText("");
        });
    }
}
