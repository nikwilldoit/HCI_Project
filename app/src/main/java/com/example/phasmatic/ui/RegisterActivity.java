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
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.phasmatic.R;
import com.example.phasmatic.data.model.User;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    EditText edtEmailAddressReg, edtPasswordReg, edtFullNameReg,
            edtDateOfBirthReg, edtPhoneNumberReg, edtBioReg;
    Button btnRegisterReg, btnLoginReg, btnCaptureFace, btnTakePhoto;
    TextView txtDisplayInfoReg;

    DatabaseReference usersRef;
    private static final int CAMERA_PERMISSION_CODE = 100;

    private PreviewView viewFinder;
    private ImageCapture imageCapture;
    private FrameLayout cameraLayout;
    private View registerLayout;

    private float[] capturedEmbedding = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtEmailAddressReg = findViewById(R.id.edtEmailAddressReg);
        edtPasswordReg     = findViewById(R.id.edtPasswordReg);
        edtFullNameReg     = findViewById(R.id.edtFullName);
        edtDateOfBirthReg  = findViewById(R.id.edtDateOfBirthReg);
        edtPhoneNumberReg  = findViewById(R.id.edtPhoneNumberReg);
        edtBioReg          = findViewById(R.id.edtBioReg);

        btnLoginReg    = findViewById(R.id.btnLoginReg);
        btnRegisterReg = findViewById(R.id.btnRegisterReg);
        btnCaptureFace = findViewById(R.id.btnCaptureFace);
        btnTakePhoto   = findViewById(R.id.btnTakePhoto);
        txtDisplayInfoReg = findViewById(R.id.txtDisplayInfoReg);

        cameraLayout   = findViewById(R.id.cameraLayout);
        registerLayout = findViewById(R.id.registerLayout);
        viewFinder     = findViewById(R.id.viewFinder);

        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        usersRef = firebaseDb.getReference("users");

        btnLoginReg.setOnClickListener(v -> {
            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        });

        btnCaptureFace.setOnClickListener(v -> {
            cameraLayout.setVisibility(View.VISIBLE);
            registerLayout.setVisibility(View.GONE);
            checkCameraPermission();
        });

        btnTakePhoto.setOnClickListener(v -> takePhoto());

        btnRegisterReg.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String fullname = edtFullNameReg.getText().toString().trim();
        String email    = edtEmailAddressReg.getText().toString().trim();
        String password = edtPasswordReg.getText().toString().trim();
        String dob      = edtDateOfBirthReg.getText().toString().trim();
        String phone    = edtPhoneNumberReg.getText().toString().trim();
        String bio      = edtBioReg.getText().toString().trim();

        if (fullname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Full name, email and password are required",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (capturedEmbedding == null) {
            Toast.makeText(this, "Please capture your face first",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = usersRef.push().getKey();
        if (userId != null) {
            User user = new User(userId, fullname, email, password, dob, phone, bio);
            usersRef.child(userId).setValue(user)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Firebase error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });

            List<Double> embeddingList = new ArrayList<>();
            for (float f : capturedEmbedding) embeddingList.add((double) f);
            usersRef.child(userId).child("faceEmbedding").setValue(embeddingList);
        }
    }

    // ================= CAMERA =================

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        }
    }

    private void startCamera() {
        viewFinder.setVisibility(View.VISIBLE);
        btnTakePhoto.setVisibility(View.VISIBLE);

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();
                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

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

        String fullName = edtFullNameReg.getText().toString().trim();
        if (fullName.isEmpty()) {
            Toast.makeText(this, "Please enter your full name first", Toast.LENGTH_SHORT).show();
            return;
        }
        String safeFileName = fullName.replaceAll("[^a-zA-Z0-9]", "_");

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, safeFileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/FaceReg");
        }

        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(
                        getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                ).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onError(@NonNull ImageCaptureException exc) {
                        Log.e("CameraX", "Photo failed: " + exc.getMessage());
                    }

                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        Toast.makeText(RegisterActivity.this,
                                "Face Captured!", Toast.LENGTH_SHORT).show();

                        // dummy embedding
                        capturedEmbedding = new float[128];
                        for (int i = 0; i < 128; i++) capturedEmbedding[i] = (float) Math.random();

                        //return to register
                        cameraLayout.setVisibility(View.GONE);
                        registerLayout.setVisibility(View.VISIBLE);
                    }
                });
    }
}
