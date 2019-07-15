package com.google.cloud.android.speech;

import com.orhanobut.dialogplus.DialogPlus;

public interface ConfirmCallbackDialogListener {

    void confirmClick(DialogPlus dialog, String name, String phone);

    void cancelClick(DialogPlus dialog);

}
