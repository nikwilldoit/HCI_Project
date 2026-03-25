package com.example.phasmatic.extras;

import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SupabaseStorageHelper {

    //apo to Supabase
    private static final String SUPABASE_URL = "https://sbzxqcwvbbgbpykyvmfa.supabase.co";
    private static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNienhxY3d2YmJnYnB5a3l2bWZhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQ0MjcwNDEsImV4cCI6MjA5MDAwMzA0MX0.oUc-uXUKPE6HJS7peW3ytfW1H5uSTFP6vUa_8Zn7iuo";

    //to sugekrimeno backet:
    private static final String BUCKET_NAME = "avatars";

    private static final OkHttpClient client = new OkHttpClient();

    @Nullable
    public static String uploadImageBytes(byte[] bytes, String path) {
        String url = SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + path;

        RequestBody body = RequestBody.create(bytes, MediaType.parse("image/jpeg"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                .addHeader("Content-Type", "image/jpeg")
                .addHeader("x-upsert", "true")          // σημαντικό
                .build();

        try (Response response = client.newCall(request).execute()) {
            String respBody = response.body() != null ? response.body().string() : "";
            System.out.println("Supabase upload response: " + response.code() + " " + respBody);

            if (!response.isSuccessful()) {
                return null;
            }

            return SUPABASE_URL + "/storage/v1/object/public/" + BUCKET_NAME + "/" + path;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static byte[] readAllBytes(java.io.InputStream is) throws IOException {
        byte[] buffer = new byte[8192];
        int n;
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        while ((n = is.read(buffer)) != -1) {
            baos.write(buffer, 0, n);
        }
        return baos.toByteArray();
    }
}