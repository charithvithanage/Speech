package com.google.cloud.android.speech;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class FirstPage extends AppCompatActivity implements MessageDialogFragment.Listener {

    private static final int MY_DATA_CHECK_CODE = 200;
    private Button btnEnglish, btnSinhala, btnTamil;
    TextToSpeech ts;

    private static final String FRAGMENT_MESSAGE_DIALOG = "message_dialog";

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;

    private SpeechService mSpeechService;

    private VoiceRecorder mVoiceRecorder;

    private static final String TAG = "GoogleVoiceToText";

    String status = "startStatus";

    LinearLayout btnLayout;

    ImageView imageView;

    TextView tvWait;

    GetQuestion getQuestion = new GetQuestion();

    ListView listView;

    private android.app.ProgressDialog dialog;

    Boolean selectExactQuestion = true;

    MediaPlayer mediaPlayer = new MediaPlayer();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        Log.d(TAG, "onCreated()");

        init();


        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectEnglish();
            }
        });


        btnSinhala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectSinhala();
            }
        });


        btnTamil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTamil();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "OnStart()");

        // Prepare Cloud Speech API

        // Start listening to voices
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            startVoiceRecorder();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            showPermissionMessageDialog();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    public void onPause() {
        if (ts != null) {
            ts.stop();
            ts.shutdown();
        }
        super.onPause();
    }


    private void init() {
        listView = findViewById(R.id.questionList);
        tvWait = findViewById(R.id.tvWait);
//        tvWait.setText("Say something....");

        dialog = new android.app.ProgressDialog(FirstPage.this);
        imageView = findViewById(R.id.ivAnimation);
//        hearingImageView = findViewById(R.id.hearingImageView);
//        hearingImageView.setVisibility(View.GONE);
        Glide.with(this).load(R.raw.welcome_gif).into(imageView);
//        Glide.with(this).load(R.raw.listning).into(hearingImageView);
        btnEnglish = findViewById(R.id.btnEnglish);
        btnSinhala = findViewById(R.id.btnSinhala);
        btnTamil = findViewById(R.id.btnTamil);
        btnLayout = findViewById(R.id.btnLayout);

        btnLayout.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);

//        tvWait.setVisibility(View.GONE);

//        final Resources resources = getResources();
//        final Resources.Theme theme = getTheme();
//        mColorHearing = ResourcesCompat.getColor(resources, R.color.status_hearing, theme);
//        mColorNotHearing = ResourcesCompat.getColor(resources, R.color.status_not_hearing, theme);

//        mStatus = findViewById(R.id.status);
    }

    private void startVoiceRecorder() {

        Log.d(TAG, "startVoiceRecord()");

        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
        }
        mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
        mVoiceRecorder.start();

        tvWait.setText("Listening");


        Log.d(TAG, "service started()");

        bindService(new Intent(FirstPage.this, SpeechService.class), mServiceConnection, BIND_AUTO_CREATE);

    }

    private void stopVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
            mVoiceRecorder = null;
        }
    }

    private void showPermissionMessageDialog() {
        MessageDialogFragment
                .newInstance(getString(R.string.permission_message))
                .show(getSupportFragmentManager(), FRAGMENT_MESSAGE_DIALOG);
    }


    private void showStatus(final boolean hearingVoice) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                Glide.with(FirstPage.this).load(hearingVoice ? R.raw.listning : R.raw.waiting).into(hearingImageView);


                tvWait.setText(hearingVoice ? "Listening" : "Say something....");
//                tvWait.setTextColor(hearingVoice ? mColorHearing : mColorNotHearing);
            }
        });
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            Log.d(TAG, "onServiceConnected()");

            mSpeechService = SpeechService.from(binder);
            mSpeechService.addListener(mSpeechServiceListener);
//            mStatus.setVisibility(View.VISIBLE);
            ts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        Set<String> a = new HashSet<>();
                        a.add("male");
                        Voice voice = new Voice("en-us-x-sfg#male_2-local", new Locale("en", "US"), 400, 200, true, a);
                        ts.setVoice(voice);
//                        ts.setLanguage(Locale.forLanguageTag("en-US"));

