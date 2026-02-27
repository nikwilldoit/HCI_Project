package com.example.phasmatic.ui;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
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
import androidx.camera.video.MediaStoreOutputOptions;
import androidx.camera.video.PendingRecording;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.VideoCapture;
import androidx.camera.video.VideoRecordEvent;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;

import com.example.phasmatic.R;
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
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import android.media.MediaMetadataRetriever;


public class RegisterActivity extends AppCompatActivity {

    EditText edtEmailAddressReg, edtPasswordReg, edtFullNameReg, edtPhoneNumberReg;
    Button btnRegisterReg, btnLoginReg, btnCaptureFace, btnTakePhoto, btnTakeVideo;
    TextView txtDisplayInfoReg;

    private Interpreter tflite;
    DatabaseReference usersRef;
    DatabaseReference usersfaceembeddingRef;
    private static final int CAMERA_PERMISSION_CODE = 100;

    private PreviewView viewFinder;
    private ImageCapture imageCapture;
    private VideoCapture<Recorder> videoCapture;

    private Recorder recorder;
    private FrameLayout cameraLayout;
    private android.view.View registerLayout;

    private PendingRecording pendingRecording;
    private androidx.camera.video.Recording activeRecording;


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
        btnTakeVideo   = findViewById(R.id.btnTakeVideo);
        txtDisplayInfoReg = findViewById(R.id.txtDisplayInfoReg);

        cameraLayout   = findViewById(R.id.cameraLayout);
        registerLayout = findViewById(R.id.registerLayout);
        viewFinder     = findViewById(R.id.viewFinder);

        //Firebase
        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        usersRef = firebaseDb.getReference("users");

        usersfaceembeddingRef = firebaseDb.getReference("users_face_embedding");

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

        //take video
        btnTakeVideo.setOnClickListener(v -> onVideoButtonClicked());

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

