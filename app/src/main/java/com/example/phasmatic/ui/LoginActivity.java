package com.example.phasmatic.ui;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.phasmatic.R;
import com.example.phasmatic.data.model.User;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.*;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class LoginActivity extends AppCompatActivity {

    EditText edtEmailAddressLog, edtPasswordLog;
    Button btnRegisterLog, btnLoginLog, btnFaceLogin;
    TextView txtDisplayInfoLog;

    DatabaseReference usersRef;
    private ImageCapture imageCapture;
    private Button captureButton;

    //Camera
    private PreviewView viewFinder;
    private View cameraLayout, loginLayout;

    private static final int CAMERA_PERMISSION_CODE = 100;

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

        //UI
        edtEmailAddressLog = findViewById(R.id.edtEmailAddressLog);
        edtPasswordLog = findViewById(R.id.edtPasswordLog);
        btnLoginLog = findViewById(R.id.btnLoginLog);
        btnRegisterLog = findViewById(R.id.btnRegisterLog);
        txtDisplayInfoLog = findViewById(R.id.txtDisplayInfoLog);
        btnFaceLogin = findViewById(R.id.btnFaceLogin);

        viewFinder = findViewById(R.id.viewFinder);
        cameraLayout = findViewById(R.id.cameraLayout);
        loginLayout = findViewById(R.id.loginLayout);

        //Firebase
        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        usersRef = firebaseDb.getReference("users");

        //Register
        btnRegisterLog.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
        });

        //Normal Login
        btnLoginLog.setOnClickListener(v -> {
            String email = edtEmailAddressLog.getText().toString().trim();
            String password = edtPasswordLog.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                txtDisplayInfoLog.setText("Please enter email and password");
                return;
            }

            loginWithFirebase(email, password);
        });

        //Face Login

        btnFaceLogin.setOnClickListener(v -> checkCameraPermission());
        captureButton = findViewById(R.id.image_capture_button);

        captureButton.setOnClickListener(v -> takePhoto());
    }


    // =========================================================
    //CAMERA PERMISSION
    // =========================================================
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                txtDisplayInfoLog.setText("Camera permission denied");
            }
        }
    }

    // =========================================================
    //START CAMERA (CameraX)
    // =========================================================
    private void startCamera() {

        cameraLayout.setVisibility(View.VISIBLE);
        loginLayout.setVisibility(View.GONE);

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider =
                        cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();

                CameraSelector cameraSelector =
                        CameraSelector.DEFAULT_FRONT_CAMERA;

                cameraProvider.unbindAll();

                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageCapture
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }
    private void takePhoto() {
        if (imageCapture == null) {
            Toast.makeText(this, "Camera not ready yet", Toast.LENGTH_SHORT).show();
            return;
        }

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
                .format(System.currentTimeMillis());

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timeStamp);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image");
        }

        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(
                        getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                ).build();

        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onError(@NonNull ImageCaptureException exc) {
                        Log.e("CameraX", "Photo capture failed: " + exc.getMessage(), exc);
                    }

                    @Override
                    public void onImageSaved(
                            @NonNull ImageCapture.OutputFileResults output) {

                        String msg = "Photo capture succeeded: " + output.getSavedUri();
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        Log.d("CameraX", msg);

                        // 🔹 ΕΠΙΣΤΡΟΦΗ ΣΤΟ LOGIN UI
                        cameraLayout.setVisibility(View.GONE);
                        loginLayout.setVisibility(View.VISIBLE);
                    }
                }
        );
    }




    // =========================================================
    //FIREBASE LOGIN
    // =========================================================
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