//                        startVoiceRecorder();
                    }
                }
            });
        }


        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected()");

            mSpeechService = null;
        }

    };

    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

        @Override
        public void onVoiceStart() {
            Log.d(TAG, "mVoice callback onVoiceStart()");

            showStatus(true);
            if (mSpeechService != null) {
                mSpeechService.startRecognizing(mVoiceRecorder.getSampleRate());
            }
        }

        @Override
        public void onVoice(byte[] data, int size) {
            if (mSpeechService != null) {
                mSpeechService.recognize(data, size);
            }
        }

        @Override
        public void onVoiceEnd() {
            Log.d(TAG, "mVoice callback onVoiceEnd()");

            showStatus(false);
            if (mSpeechService != null) {
                mSpeechService.finishRecognizing();
            }
        }

    };


    private void selectSinhala() {
        status = "questionStatus";

        Config.Instance.setLanguageCode("si-LK");

        Set<String> a = new HashSet<>();
        a.add("male");
        Voice voice = new Voice("en-us-x-sfg#male_2-local", new Locale("si", "LK"), 400, 200, true, a);
        ts.setVoice(voice);
        ts.speak("සින්හල තෝරාගත්තා. ප්රශ්න සින්හලෙන් අහන්න", TextToSpeech.QUEUE_FLUSH, null);

        btnLayout.setVisibility(View.GONE);
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                                            tvWait.setText("Say something.....");
                startVoiceRecorder();
            }
        }, 5000);

    }

    private void selectEnglish() {
        status = "questionStatus";

        Config.Instance.setLanguageCode("en-US");
        ts.speak("English selected. please ask your questions in english", TextToSpeech.QUEUE_FLUSH, null);

        btnLayout.setVisibility(View.GONE);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                                            tvWait.setText("Say something.....");
                startVoiceRecorder();
            }
        }, 5000);

    }

    private void selectTamil() {
        status = "questionStatus";

        Config.Instance.setLanguageCode("ta-LK");
        Set<String> a = new HashSet<>();
        a.add("male");
        Voice voice = new Voice("en-us-x-sfg#male_2-local", new Locale("ta", "LK"), 400, 200, true, a);
        ts.setVoice(voice);

        ts.speak("தமிழ் தெரிவு செய்துள்ளீர்கள். உங்கள் கேள்விகளை தமிழ் மொழியில் கேளுங்கள்", TextToSpeech.QUEUE_FLUSH, null);
        btnLayout.setVisibility(View.GONE);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                                            tvWait.setText("Say something.....");
                startVoiceRecorder();
            }
        }, 6000);

    }


    @Override
    protected void onStop() {
        // Stop listening to voice
        stopVoiceRecorder();

        // Stop Cloud Speech API
        if (mSpeechService != null) {
            mSpeechService.removeListener(mSpeechServiceListener);
        }

        unbindService(mServiceConnection);

        mSpeechService = null;

        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (permissions.length == 1 && grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startVoiceRecorder();
            } else {
                showPermissionMessageDialog();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onMessageDialogDismissed() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_RECORD_AUDIO_PERMISSION);
    }

    private final SpeechService.Listener mSpeechServiceListener =
            new SpeechService.Listener() {
                @Override
                public void onSpeechRecognized(final String text, final boolean isFinal) {

                    if (!TextUtils.isEmpty(text)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isFinal) {

                                    stopVoiceRecorder();

                                    Log.d(TAG, text);


                                    if (status.equals("startStatus")) {
                                        if (text.toLowerCase().equals("hello sam")) {
                                            imageView.setVisibility(View.VISIBLE);
                                            status = "selectLanguageStatus";
                                            ts.speak("Hello I'm Sam, Please select your language", TextToSpeech.QUEUE_FLUSH, null);
                                            btnLayout.setVisibility(View.VISIBLE);
                                            imageView.setVisibility(View.VISIBLE);

                                        } else {


                                            ts.speak("Say hello sam", TextToSpeech.QUEUE_FLUSH, null);

                                        }

                                        Handler handler = new Handler();

                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
//                                            tvWait.setText("Say something.....");
                                                startVoiceRecorder();
                                            }
                                        }, 3000);
                                    } else if (status.equals("selectLanguageStatus")) {

                                        if (firstTwo(text.toLowerCase()).equals("en")) {
                                            selectEnglish();
                                        } else if (firstTwo(text.toLowerCase()).equals("ta")) {
                                            selectTamil();
                                        } else if (firstTwo(text.toLowerCase()).equals("si")) {
                                            selectSinhala();
                                        } else {
                                            ts.speak("Say it again", TextToSpeech.QUEUE_FLUSH, null);
                                            Handler handler = new Handler();

                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
//                                            tvWait.setText("Say something.....");
                                                    startVoiceRecorder();
                                                }
                                            }, 3);
                                        }



                                    } else {

                                        getQuestion.setQuestion(text);
                                        if (Config.Instance.getLanguageCode().equals("si-LK")) {
                                            new CheckSinhalaQuestionAsync().execute();
                                        } else if (Config.Instance.getLanguageCode().equals("ta-LK")) {
                                            new CheckTamilQuestionAsync().execute();
                                        } else {
                                            new CheckEnglishQuestionAsync().execute();
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            };

    public String firstTwo(String str) {
        return str.length() < 2 ? str : str.substring(0, 2);
    }

//    private class AddQuestionAsync extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            ApiService policyService = ApiService.getInstance();
//
//            policyService.addSinhalaQuestion(FirstPage.this, getQuestion, new VolleyCallback() {
//                @Override
//                public void onSuccessResponse(String result) {
//                    JSONObject jsonObject;
//                    String status;
//                    try {
//                        jsonObject = new JSONObject(result);
//
//                        Log.d(TAG, jsonObject.toString());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//
//                    }
//                }
//
//                @Override
//                public void onError(String error) {
//                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
//
//                }
//            });
//            return null;
//        }
//
//
//    }

    private class CheckSinhalaQuestionAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            stopVoiceRecorder();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ApiService policyService = ApiService.getInstance();
            final Gson gson = new Gson();

            final List<SampleObject> listOfSampleObject = new ArrayList<>();

            policyService.checkSinhalaQuestion(FirstPage.this, getQuestion, new VolleyCallback() {
                @Override
                public void onSuccessResponse(String result) {
                    JSONObject jsonObject;
                    String status;
                    try {
                        jsonObject = new JSONObject(result);


                        Object json = new JSONTokener(jsonObject.getString("object")).nextValue();

                        if (json instanceof JSONObject) {

                            String str = jsonObject.getString("object");
                            SampleObject sampleObject = new SampleObject();

                            sampleObject.setQuestion(str);
                            sampleObject.setCount(1);

                            listOfSampleObject.add(sampleObject);
                        } else if (json instanceof JSONArray) {

                            JSONArray jsonArray = jsonObject.getJSONArray("object");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                SampleObject sampleObject = new SampleObject();

                                sampleObject.setQuestion(jsonObject1.getString("sinhalaQuestion"));
                                sampleObject.setCount(jsonObject1.getInt("count"));


                                listOfSampleObject.add(sampleObject);
                            }

                        }

                        if (listOfSampleObject.size() == 1) {
                            Question sinhalaQuestion = gson.fromJson(Utils.sortCardList(listOfSampleObject).get(0).getQuestion(), Question.class);
                            byte[] decoded = Base64.decode(sinhalaQuestion.getAudioString(), 0);
                            playMp3(decoded);
                        } else if (listOfSampleObject.size() == 0) {
                            ts.speak("කරුණාකර පාරිභෝගික නියෝජිතයා අමතන්න", TextToSpeech.QUEUE_FLUSH, null);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startVoiceRecorder();
                                }
                            }, 3000);
                        } else {

                            ts.speak("ඔබ අදහස් කලේ", TextToSpeech.QUEUE_FLUSH, null);

                            listView.setVisibility(View.VISIBLE);

                            QuestionListAdapter questionListAdapter = new QuestionListAdapter(FirstPage.this, Utils.sortCardList(listOfSampleObject));

                            listView.setAdapter(questionListAdapter);

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    SampleObject sampleObject = (SampleObject) listView.getItemAtPosition(position);

                                    Question sinhalaQuestion = gson.fromJson(sampleObject.getQuestion(), Question.class);
                                    getQuestion.setQuestion(sinhalaQuestion.getQuestion());
                                    listView.setVisibility(View.GONE);

                                    new CheckSinhalaQuestionAsync().execute();

                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();

                }


            });


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Handler handler = new Handler();


            if (selectExactQuestion) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startVoiceRecorder();
                    }
                }, 3000);
            }
        }
    }

    private class CheckTamilQuestionAsync extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            ApiService policyService = ApiService.getInstance();
            final Gson gson = new Gson();

            final List<SampleObject> listOfSampleObject = new ArrayList<>();

            policyService.checkTamilQuestion(FirstPage.this, getQuestion, new VolleyCallback() {
                @Override
                public void onSuccessResponse(String result) {
                    JSONObject jsonObject;
                    String status;
                    try {
                        jsonObject = new JSONObject(result);


                        Object json = new JSONTokener(jsonObject.getString("object")).nextValue();

                        if (json instanceof JSONObject) {

                            String str = jsonObject.getString("object");
                            SampleObject sampleObject = new SampleObject();

                            sampleObject.setQuestion(str);
                            sampleObject.setCount(1);

                            listOfSampleObject.add(sampleObject);
                        } else if (json instanceof JSONArray) {

                            JSONArray jsonArray = jsonObject.getJSONArray("object");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                SampleObject sampleObject = new SampleObject();

                                sampleObject.setQuestion(jsonObject1.getString("tamilQuestion"));
                                sampleObject.setCount(jsonObject1.getInt("count"));


                                listOfSampleObject.add(sampleObject);
                            }

                        }

                        if (listOfSampleObject.size() == 1) {
                            Question sinhalaQuestion = gson.fromJson(Utils.sortCardList(listOfSampleObject).get(0).getQuestion(), Question.class);
                            byte[] decoded = Base64.decode(sinhalaQuestion.getAudioString(), 0);
                            playMp3(decoded);

                        } else if (listOfSampleObject.size() == 0) {
                            ts.speak("කරුණාකර පාරිභෝගික නියෝජිතයා අමතන්න", TextToSpeech.QUEUE_FLUSH, null);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startVoiceRecorder();
                                }
                            }, 3000);
                        } else {
                            ts.speak("ඔබ අදහස් කලේ", TextToSpeech.QUEUE_FLUSH, null);

                            listView.setVisibility(View.VISIBLE);

                            QuestionListAdapter questionListAdapter = new QuestionListAdapter(FirstPage.this, Utils.sortCardList(listOfSampleObject));

                            listView.setAdapter(questionListAdapter);

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    SampleObject sampleObject = (SampleObject) listView.getItemAtPosition(position);

                                    Question tamilQuestion = gson.fromJson(sampleObject.getQuestion(), Question.class);
                                    getQuestion.setQuestion(tamilQuestion.getQuestion());
                                    listView.setVisibility(View.GONE);

                                    new CheckTamilQuestionAsync().execute();

                                }
                            });

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();

                }


            });


            return null;
        }
    }

    private class CheckEnglishQuestionAsync extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            ApiService policyService = ApiService.getInstance();
            final Gson gson = new Gson();

            final List<SampleObject> listOfSampleObject = new ArrayList<>();

            policyService.checkEnglishaQuestion(FirstPage.this, getQuestion, new VolleyCallback() {
                @Override
                public void onSuccessResponse(String result) {
                    JSONObject jsonObject;
                    String status;
                    try {
                        jsonObject = new JSONObject(result);


                        Object json = new JSONTokener(jsonObject.getString("object")).nextValue();

                        if (json instanceof JSONObject) {

                            String str = jsonObject.getString("object");
                            SampleObject sampleObject = new SampleObject();

                            sampleObject.setQuestion(str);
                            sampleObject.setCount(1);

                            listOfSampleObject.add(sampleObject);
                        } else if (json instanceof JSONArray) {

                            JSONArray jsonArray = jsonObject.getJSONArray("object");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                SampleObject sampleObject = new SampleObject();

                                sampleObject.setQuestion(jsonObject1.getString("englishQuestion"));
                                sampleObject.setCount(jsonObject1.getInt("count"));


                                listOfSampleObject.add(sampleObject);
                            }

                        }

                        if (listOfSampleObject.size() == 1) {
                            Question sinhalaQuestion = gson.fromJson(Utils.sortCardList(listOfSampleObject).get(0).getQuestion(), Question.class);
                            byte[] decoded = Base64.decode(sinhalaQuestion.getAudioString(), 0);
                            playMp3(decoded);

                        } else if (listOfSampleObject.size() == 0) {
                            ts.speak("කරුණාකර පාරිභෝගික නියෝජිතයා අමතන්න", TextToSpeech.QUEUE_FLUSH, null);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startVoiceRecorder();
                                }
                            }, 3000);
                        } else {
                            ts.speak("ඔබ අදහස් කලේ", TextToSpeech.QUEUE_FLUSH, null);
                            listView.setVisibility(View.VISIBLE);

                            QuestionListAdapter questionListAdapter = new QuestionListAdapter(FirstPage.this, Utils.sortCardList(listOfSampleObject));

                            listView.setAdapter(questionListAdapter);

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    SampleObject sampleObject = (SampleObject) listView.getItemAtPosition(position);

                                    Question tamilQuestion = gson.fromJson(sampleObject.getQuestion(), Question.class);
                                    getQuestion.setQuestion(tamilQuestion.getQuestion());
                                    listView.setVisibility(View.GONE);

                                    new CheckEnglishQuestionAsync().execute();

                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();

                }


            });


            return null;
        }

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

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Toast.makeText(FirstPage.this, "Audio ended", Toast.LENGTH_SHORT).show();
                    startVoiceRecorder();
                }
            });
        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }


}
