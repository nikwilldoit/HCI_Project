package com.example.phasmatic.data.ai;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;

public class OpenAIChatClient {

    private static final String OPENAI_URL =
            "https://api.openai.com/v1/chat/completions";

    private static final MediaType JSON =
            MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private final FirebaseDatabase firebaseDb;

    public interface ChatCallback {
        void onSuccess(String reply);
        void onError(String error);
    }

    public OpenAIChatClient(Context context) {
        firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
    }

    public void sendMessage(String userMessage, ChatCallback callback) {
        Log.d("OpenAI", "sendMessage: " + userMessage);

        firebaseDb.getReference("api_keys/0/api_key")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String apiKey = snapshot.getValue(String.class);
                        Log.d("OpenAI", "apiKey from DB: " + apiKey);

                        if (apiKey == null || apiKey.isEmpty()) {
                            callback.onError("API key not found");
                            return;
                        }
                        callOpenAI(apiKey, userMessage, callback);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("OpenAI", "Firebase error: " + error.getMessage());
                        callback.onError("Firebase error: " + error.getMessage());
                    }
                });
    }

    private void callOpenAI(String apiKey,
                            String userMessage,
                            ChatCallback callback) {

        Log.d("OpenAI", "Calling OpenAI...");

        try {

            JSONObject body = new JSONObject();
            body.put("model", "gpt-4o-mini");

            JSONArray messages = new JSONArray();

            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content",
                    "You are a helpful assistant for study planning.");

            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);

            messages.put(systemMsg);
            messages.put(userMsg);

            body.put("messages", messages);

            RequestBody reqBody =
                    RequestBody.create(body.toString(), JSON);

            Request request = new Request.Builder()
                    .url(OPENAI_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(reqBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(@NonNull Call call,
                                      @NonNull IOException e) {

                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call,
                                       @NonNull Response response) {

                    try {

                        if (!response.isSuccessful()) {

                            String bodyText = response.body() != null
                                    ? response.body().string()
                                    : "";

                            callback.onError(
                                    "HTTP " + response.code() + " " + bodyText);

                            return;
                        }

                        if (response.body() == null) {
                            callback.onError("Empty response body");
                            return;
                        }

                        String respBody = response.body().string();

                        JSONObject json = new JSONObject(respBody);
                        JSONArray choices = json.getJSONArray("choices");

                        if (choices.length() == 0) {
                            callback.onError("No response choices");
                            return;
                        }

                        JSONObject message =
                                choices.getJSONObject(0)
                                        .getJSONObject("message");

                        String content = message.getString("content");

                        callback.onSuccess(content.trim());

                    } catch (Exception ex) {
                        callback.onError("Parse error: " + ex.getMessage());
                    } finally {
                        response.close();
                    }
                }
            });

        } catch (Exception e) {
            callback.onError("Client error: " + e.getMessage());
        }
    }
}