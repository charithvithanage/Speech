package com.google.cloud.android.speech;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.toasterlibrary.ToasterMessage;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "GoogleVoiceToText";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ToasterMessage.s(TestActivity.this,"SAMPLE TEST");
    }
}


