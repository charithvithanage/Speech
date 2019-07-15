package com.google.cloud.android.speech;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Utils {

    private static final String TAG ="GoogleVoiceToText" ;

    public static void startNewActivity(Context context, Class activity) {
        Intent intent = new Intent(context, activity);
        context.startActivity(intent);
    }

    //Sort card list
    public static SampleObject sortCardList(List<SampleObject> lsit, Gson gson, String responseTag) {

        Collections.sort(lsit, new Comparator<SampleObject>() {
            public int compare(SampleObject obj1, SampleObject obj2) {
                // ## Ascending order
                return Integer.valueOf((int) obj2.getPoints()).compareTo((int) obj1.getPoints()); // To compare string values
                // return Integer.valueOf(obj1.empId).compareTo(obj2.empId); // To compare integer values

                // ## Descending order
                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                // return Integer.valueOf(obj2.empId).compareTo(obj1.empId); // To compare integer values
            }
        });

        if (responseTag != null) {

            List<SampleObject> temp = new ArrayList<>();


            for (SampleObject sampleObject: lsit)
            {
                if(lsit.get(0).getPoints()==sampleObject.getPoints()){
                    temp.add(sampleObject);

                    Log.d(TAG,sampleObject.getQuestion());
                    Log.d(TAG, String.valueOf(sampleObject.getCount()));
                }
            }

            if (temp != null || temp.size() != 0) {
                for (SampleObject sampleObject1 : temp) {

                    Question question = gson.fromJson(sampleObject1.getQuestion(), Question.class);

                    if (question.getTag().equals(responseTag)) {
                        return sampleObject1;
                    }
                }
            }
        }

        return lsit.get(0);
    }

    public static boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    public static void displayEmailDialog(Context context, final ConfirmEmailListener listener) {
        final DialogPlus dialog = DialogPlus.newDialog(context)
                .setPadding(50, 50, 50, 50)
                .setMargin(100, 50, 100, 50)
                .setContentBackgroundResource(R.drawable.callback_dialog_bg)
                .setContentHolder(new ViewHolder(R.layout.email_dialog_layout))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setCancelable(false)
                .setExpanded(true, ViewGroup.LayoutParams.WRAP_CONTENT).setGravity(Gravity.CENTER)  // This will enable the expand feature, (similar to android L share dialog)
                .create();

        View viewDialog = dialog.getHolderView();

        final EditText etEmail = viewDialog.findViewById(R.id.etEmail);

        Button dialogButtonOk = viewDialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(etEmail.getText().toString())) {
                    listener.confirmClick(dialog, etEmail.getText().toString());
                }
            }
        });

        Button dialogButtonCancel = viewDialog.findViewById(R.id.dialogButtonCancel);
        // if button is clicked, close the custom dialog
        dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.cancelClick(dialog);
            }
        });

        dialog.show();
    }

    public static void displayCallbackDialog(Context context, final ConfirmCallbackDialogListener listener) {
        final DialogPlus dialog = DialogPlus.newDialog(context)
                .setPadding(50, 50, 50, 50)
                .setMargin(50, 50, 50, 50)
                .setContentBackgroundResource(R.drawable.callback_dialog_bg)
                .setContentHolder(new ViewHolder(R.layout.callback_dialog_layout))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setCancelable(false)
                .setExpanded(true, ViewGroup.LayoutParams.WRAP_CONTENT).setGravity(Gravity.CENTER)  // This will enable the expand feature, (similar to android L share dialog)
                .create();

        View viewDialog = dialog.getHolderView();

        final EditText etName = viewDialog.findViewById(R.id.etName);
        final EditText etPhone = viewDialog.findViewById(R.id.etPhone);

        Button dialogButtonOk = viewDialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(etName.getText().toString())&&!TextUtils.isEmpty(etPhone.getText().toString())) {
                    listener.confirmClick(dialog, etName.getText().toString(),etPhone.getText().toString());
                }
            }
        });

        Button dialogButtonCancel = viewDialog.findViewById(R.id.dialogButtonCancel);
        // if button is clicked, close the custom dialog
        dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.cancelClick(dialog);
            }
        });

        dialog.show();
    }

}
