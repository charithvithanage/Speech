package com.google.cloud.android.speech;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class BankActivity extends AppCompatActivity implements MessageDialogFragment.Listener {

    private static final String FRAGMENT_MESSAGE_DIALOG = "message_dialog";

    private static final String STATE_RESULTS = "results";

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 2;

    private SpeechService mSpeechService;

    private VoiceRecorder mVoiceRecorder;

    private static final String TAG = "GoogleVoiceToText";

    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

        @Override
        public void onVoiceStart() {
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
            showStatus(false);
            if (mSpeechService != null) {
                mSpeechService.finishRecognizing();
            }
        }

    };

    // Resource caches
    private int mColorHearing;
    private int mColorNotHearing;

    // View references
    private TextView mStatus;
    private TextView mText;
    private ResultAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ImageView btnMic;

    //    Map<String, String> map = new HashMap<>();
    Map<String, String> map = new HashMap<>();

    private final ArrayList<String> mResults = new ArrayList<>();


    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mSpeechService = SpeechService.from(binder);
            mSpeechService.addListener(mSpeechServiceListener);
            mStatus.setVisibility(View.VISIBLE);


        }


        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mSpeechService = null;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_bank);

        createMap();

        btnMic = findViewById(R.id.btnMic);


        final Resources resources = getResources();
        final Resources.Theme theme = getTheme();
        mColorHearing = ResourcesCompat.getColor(resources, R.color.status_hearing, theme);
        mColorNotHearing = ResourcesCompat.getColor(resources, R.color.status_not_hearing, theme);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        mStatus = findViewById(R.id.status);
        mText = findViewById(R.id.textSearch);
