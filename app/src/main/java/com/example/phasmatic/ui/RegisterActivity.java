package com.example.phasmatic.ui;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    EditText edtEmailAddressReg, edtPasswordReg, edtFullNameReg, edtPhoneNumberReg;
    Button btnRegisterReg, btnLoginReg, btnCaptureFace, btnTakePhoto;
    TextView txtDisplayInfoReg;

    private Interpreter tflite;
    DatabaseReference usersRef;
    private static final int CAMERA_PERMISSION_CODE = 100;

    private PreviewView viewFinder;
    private ImageCapture imageCapture;
    private FrameLayout cameraLayout;
    private android.view.View registerLayout;

    private float[] capturedEmbedding = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //views apo XML
        edtEmailAddressReg = findViewById(R.id.edtEmailAddressReg);
        edtPasswordReg     = findViewById(R.id.edtPasswordReg);
        edtFullNameReg     = findViewById(R.id.edtFullName);
        edtPhoneNumberReg  = findViewById(R.id.edtPhoneNumberReg);

        btnLoginReg    = findViewById(R.id.btnLoginReg);
        btnRegisterReg = findViewById(R.id.btnRegisterReg);
        btnCaptureFace = findViewById(R.id.btnCaptureFace);
        btnTakePhoto   = findViewById(R.id.btnTakePhoto);
        txtDisplayInfoReg = findViewById(R.id.txtDisplayInfoReg);

        cameraLayout   = findViewById(R.id.cameraLayout);
        registerLayout = findViewById(R.id.registerLayout);
        viewFinder     = findViewById(R.id.viewFinder);

        //Firebase
        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        usersRef = firebaseDb.getReference("users");

        //go to login
        btnLoginReg.setOnClickListener(v -> {
            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        });

        //capture face
        btnCaptureFace.setOnClickListener(v -> {
            cameraLayout.setVisibility(android.view.View.VISIBLE);
            registerLayout.setVisibility(android.view.View.GONE);
            checkCameraPermission();
        });

        //take photo
        btnTakePhoto.setOnClickListener(v -> takePhoto());

        //register
        btnRegisterReg.setOnClickListener(v -> registerUser());

        //load TFLite model
        try {
            AssetFileDescriptor fileDescriptor = getAssets().openFd("facenet.tflite");
            FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            tflite = new Interpreter(fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerUser() {
        String fullname = edtFullNameReg.getText().toString().trim();
        String email    = edtEmailAddressReg.getText().toString().trim();
        String password = edtPasswordReg.getText().toString().trim();
        String phone    = edtPhoneNumberReg.getText().toString().trim();

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

            List<Double> embeddingList = new ArrayList<>();
            for (float f : capturedEmbedding) embeddingList.add((double) f);

            User user = new User(
                    userId,
                    fullname,
                    email,
                    password,
                    phone,
                    embeddingList
            );

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
        }
    }

    //CAMERA
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
        viewFinder.setVisibility(android.view.View.VISIBLE);
        btnTakePhoto.setVisibility(android.view.View.VISIBLE);

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

                        try {
                            Uri imageUri = output.getSavedUri();

                            if (imageUri == null) {
                                Toast.makeText(RegisterActivity.this,
                                        "Image URI is null", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);

                            if (bitmap == null) {
                                Toast.makeText(RegisterActivity.this,
                                        "Bitmap decode failed", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
                            Bitmap cropped = Bitmap.createBitmap(
                                    bitmap,
                                    (bitmap.getWidth() - size) / 2,
                                    (bitmap.getHeight() - size) / 2,
                                    size,
                                    size
                            );

                            Bitmap resized = Bitmap.createScaledBitmap(cropped, 160, 160, true);

                            capturedEmbedding = runModel(resized);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this,
                                    "Processing error", Toast.LENGTH_SHORT).show();
                        }

                        cameraLayout.setVisibility(android.view.View.GONE);
                        registerLayout.setVisibility(android.view.View.VISIBLE);
                    }
                });
    }

    private float[] runModel(Bitmap bitmap) {

        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(1 * 160 * 160 * 3 * 4);
        inputBuffer.order(ByteOrder.nativeOrder());

        int[] pixels = new int[160 * 160];
        bitmap.getPixels(pixels, 0, 160, 0, 0, 160, 160);

        for (int pixel : pixels) {
            float r = ((pixel >> 16) & 0xFF) / 255.0f;
            float g = ((pixel >> 8) & 0xFF) / 255.0f;
            float b = (pixel & 0xFF) / 255.0f;

            inputBuffer.putFloat(r);
            inputBuffer.putFloat(g);
            inputBuffer.putFloat(b);
        }

        float[][] output = new float[1][128];

        tflite.run(inputBuffer, output);

        return output[0];
    }
}
