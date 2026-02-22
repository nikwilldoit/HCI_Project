package com.example.phasmatic.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.phasmatic.R;

public class ModeSelectionActivity extends AppCompatActivity {

    Button btnErasmus, btnMaster;
    TextView txtTitle, txtSubtitle;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mode_selection);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtTitle = findViewById(R.id.txtTitleMode);
        txtSubtitle = findViewById(R.id.txtSubtitleMode);
        btnErasmus = findViewById(R.id.btnErasmusMode);
        btnMaster = findViewById(R.id.btnMasterMode);

        btnErasmus.setOnClickListener(v -> {
            Intent i = new Intent(ModeSelectionActivity.this, ProfileQuestionsActivity.class);
            i.putExtra("MODE", "ERASMUS");
            startActivity(i);
        });

        btnMaster.setOnClickListener(v -> {
            Intent i = new Intent(ModeSelectionActivity.this, ProfileQuestionsActivity.class);
            i.putExtra("MODE", "MASTER");
            startActivity(i);
        });
    }
}
