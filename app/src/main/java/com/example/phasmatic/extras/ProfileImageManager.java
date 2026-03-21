package com.example.phasmatic.extras;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ProfileImageManager {

    private static final String FOLDER_NAME = "profile_images";

    public static String saveBitmap(Context context, String userId, Bitmap bitmap) {
        try {
            File folder = new File(context.getFilesDir(), FOLDER_NAME);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            File imageFile = new File(folder, userId + ".jpg");

            FileOutputStream fos = new FileOutputStream(imageFile, false);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();

            return imageFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String saveUri(Context context, String userId, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            return saveBitmap(context, userId, bitmap);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap loadBitmap(Context context, String userId) {
        try {
            File folder = new File(context.getFilesDir(), FOLDER_NAME);
            File imageFile = new File(folder, userId + ".jpg");

            if (imageFile.exists()) {
                return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean hasImage(Context context, String userId) {
        File folder = new File(context.getFilesDir(), FOLDER_NAME);
        File imageFile = new File(folder, userId + ".jpg");
        return imageFile.exists();
    }

    public static String getImagePath(Context context, String userId) {
        File folder = new File(context.getFilesDir(), FOLDER_NAME);
        File imageFile = new File(folder, userId + ".jpg");
        return imageFile.getAbsolutePath();
    }
}
