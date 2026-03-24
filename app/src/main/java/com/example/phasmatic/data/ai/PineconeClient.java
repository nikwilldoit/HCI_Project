package com.example.phasmatic.data.ai;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PineconeClient {

    private static final String BASE_URL = "https://....../query";
    private static final String API_KEY = "pcsk_4cqDGB_56PZTCSRhrvUwzcUBvpQFGJpRKU12kv4tEqVayuDFhZXbLgZuKYxcDdiGi5G3kc";

    private final OkHttpClient client = new OkHttpClient();

    public interface PineconeCallback {
        void onSuccess(String context);
        void onError(String error);
    }

    public void query(float[] vector, PineconeCallback callback) {

        try {
            JSONObject body = new JSONObject();
            body.put("vector", new JSONArray(vector));
            body.put("topK", 5);
            body.put("includeMetadata", true);

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .addHeader("Api-Key", API_KEY)
                    .post(RequestBody.create(body.toString(),
                            MediaType.get("application/json")))
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        String res = response.body().string();

                        JSONObject json = new JSONObject(res);
                        JSONArray matches = json.getJSONArray("matches");

                        StringBuilder context = new StringBuilder();

                        for (int i = 0; i < matches.length(); i++) {
                            JSONObject meta = matches.getJSONObject(i)
                                    .getJSONObject("metadata");

                            context.append("Program: ").append(meta.optString("title")).append("\n").append("Country: ").append(meta.optString("country")).append("\n").append("Field: ").append(meta.optString("field")).append("\n").append("Description: ").append(meta.optString("description")).append("\n\n");
                        }

                        callback.onSuccess(context.toString());

                    } catch (Exception e) {
                        callback.onError(e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }
}