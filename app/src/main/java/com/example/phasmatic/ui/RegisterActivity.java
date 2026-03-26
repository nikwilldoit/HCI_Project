package com.example.phasmatic.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.phasmatic.R;
import com.example.phasmatic.data.model.FaceGuideOverlay;
import com.example.phasmatic.data.model.User;
import com.example.phasmatic.data.model.User_Face_Embedding;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    EditText edtEmailAddressReg, edtPasswordReg, edtFullNameReg, edtPhoneNumberReg;
    Button btnRegisterReg, btnLoginReg, btnCaptureFace;
    TextView txtDisplayInfoReg;

    private Interpreter tflite;
    private Toast currentToast;
    DatabaseReference usersRef;
    DatabaseReference usersfaceembeddingRef;
    private static final int CAMERA_PERMISSION_CODE = 100;

    private PreviewView viewFinder;
    private FrameLayout cameraLayout;
    private long lastCaptureTime = 0;

    private int framesCapturedForAction = 0;
    private static final int FRAMES_PER_ACTION = 3;

    private List<List<Double>> centerEmbeddings = new ArrayList<>();
    private List<List<Double>> actionEmbeddings = new ArrayList<>();

    private static final int CENTER_EMBEDDINGS = 3;

    private android.view.View registerLayout;
    private FaceGuideOverlay faceGuideOverlay;

    public enum FaceAction {
        CENTER, LOOK_LEFT, LOOK_RIGHT, LOOK_UP, LOOK_DOWN, BLINK, DONE
    }

    private FaceAction currentAction = FaceAction.CENTER;

    private List<List<Double>> finalEmbeddingsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtEmailAddressReg = findViewById(R.id.edtEmailAddressReg);
        edtPasswordReg     = findViewById(R.id.edtPasswordReg);
        edtFullNameReg     = findViewById(R.id.edtFullName);
        edtPhoneNumberReg  = findViewById(R.id.edtPhoneNumberReg);

        btnLoginReg        = findViewById(R.id.btnLoginReg);
        btnRegisterReg     = findViewById(R.id.btnRegisterReg);
        btnCaptureFace     = findViewById(R.id.btnCaptureFace);
        faceGuideOverlay   = findViewById(R.id.faceGuideOverlay);

        cameraLayout       = findViewById(R.id.cameraLayout);
        registerLayout     = findViewById(R.id.registerLayout);
        viewFinder         = findViewById(R.id.viewFinder);

        // Firebase
        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        usersRef = firebaseDb.getReference("users");
        usersfaceembeddingRef = firebaseDb.getReference("users_face_embedding");

        // Go to login
        btnLoginReg.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        // Capture face
        btnCaptureFace.setOnClickListener(v -> {
            cameraLayout.setVisibility(android.view.View.VISIBLE);
            registerLayout.setVisibility(android.view.View.GONE);
            checkCameraPermission();
        });

        //Register user
        btnRegisterReg.setOnClickListener(v -> registerUser());

        //Load TFLite model
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
            Toast.makeText(this, "Full name, email and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        List<List<Double>> finalEmbeddingsList = buildFinalEmbeddings();

        if (finalEmbeddingsList.isEmpty()) {
            Toast.makeText(this, "Please capture your face first", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = usersRef.push().getKey();
        String user_face_embeddingId = usersfaceembeddingRef.push().getKey();

        if (userId != null) {
            User user = new User(userId, fullname, email, password, phone);
            User_Face_Embedding userFaceEmbedding = new User_Face_Embedding(user_face_embeddingId, userId, finalEmbeddingsList, user);

            usersRef.child(userId).setValue(user)
                    .addOnSuccessListener(unused -> usersfaceembeddingRef.child(user_face_embeddingId)
                            .setValue(userFaceEmbedding)
                            .addOnSuccessListener(u -> {
                                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            }));
        }
    }

    // CAMERA
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        }
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void startCamera() {

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {

            try {

                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                ImageAnalysis imageAnalysis =
                        new ImageAnalysis.Builder()
                                .setBackpressureStrategy(
                                        ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build();

                imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(this),
                        imageProxy -> {

                            if (imageProxy.getImage() == null) {
                                imageProxy.close();
                                return;
                            }

                            InputImage image =
                                    InputImage.fromMediaImage(
                                            imageProxy.getImage(),
                                            imageProxy.getImageInfo().getRotationDegrees());

                            FaceDetectorOptions options =
                                    new FaceDetectorOptions.Builder()
                                            .setPerformanceMode(
                                                    FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                                            .setClassificationMode(
                                                    FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                                            .build();

                            FaceDetector detector =
                                    FaceDetection.getClient(options);

                            detector.process(image)
                                    .addOnSuccessListener(faces -> {

                                        if (!faces.isEmpty()) {

                                            Face face = faces.get(0);

                                            boolean actionCompleted = updateFaceAction(face);

                                            if (actionCompleted) {

                                                Bitmap bitmap = viewFinder.getBitmap();

                                                if (bitmap != null) {
                                                    processFrame(bitmap, face);
                                                }
                                            }

                                            if (currentAction == FaceAction.DONE) {

                                                cameraLayout.setVisibility(android.view.View.GONE);
                                                registerLayout.setVisibility(android.view.View.VISIBLE);
                                            }
                                        }

                                    })
                                    .addOnCompleteListener(task ->
                                            imageProxy.close());

                        });

                CameraSelector cameraSelector =
                        CameraSelector.DEFAULT_FRONT_CAMERA;

                cameraProvider.unbindAll();

                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageAnalysis
                );

            } catch (Exception e) {

                e.printStackTrace();
            }

        }, ContextCompat.getMainExecutor(this));
    }

    private void processFrame(Bitmap bitmap, Face face) {

        long now = System.currentTimeMillis();

        if(now - lastCaptureTime < 500)
            return;

        lastCaptureTime = now;

        Rect bounds = face.getBoundingBox();

        int left = Math.max(bounds.left, 0);
        int top = Math.max(bounds.top, 0);
        int width = Math.min(bounds.width(), bitmap.getWidth() - left);
        int height = Math.min(bounds.height(), bitmap.getHeight() - top);

        if (width <= 0 || height <= 0) return;

        Bitmap faceBitmap =
                Bitmap.createBitmap(bitmap, left, top, width, height);

        processWithAugmentation(faceBitmap);

        framesCapturedForAction++;

        showToast("Photo " + framesCapturedForAction + "/" + FRAMES_PER_ACTION);

        if(framesCapturedForAction >= FRAMES_PER_ACTION){
            framesCapturedForAction = 0;
            currentAction = getNextAction(currentAction);
        }
    }
    private void showToast(String msg) {

        if(currentToast != null)
            currentToast.cancel();

        currentToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        currentToast.show();
    }


    private boolean updateFaceAction(Face face) {

        float yaw = face.getHeadEulerAngleY();
        float pitch = face.getHeadEulerAngleX();

        switch (currentAction) {

            case CENTER:

                faceGuideOverlay.setAction(FaceAction.CENTER);
                showToast("Look straight");

                if (Math.abs(yaw) < 10 && Math.abs(pitch) < 10) {
                    return true;
                }
                break;

            case LOOK_LEFT:

                faceGuideOverlay.setAction(FaceAction.LOOK_LEFT);

                if (yaw > 20) {
                    return true;
                }
                break;

            case LOOK_RIGHT:

                faceGuideOverlay.setAction(FaceAction.LOOK_RIGHT);

                if (yaw < -20) {
                    return true;
                }
                break;

            case LOOK_UP:

                faceGuideOverlay.setAction(FaceAction.LOOK_UP);

                if (pitch < -15) {
                    return true;
                }
                break;

            case LOOK_DOWN:

                faceGuideOverlay.setAction(FaceAction.LOOK_DOWN);

                if (pitch > 15) {
                    return true;
                }
                break;

            case BLINK:

                faceGuideOverlay.setAction(FaceAction.BLINK);

                if (face.getLeftEyeOpenProbability() != null &&
                        face.getLeftEyeOpenProbability() < 0.3) {

                    currentAction = FaceAction.DONE;
                    return true;
                }
                break;

            case DONE:
                return false;
        }

        return false;
    }

    private FaceAction getNextAction(FaceAction action){

        switch (action){

            case CENTER:
                showToast("Turn your head left");
                return FaceAction.LOOK_LEFT;

            case LOOK_LEFT:
                showToast("Turn your head right");
                return FaceAction.LOOK_RIGHT;

            case LOOK_RIGHT:
                showToast("Look up");
                return FaceAction.LOOK_UP;

            case LOOK_UP:
                showToast("Look down");
                return FaceAction.LOOK_DOWN;

            case LOOK_DOWN:
                showToast("Blink your eyes");
                return FaceAction.BLINK;

            case BLINK:
                return FaceAction.DONE;

            default:
                return FaceAction.DONE;
        }
    }

    private void processWithAugmentation(Bitmap faceBitmap) {

        Bitmap resized = Bitmap.createScaledBitmap(faceBitmap, 160, 160, true);
        float[] emb = runModel(resized);
        emb = normalize(emb);

        List<Double> embeddingList = new ArrayList<>();
        for(float v : emb) embeddingList.add((double) v);

        if(currentAction == FaceAction.CENTER) {

            if(centerEmbeddings.size() < CENTER_EMBEDDINGS){
                centerEmbeddings.add(embeddingList);
            }

        } else {

            if(actionEmbeddings.size() < 5){
                actionEmbeddings.add(embeddingList);
            }

        }

        Toast.makeText(this, "Face captured", Toast.LENGTH_SHORT).show();
    }

    private Bitmap changeBrightness(Bitmap bmp, float factor) {
        Bitmap newBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        for (int x = 0; x < bmp.getWidth(); x++) {
            for (int y = 0; y < bmp.getHeight(); y++) {
                int pixel = bmp.getPixel(x, y);
                int r = Math.min(255, (int)(((pixel >> 16) & 0xFF) * factor));
                int g = Math.min(255, (int)(((pixel >> 8) & 0xFF) * factor));
                int b = Math.min(255, (int)((pixel & 0xFF) * factor));
                newBitmap.setPixel(x, y, (0xFF << 24) | (r << 16) | (g << 8) | b);
            }
        }
        return newBitmap;
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
            inputBuffer.putFloat(r); inputBuffer.putFloat(g); inputBuffer.putFloat(b);
        }

        float[][] output = new float[1][128];
        tflite.run(inputBuffer, output);
        return output[0];
    }

    private float[] normalize(float[] emb) {
        float sum = 0f;
        for (float v : emb) sum += v * v;
        float norm = (float) Math.sqrt(sum);
        for (int i = 0; i < emb.length; i++) emb[i] /= norm;
        return emb;
    }

    private List<Double> computeAverageEmbedding(List<List<Double>> embeddings){

        int dim = embeddings.get(0).size();
        double[] avg = new double[dim];

        for(List<Double> emb : embeddings){
            for(int i=0;i<dim;i++){
                avg[i] += emb.get(i);
            }
        }

        for(int i=0;i<dim;i++){
            avg[i] /= embeddings.size();
        }

        List<Double> result = new ArrayList<>();
        for(double v : avg) result.add(v);

        return result;
    }

    private List<List<Double>> buildFinalEmbeddings(){

        List<List<Double>> finalList = new ArrayList<>();

        finalList.addAll(centerEmbeddings);
        finalList.addAll(actionEmbeddings);

        List<List<Double>> all = new ArrayList<>();
        all.addAll(centerEmbeddings);
        all.addAll(actionEmbeddings);

        List<Double> avg = computeAverageEmbedding(all);

        finalList.add(avg);

        return finalList;
    }

}