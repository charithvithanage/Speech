package com.google.cloud.android.speech;

import com.orhanobut.dialogplus.DialogPlus;

public interface ConfirmEmailListener {
    void confirmClick(DialogPlus dialog, String email);

    void cancelClick(DialogPlus dialog);
}
