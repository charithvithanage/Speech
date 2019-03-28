package com.google.cloud.android.speech;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.toasterlibrary.ToasterMessage;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "GoogleVoiceToText";
    MediaPlayer mediaPlayer = new MediaPlayer();
    SinhalaQuestion sinhalaQuestion=new SinhalaQuestion();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ToasterMessage.s(TestActivity.this,"SAMPLE TEST");

        sinhalaQuestion.setQuestion("මගේ දරුවන්ට ගිණුමක් විවෘත කළ හැක්කේ කෙසේ");

        new CheckQuestionAsync().execute();

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

    private class CheckQuestionAsync extends AsyncTask<Void, Void, String> {



        @Override
        protected String doInBackground(Void... voids) {
            ApiService policyService = ApiService.getInstance();
            final String[] returnResult = {null};

            policyService.checkSinhalaQuestion(TestActivity.this, sinhalaQuestion, new VolleyCallback() {
                @Override
                public void onSuccessResponse(String result) {
                    JSONObject jsonObject;
                    String status;
                    try {
                        jsonObject = new JSONObject(result);

                        JSONArray jsonArray=jsonObject.getJSONArray("object");

                        JSONObject jsonObject1=jsonArray.getJSONObject(0);

                        returnResult[0] =jsonObject1.getString("audioString");



                        Log.d(TAG,jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getApplicationContext(), error,Toast.LENGTH_SHORT).show();

                }
            });


            return returnResult[0];
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);


        byte[] decoded = Base64.decode(str, 0);

        playMp3(decoded);

        }
    }

}


