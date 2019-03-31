package com.google.cloud.android.speech;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
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
import java.util.List;

public class FirstPage extends AppCompatActivity implements MessageDialogFragment.Listener {

    private static final int MY_DATA_CHECK_CODE = 200;
    private Button btnEnglish, btnSinhala, btnTamil;


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

    TextView tvAnswer;

    MediaPlayer mediaPlayer = new MediaPlayer();

    Button btnRestart, btnClose;

    WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        Log.d(TAG, "onCreated()");

        Config.Instance.setLanguageCode("en-US");

        init();


        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEnglish();
            }
        });


        btnSinhala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSinhala();
            }
        });


        btnTamil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTamil();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
    }

    private String getUrlFromString(String str) {

//        Log.d(TAG,String.valueOf(str.indexOf("https://")));
//        Log.d(TAG,String.valueOf(str.length()));
        String strEnd;

        if (str.contains("https://")) {
            int startPosition = str.indexOf("https://");
            int endPosition = str.length();
            strEnd = str.substring(startPosition, endPosition);

        } else {
            strEnd = "No url found";
        }


        return strEnd;
    }


    private Boolean checkUrlExistance(String str) {

        Boolean check = false;
        if (str.contains("https://")) {
            check = true;

        }

        return check;

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


    private void init() {
        btnRestart = findViewById(R.id.btnRestart);
        btnClose = findViewById(R.id.btnClose);
        listView = findViewById(R.id.questionList);
        tvAnswer = findViewById(R.id.tvAnswer);
        tvWait = findViewById(R.id.tvWait);
        listView.setVisibility(View.GONE);
//        tvWait.setText("Say something....");

        webView = findViewById(R.id.webView);
        webView.setVisibility(View.GONE);
        imageView = findViewById(R.id.ivAnimation);
//        hearingImageView = findViewById(R.id.hearingImageView);
//        hearingImageView.setVisibility(View.GONE);
        Glide.with(this).load("https://cdn.dribbble.com/users/1162077/screenshots/4649464/skatter-programmer.gif").into(imageView);
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
        stopVoiceRecorder();
        status = "questionStatus";

        Config.Instance.setLanguageCode("si-LK");
        playMp3ByRawFile(R.raw.select_language_sinhala);
        btnLayout.setVisibility(View.GONE);
    }

    private void selectEnglish() {
        stopVoiceRecorder();

        status = "questionStatus";

        Config.Instance.setLanguageCode("en-US");
        playMp3ByRawFile(R.raw.select_language_eng);
        btnLayout.setVisibility(View.GONE);

    }

    private void selectTamil() {
        stopVoiceRecorder();

        status = "questionStatus";

        Config.Instance.setLanguageCode("ta-LK");
        playMp3ByRawFile(R.raw.select_language_tamil);
        btnLayout.setVisibility(View.GONE);
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


                    if (isFinal) {
                        stopVoiceRecorder();
                    }

                    if (!TextUtils.isEmpty(text)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isFinal) {

                                    Log.d(TAG, text);

                                    if (status.equals("startStatus")) {
                                        if (text.toLowerCase().contains("hello")) {
                                            imageView.setVisibility(View.VISIBLE);
                                            status = "selectLanguageStatus";
                                            playMp3ByRawFile(R.raw.im_sam);
//                                            ts.speak("Hello I'm Sam, Please select your language", TextToSpeech.QUEUE_FLUSH, null);
                                            btnLayout.setVisibility(View.VISIBLE);
                                            imageView.setVisibility(View.VISIBLE);
                                        } else {
                                            playMp3ByRawFile(R.raw.say_hello_sam);
//                                            ts.speak("Say hello sam", TextToSpeech.QUEUE_FLUSH, null);
                                        }

                                    } else if (status.equals("selectLanguageStatus")) {

                                        if (firstTwo(text.toLowerCase()).equals("en")) {
                                            selectEnglish();
                                        } else if (firstTwo(text.toLowerCase()).equals("ta")) {
                                            selectTamil();
                                        } else if (firstTwo(text.toLowerCase()).equals("si")) {
                                            selectSinhala();
                                        } else {
                                            playMp3ByRawFile(R.raw.repeat);
//                                            ts.speak("Say it again", TextToSpeech.QUEUE_FLUSH, null);
                                        }


                                    } else {

                                        getQuestion.setQuestion(text.toLowerCase());
                                        new CheckSinhalaQuestionAsync().execute();

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

                                if (Config.Instance.getLanguageCode().equals("si-LK")) {
                                    sampleObject.setQuestion(jsonObject1.getString("sinhalaQuestion"));
                                } else if (Config.Instance.getLanguageCode().equals("ta-LK")) {
                                    sampleObject.setQuestion(jsonObject1.getString("tamilQuestion"));
                                } else {
                                    sampleObject.setQuestion(jsonObject1.getString("englishQuestion"));
                                }

                                sampleObject.setCount(jsonObject1.getInt("count"));


                                listOfSampleObject.add(sampleObject);
                            }

                        }

                        if (listOfSampleObject.size() == 1) {
                            final Question sinhalaQuestion = gson.fromJson(Utils.sortCardList(listOfSampleObject).get(0).getQuestion(), Question.class);
                            byte[] decoded = Base64.decode(sinhalaQuestion.getAudioString(), 0);
                            playMp3(decoded);
                            tvAnswer.setVisibility(View.VISIBLE);
                            webView.setVisibility(View.GONE);

                            if (checkUrlExistance(sinhalaQuestion.getAnswer())) {
                                tvAnswer.setText(getUrlFromString(sinhalaQuestion.getAnswer()));

                                tvAnswer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        tvAnswer.setVisibility(View.GONE);
                                        webView.setVisibility(View.VISIBLE);
                                        webView.loadUrl(getUrlFromString(sinhalaQuestion.getAnswer()));
                                    }
                                });
                            } else {
                                tvAnswer.setText(sinhalaQuestion.getAnswer());

                            }
                        } else if (listOfSampleObject.size() == 0) {
                            if (Config.Instance.getLanguageCode().equals("si-LK")) {
                                playMp3ByRawFile(R.raw.contact_customer_representative_sinhala);


                            } else if (Config.Instance.getLanguageCode().equals("ta-LK")) {
                                playMp3ByRawFile(R.raw.contact_customer_representative_tamil);

                            } else {
                                playMp3ByRawFile(R.raw.contact_customer_representative_eng);

                            }

//                            ts.speak("කරුණාකර පාරිභෝගික නියෝජිතයා අමතන්න", TextToSpeech.QUEUE_FLUSH, null);

                        } else {

                            if (Config.Instance.getLanguageCode().equals("si-LK")) {
                                playMp3ByRawFile(R.raw.did_you_mean_sinhala);

                            } else if (Config.Instance.getLanguageCode().equals("ta-LK")) {
                                playMp3ByRawFile(R.raw.did_you_mean_tamil);

                            } else {
                                playMp3ByRawFile(R.raw.did_you_mean_eng);

                            }


//                            ts.speak("ඔබ අදහස් කලේ", TextToSpeech.QUEUE_FLUSH, null);

                            listView.setVisibility(View.VISIBLE);
                            tvAnswer.setVisibility(View.GONE);
                            webView.setVisibility(View.GONE);

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

                    startVoiceRecorder();


                }


            });


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
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
                    startVoiceRecorder();
                }
            });
        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }

    private void playMp3ByRawFile(int resorceId) {
        try {
            // create temp file that will hold byte array
            mediaPlayer.reset();

            mediaPlayer = MediaPlayer.create(getApplicationContext(), resorceId);//Create MediaPlayer object with MP3 file under res/raw folder
            mediaPlayer.start();//

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    startVoiceRecorder();
                }
            });
        } catch (Exception ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }

}