//
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final ArrayList<String> results = savedInstanceState == null ? null :
                savedInstanceState.getStringArrayList(STATE_RESULTS);
        mAdapter = new ResultAdapter(results);
        mRecyclerView.setAdapter(mAdapter);

        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (btnMic.isSelected()) {
                    mResults.clear();
                    mAdapter.notifyDataSetChanged();
                    btnMic.setSelected(false);
                    btnMic.setImageResource(R.mipmap.microphone_icon);
                    mSpeechService.removeListener(mSpeechServiceListener);
                    unbindService(mServiceConnection);
                } else {
                    btnMic.setSelected(true);
                    btnMic.setImageResource(R.mipmap.stop_icon);
                    bindService(new Intent(BankActivity.this, SpeechService.class), mServiceConnection, BIND_AUTO_CREATE);

                }

            }
        });


    }

    private void createMap() {

        if (Config.Instance.getLanguageCode().equals("en-US")) {

            InputStream is = getResources().openRawResource(R.raw.bank_datas);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            String line = "";
            try {

                reader.readLine();
                while ((line = reader.readLine()) != null) {

                    String[] tokens = line.split(",");

                    map.put(tokens[0].toLowerCase().replaceAll("[^a-zA-Z0-9]", ""), tokens[1]);
                }
            } catch (IOException e) {
                Log.wtf("MyActivity", "Error reading data file on line " + line, e);
                e.printStackTrace();
            }
        } else if (Config.Instance.getLanguageCode().equals("si-LK")) {
            map.put("customercarecenterඑකේනම්බර්එකදෙන්න", "Tel - +94 11 2 30 30 50 \\n\\nFax - +94 11 4712 013");
            map.put("කස්ටමර්කෙයර්සෙන්ටර්එකේනම්බර්එකදෙන්න", "Tel - +94 11 2 30 30 50 \\n\\nFax - +94 11 4712 013");
            map.put("customerservicedepartmentඑකේනම්බර්එකදැනගන්නපුලුවන්ද", "Tel - +94 11 2 30 30 50 \\n\\nFax - +94 11 4712 013");
            map.put("කස්ටමර්සර්විස්ඩිපාර්ට්මන්ට්එකේනම්බර්එකදැනගන්නපුලුවන්ද", "Tel - +94 11 2 30 30 50 \\n\\nFax - +94 11 4712 013");
            map.put("මගේsavingsපාස්පොතනැතිවෙලා", "ඔබට ගිණුම අවලංගු කර අලුත් පාස් පොතක් ලබාගැනීමට හෝ පාස් පොතේ පිටපතක් ලබාගත හැක.");
            map.put("magesavingsපාස්පොතනැතිවෙලා", "ඔබට ගිණුම අවලංගු කර අලුත් පාස් පොතක් ලබාගැනීමට හෝ පාස් පොතේ පිටපතක් ලබාගත හැක.");
            map.put("මගේඉතිරිකිරීමේපාස්පොතනැතිවෙලා", "ඔබට ගිණුම අවලංගු කර අලුත් පාස් පොතක් ලබාගැනීමට හෝ පාස් පොතේ පිටපතක් ලබාගත හැක.");
            map.put("ඔබගේගිණුමෙන්මුදල්ලබාගැනීමටපුළුවන්කවදාද", "ඔව්. වයස 18 සම්පුර්ණ වූ විට, ඔබට/ඇයට මුදල් ලබා ගත හැක.");
            map.put("ලමයින්ගේගිණුම්", "කරුණාකර ලගම සම්පත් බැංකු ශාකාවට ගිහින් හෝ මෙම ලින්ක් එකට ඇතුළුවන්න link\\nhttps://www.sampath.lk/en/personal/savings/childrens-savings");
            map.put("ළමයින්ගේගිණුම්", "කරුණාකර ලගම සම්පත් බැංකු ශාකාවට ගිහින් හෝ මෙම ලින්ක් එකට ඇතුළුවන්න link\\nhttps://www.sampath.lk/en/personal/savings/childrens-savings");
            map.put("ළමයින්ගෙගිණුම්", "කරුණාකර ලගම සම්පත් බැංකු ශාකාවට ගිහින් හෝ මෙම ලින්ක් එකට ඇතුළුවන්න link\\nhttps://www.sampath.lk/en/personal/savings/childrens-savings");
            map.put("ලමයින්ගෙගිණුම්", "කරුණාකර ලගම සම්පත් බැංකු ශාකාවට ගිහින් හෝ මෙම ලින්ක් එකට ඇතුළුවන්න link\\nhttps://www.sampath.lk/en/personal/savings/childrens-savings");
            map.put("ජේෂ්ඨපුරවැසිගිණුම", "මෙම ලින්ක් එකට ඇතුළුවන්න link:(https://www.sampath.lk/en/personal/savings?lvl3=senior-savings) ජේස්ට පුරවැසි ගිණුම සම්බන්ද විස්තර දැනගැනීමට");
            map.put("ජේශ්ටපුරවැසිගිනුම", "මෙම ලින්ක් එකට ඇතුළුවන්න link:(https://www.sampath.lk/en/personal/savings?lvl3=senior-savings) ජේස්ට පුරවැසි ගිණුම සම්බන්ද විස්තර දැනගැනීමට");
            map.put("ළමයින්ටඅලුත්ගිණුමක්", "මෙම ලින්ක් එකට ඇතුළුවන්න link:(https://www.sampath.lk/en/personal/savings/childrens-savings)");
            map.put("රැකියාඅවස්ථා", "මෙම ලින්ක් එකට ඇතුළුවන්න link:(https://www.sampath.lk/en/careers/vacancy-list)");
            map.put("බැංකුවවිවෘතකරනවේලාව", "මෙම ලින්ක් එකට ඇතුළුවන්න link:(Please visit this link:(https://www.sampath.lk/en/personal/savings?lvl3=senior-savings)");

        }


    }


    @Override
    protected void onStart() {
        super.onStart();

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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null) {
            outState.putStringArrayList(STATE_RESULTS, mAdapter.getResults());
        }
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

    private void startVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
        }
        mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
        mVoiceRecorder.start();
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
                mStatus.setTextColor(hearingVoice ? mColorHearing : mColorNotHearing);
            }
        });
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
                        mVoiceRecorder.dismiss();
                    }
                    if (mText != null && !TextUtils.isEmpty(text)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isFinal) {
                                    mText.setText(text);

//                                    String value = map.get(text.toLowerCase().replaceAll("[^a-zA-Z0-9]", ""));
                                    String replacedCommas = text.toLowerCase().replaceAll(" ", "");
                                    String value = map.get(replacedCommas);

                                    Log.d(TAG, text);
//                                    mRecyclerView.smoothScrollToPosition(0);

                                    if (btnMic.isSelected()) {
                                        btnMic.setSelected(false);
                                        btnMic.setImageResource(R.mipmap.microphone_icon);
                                        mSpeechService.removeListener(mSpeechServiceListener);
                                        unbindService(mServiceConnection);
                                    }

                                    if (value == null) {
                                        Toast.makeText(getApplicationContext(), "No record found.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        mResults.clear();
                                        mResults.add(value);
                                        mAdapter.notifyDataSetChanged();

                                    }
                                } else {
                                    mText.setText(text);
                                }
                            }
                        });
                    }
                }
            };

    private static class ViewHolder extends RecyclerView.ViewHolder {

        TextView text;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_result, parent, false));
            text = itemView.findViewById(R.id.text);
        }

    }

    private class ResultAdapter extends RecyclerView.Adapter<ViewHolder> {


        ResultAdapter(ArrayList<String> results) {
            if (results != null) {
                mResults.addAll(results);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(mResults.get(position));
        }

        @Override
        public int getItemCount() {
            return mResults.size();
        }

        void addResult(String result) {
            mResults.clear();
            mResults.add(result);
            notifyItemInserted(0);
        }

        public ArrayList<String> getResults() {
            return mResults;
        }

    }

}

