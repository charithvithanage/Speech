package com.google.cloud.android.speech;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
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

    String status="startStatus";


    // Resource caches
    private int mColorHearing;
    private int mColorNotHearing;

    // View references
//    private TextView mStatus;

    ImageView imageView;
//    ImageView hearingImageView;

//    TextView tvWait;

    Boolean startQuestion = true;

    SinhalaQuestion sinhalaQuestion = new SinhalaQuestion();

     private android.app.ProgressDialog dialog;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        Log.d(TAG,"onCreated()");

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

                Log.d(TAG,"OnStart()");

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
//        tvWait = findViewById(R.id.tvWait);
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

        btnEnglish.setVisibility(View.GONE);
        btnSinhala.setVisibility(View.GONE);
        btnTamil.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);

//        tvWait.setVisibility(View.GONE);

//        final Resources resources = getResources();
//        final Resources.Theme theme = getTheme();
//        mColorHearing = ResourcesCompat.getColor(resources, R.color.status_hearing, theme);
//        mColorNotHearing = ResourcesCompat.getColor(resources, R.color.status_not_hearing, theme);

//        mStatus = findViewById(R.id.status);
    }

    private void startVoiceRecorder() {

                Log.d(TAG,"startVoiceRecord()");

        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
        }
        mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
        mVoiceRecorder.start();

                Log.d(TAG,"service started()");

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


//                tvWait.setText(hearingVoice ? "Listening" : "Say something....");
//                tvWait.setTextColor(hearingVoice ? mColorHearing : mColorNotHearing);
            }
        });
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
                            Log.d(TAG,"onServiceConnected()");

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
                                        Log.d(TAG,"onServiceDisconnected()");

            mSpeechService = null;
        }

    };

    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

        @Override
        public void onVoiceStart() {
                    Log.d(TAG,"mVoice callback onVoiceStart()");

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
                                Log.d(TAG,"mVoice callback onVoiceEnd()");

            showStatus(false);
            if (mSpeechService != null) {
                mSpeechService.finishRecognizing();
            }
        }

    };


    private void selectSinhala() {
        Config.Instance.setLanguageCode("si-LK");
        startQuestion = false;
                                                ts.speak("please continue in sinhala", TextToSpeech.QUEUE_FLUSH, null);

//        selectNavigationOption();
//        Utils.startNewActivity(FirstPage.this, BankActivity.class);

    }

    private void selectEnglish() {
        Config.Instance.setLanguageCode("en-US");
        startQuestion = false;
                                                ts.speak("please continue in english", TextToSpeech.QUEUE_FLUSH, null);


//        Utils.startNewActivity(FirstPage.this, BankActivity.class);

//        selectNavigationOption();
    }

    private void selectTamil() {
        Config.Instance.setLanguageCode("ta-LK");
        startQuestion = false;
                                                ts.speak("please continue in tamil", TextToSpeech.QUEUE_FLUSH, null);

//        Utils.startNewActivity(FirstPage.this, BankActivity.class);
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


                                    if(status.equals("startStatus")){
                                    if (text.toLowerCase().equals("hello sam")) {
                                                                                imageView.setVisibility(View.VISIBLE);

                                        status="selectLanguageStatus";

                                        startQuestion = false;
                                        ts.speak("Hello I'm Sam, Please select your language", TextToSpeech.QUEUE_FLUSH, null);
                                        btnEnglish.setVisibility(View.VISIBLE);
                                        btnSinhala.setVisibility(View.VISIBLE);
                                        btnTamil.setVisibility(View.VISIBLE);
                                        imageView.setVisibility(View.VISIBLE);

                                    } else{
                                                                                    ts.speak("Say hello sam", TextToSpeech.QUEUE_FLUSH, null);

                                    }
                                    }else if(status.equals("selectLanguageStatus")){
                                           if (text.toLowerCase().equals("english")) {
                                        selectEnglish();

                                    } else if (text.toLowerCase().equals("tamil")) {
                                        selectTamil();
                                    } else if (text.toLowerCase().equals("sing hello")) {
                                        selectSinhala();
                                    } else if (text.toLowerCase().equals("sinhala")) {
                                        selectSinhala();
                                    }else{
                                                                                                                                   ts.speak("Say hello sam", TextToSpeech.QUEUE_FLUSH, null);
                                                                                    ts.speak("Say it again", TextToSpeech.QUEUE_FLUSH, null);

                                    }

                                    }else{
                                     sinhalaQuestion.setId(null);
                                            sinhalaQuestion.setQuestion(text);
                                            sinhalaQuestion.setAnswer(null);
                                            sinhalaQuestion.setKeywords(null);
                                            sinhalaQuestion.setSource(null);
                                            new AddQuestionAsync().execute();
                                    }


                                    Handler handler = new Handler();

                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
//                                            tvWait.setText("Say something.....");
                                            startVoiceRecorder();
                                        }
                                    }, 3000);


                                }
                            }
                        });
                    }
                }
            };

    private class AddQuestionAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            ApiService policyService = ApiService.getInstance();

            policyService.addSinhalaQuestion(FirstPage.this, sinhalaQuestion, new VolleyCallback() {
                @Override
                public void onSuccessResponse(String result) {
                    JSONObject jsonObject;
                    String status;
                    try {
                        jsonObject = new JSONObject(result);

                        Log.d(TAG, jsonObject.toString());
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

}
