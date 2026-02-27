package com.example.phasmatic.data.ai;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

public class OpenAIChatClient {

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private final FirebaseDatabase firebaseDb;

    public interface ChatCallback {
        void onSuccess(String reply);
        void onError(String error);
    }

    public OpenAIChatClient(Context context) {
        firebaseDb = FirebaseDatabase.getInstance();
    }

    public void sendMessage(String userMessage, ChatCallback callback) {
        //perno to api kateuthian apo to db xwris security
        firebaseDb.getReference("api_keys/0/api_key")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String apiKey = snapshot.getValue(String.class);
                        if (apiKey == null || apiKey.isEmpty()) {
                            callback.onError("API key not found in DB");
                            return;
                        }
                        //afou exw to key kanw call openai
                        callOpenAI(apiKey, userMessage, callback);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError("Firebase error: " + error.getMessage());
                    }
                });
    }

    private void callOpenAI(String apiKey, String userMessage, ChatCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("model", "gpt-4o-mini"); //den exw apofasisei akoma gia to pio tha xrhsimopoihsoume logika 5 mini

            JSONArray messages = new JSONArray();
            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", "you are a helpful assistant for study planning.");
            messages.put(systemMsg);

            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.put(userMsg);

            body.put("messages", messages);

            RequestBody reqBody = RequestBody.create(body.toString(), JSON);

            Request request = new Request.Builder()
                    .url(OPENAI_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(reqBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onError(OpenAIErrorHandler.mapClientException(e.getMessage()));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        if (!response.isSuccessful()) {
                            String bodyText = response.body() != null ? response.body().string() : "";
                            String niceError = OpenAIErrorHandler.mapHttpError(response.code(), bodyText);
                            callback.onError(niceError);
                            return;
                        }

                        if (response.body() == null) {
                            callback.onError(OpenAIErrorHandler.mapParseException("empty body"));
                            return;
                        }

                        String respBody = response.body().string();
                        JSONObject json = new JSONObject(respBody);
                        JSONArray choices = json.getJSONArray("choices");
                        if (choices.length() == 0) {
                            callback.onError(OpenAIErrorHandler.mapParseException("no choices"));
                            return;
                        }

                        JSONObject first = choices.getJSONObject(0);
                        JSONObject message = first.getJSONObject("message");
                        String content = message.getString("content");
                        callback.onSuccess(content.trim());

                    } catch (Exception ex) {
                        callback.onError(OpenAIErrorHandler.mapParseException(ex.getMessage()));
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
