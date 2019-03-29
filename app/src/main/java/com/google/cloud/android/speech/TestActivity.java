package com.google.cloud.android.speech;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.toasterlibrary.ToasterMessage;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "GoogleVoiceToText";
    MediaPlayer mediaPlayer = new MediaPlayer();
    GetQuestion sinhalaQuestion = new GetQuestion();
    TextToSpeech ts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ToasterMessage.s(TestActivity.this, "SAMPLE TEST");

        sinhalaQuestion.setQuestion("ජ්යෙෂ්ඨ පුරවැසි ගිණුමක් විවෘත කළ හැක්කේ කෙසේද");
        ts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Set<String> a = new HashSet<>();
                    a.add("male");
                    Voice voice = new Voice("en-us-x-sfg#male_2-local", new Locale("si", "LK"), 400, 200, true, a);
                    ts.setVoice(voice);
                }
            }
        });

//        byte[] decoded = Base64.decode(getTermsString(), 0);

//        playMp3(decoded);

//        File file2=null;
//        try
//        {
//            file2 = new File("/sdcard/hello-5.wav");
////            file2 = new File( Environment.getExternalStorageDirectory().getAbsolutePath() + "/hello-5.wav");
//            FileOutputStream os = new FileOutputStream(file2, true);
//            os.write(decoded);
//            os.close();
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//        try{
//            String filePath = "/sdcard/hello-5.wav";
//            mediaPlayer = new  MediaPlayer();
//            mediaPlayer.setDataSource(filePath);
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//        }
//        catch(Exception e){
//            e.printStackTrace();
//        }
    }

    private void playMp3(byte[] mp3SoundByteArray) {
        try {
            // create temp file that will hold byte array
            File tempMp3 = File.createTempFile("temp_audio", "mp3", getCacheDir());
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(mp3SoundByteArray);
            fos.close();

            // resetting mediaplayer instance to evade problems
            mediaPlayer.reset();

            // In case you run into issues with threading consider new instance like:
            // MediaPlayer mediaPlayer = new MediaPlayer();

            // Tried passing path directly, but kept getting
            // "Prepare failed.: status=0x1"
            // so using file descriptor instead
            FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());

            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }

    private String getTermsString() {
        StringBuilder termsString = new StringBuilder();
        BufferedReader reader;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("audio_record.txt")));

            String str;
            while ((str = reader.readLine()) != null) {
                termsString.append(str);
            }

            reader.close();
            return termsString.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onPause() {
        if (ts != null) {
            ts.stop();
            ts.shutdown();
        }
        super.onPause();
    }
}


