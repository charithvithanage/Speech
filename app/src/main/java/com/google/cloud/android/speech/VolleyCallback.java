package com.google.cloud.android.speech;

public interface VolleyCallback {
    void onSuccessResponse(String result);

    void onError(String error);
}