        if (finalEmbeddingsList.isEmpty()) {
            Toast.makeText(this, "Please capture your face first",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = usersRef.push().getKey();

        String user_face_embeddingId = usersfaceembeddingRef.push().getKey();

        if (userId != null) {


            User user = new User(
                    userId,
                    fullname,
                    email,
                    password,
                    phone
            );


            User_Face_Embedding userFaceEmbedding = new User_Face_Embedding(
                    user_face_embeddingId,
                    userId,
                    finalEmbeddingsList,
                    user
            );

            usersRef.child(userId).setValue(user)
                    .addOnSuccessListener(unused -> {

                        usersfaceembeddingRef.child(user_face_embeddingId)
                                .setValue(userFaceEmbedding)
                                .addOnSuccessListener(u -> {
                                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish();
                                });

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

                recorder = new Recorder.Builder().setQualitySelector(QualitySelector.from(Quality.HIGHEST)).build();
                videoCapture = VideoCapture.withOutput(recorder);

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, videoCapture);

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


                            detectFaceAndProcess(cropped);

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

    private void onVideoButtonClicked() {
        if (videoCapture == null) {
            Toast.makeText(this, "Camera not ready yet", Toast.LENGTH_SHORT).show();
            return;
        }

        if (activeRecording == null) {
            //start recording
            startVideoRecording();
        } else {
            //stop recording
            stopVideoRecording();
        }
    }

    private void startVideoRecording() {
        String fullName = edtFullNameReg.getText().toString().trim();
        if (fullName.isEmpty()) {
            Toast.makeText(this, "Please enter your full name first", Toast.LENGTH_SHORT).show();
            return;
        }

        String safeFileName = fullName.replaceAll("[^a-zA-Z0-9]", "_");

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, safeFileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/FaceReg");
        }

        MediaStoreOutputOptions mediaStoreOutputOptions =
                new MediaStoreOutputOptions.Builder(
                        getContentResolver(),
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                        .setContentValues(contentValues)
                        .build();

        pendingRecording = videoCapture.getOutput()
                .prepareRecording(this, mediaStoreOutputOptions);

        activeRecording = pendingRecording.start(
                ContextCompat.getMainExecutor(this),
                videoRecordEvent -> {
                    if (videoRecordEvent instanceof VideoRecordEvent.Start) {
                        btnTakeVideo.setText("Stop Video");
                    } else if (videoRecordEvent instanceof VideoRecordEvent.Finalize) {
                        btnTakeVideo.setText("Start Video");
                        activeRecording = null;

                        VideoRecordEvent.Finalize finalizeEvent =
                                (VideoRecordEvent.Finalize) videoRecordEvent;

                        if (finalizeEvent.getError() == VideoRecordEvent.Finalize.ERROR_NONE) {
                            Toast.makeText(this, "Video saved!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Video error: " + finalizeEvent.getError(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void stopVideoRecording() {
        if (activeRecording != null) {
            activeRecording.stop();
            activeRecording = null;
        }
    }

    //frames apo to video
    private List<Bitmap> extractFramesFromVideo(Uri videoUri, int frameCount) {
        List<Bitmap> frames = new ArrayList<>();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(this, videoUri);

            String durationStr = retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION);
            long durationMs = Long.parseLong(durationStr);

            for (int i = 0; i < frameCount; i++) {
                long timeUs = (durationMs * 1000L * (i + 1)) / (frameCount + 1);
                Bitmap frame = retriever.getFrameAtTime(
                        timeUs,
                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                );
                if (frame != null) {
                    frames.add(frame);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return frames;
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

    private List<List<Double>> finalEmbeddingsList = new ArrayList<>();

    private void processWithAugmentation(Bitmap faceBitmap) {

        List<float[]> embeddings = generateEmbeddings(faceBitmap);

        finalEmbeddingsList.clear();

        for (float[] emb : embeddings) {
            List<Double> list = new ArrayList<>();
            for (float f : emb) list.add((double) f);
            finalEmbeddingsList.add(list);
        }

        Toast.makeText(this, "Face processed successfully!", Toast.LENGTH_SHORT).show();
    }


    private void detectFaceAndProcess(Bitmap bitmap) {

        InputImage image = InputImage.fromBitmap(bitmap, 0);

        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .build();

        FaceDetector detector = FaceDetection.getClient(options);

        detector.process(image)
                .addOnSuccessListener(faces -> {

                    if (faces.isEmpty()) {
                        Toast.makeText(this, "No face detected", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Face face = faces.get(0);
                    Rect bounds = face.getBoundingBox();

                    Bitmap faceBitmap = Bitmap.createBitmap(
                            bitmap,
                            Math.max(bounds.left, 0),
                            Math.max(bounds.top, 0),
                            Math.min(bounds.width(), bitmap.getWidth() - bounds.left),
                            Math.min(bounds.height(), bitmap.getHeight() - bounds.top)
                    );

                    processWithAugmentation(faceBitmap);

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Face detection failed", Toast.LENGTH_SHORT).show();
                });
    }

    private List<Bitmap> augmentImage(Bitmap original) {

        List<Bitmap> augmented = new ArrayList<>();

        augmented.add(original);

        try {
            Matrix flipMatrix = new Matrix();
            flipMatrix.preScale(-1.0f, 1.0f);
            augmented.add(Bitmap.createBitmap(original, 0, 0,
                    original.getWidth(), original.getHeight(), flipMatrix, true));
        } catch (Exception ignored) {}

        try {
            augmented.add(changeBrightness(original, 1.1f));
            augmented.add(changeBrightness(original, 0.9f));
        } catch (Exception ignored) {}

        try {
            Matrix rotate = new Matrix();
            rotate.postRotate(5);
            augmented.add(Bitmap.createBitmap(original, 0, 0,
                    original.getWidth(), original.getHeight(), rotate, true));
        } catch (Exception ignored) {}

        return augmented;
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


    private List<float[]> generateEmbeddings(Bitmap faceBitmap) {

        List<float[]> embeddings = new ArrayList<>();

        List<Bitmap> augmentedImages = augmentImage(faceBitmap);

        for (Bitmap bmp : augmentedImages) {

            Bitmap resized = Bitmap.createScaledBitmap(bmp, 160, 160, true);
            float[] emb = runModel(resized);

            embeddings.add(normalize(emb));
        }

        return embeddings;
    }


    private float[] normalize(float[] emb) {
        float sum = 0f;
        for (float v : emb) sum += v * v;
        float norm = (float) Math.sqrt(sum);

        for (int i = 0; i < emb.length; i++) {
            emb[i] /= norm;
        }
        return emb;
    }


}
