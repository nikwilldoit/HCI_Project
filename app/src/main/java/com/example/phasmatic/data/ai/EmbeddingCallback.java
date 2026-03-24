package com.example.phasmatic.data.ai;

public interface EmbeddingCallback {
    void onSuccess(float[] embedding);
    void onError(String error);
}
