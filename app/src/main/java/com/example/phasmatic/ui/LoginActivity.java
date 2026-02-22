package com.example.phasmatic.ui;

import android.content.Intent;
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
import com.example.phasmatic.data.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmailAddressLog, edtPasswordLog;
    Button btnRegisterLog, btnLoginLog;
    TextView txtDisplayInfoLog;

    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars =
                            insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top,
                            systemBars.right, systemBars.bottom);
                    return insets;
                });

        edtEmailAddressLog = findViewById(R.id.edtEmailAddressLog);
        edtPasswordLog = findViewById(R.id.edtPasswordLog);
        btnLoginLog = findViewById(R.id.btnLoginLog);
        btnRegisterLog = findViewById(R.id.btnRegisterLog);
        txtDisplayInfoLog = findViewById(R.id.txtDisplayInfoLog);

        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        usersRef = firebaseDb.getReference("users");

        btnRegisterLog.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
        });

        btnLoginLog.setOnClickListener(v -> {
            String email = edtEmailAddressLog.getText().toString().trim();
            String password = edtPasswordLog.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                txtDisplayInfoLog.setText("Please enter email and password");
                return;
            }

            loginWithFirebase(email, password);
        });
    }

    private void loginWithFirebase(String email, String password) {
        usersRef.orderByChild("email")
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            txtDisplayInfoLog.setText("Incorrect email or password");
                            return;
                        }

                        boolean found = false;
                        for (DataSnapshot child : snapshot.getChildren()) {
                            User user = child.getValue(User.class);
                            if (user != null && password.equals(user.getPassword())) {
                                found = true;
                                break;
                            }
                        }

                        if (found) {
                            Intent i = new Intent(LoginActivity.this,
                                    ModeSelectionActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            txtDisplayInfoLog.setText("Incorrect email or password");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        txtDisplayInfoLog.setText(
                                "Firebase error: " + error.getMessage());
                    }
                });
    }
}
