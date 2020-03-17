package com.example.jhw.newexapplication;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CustomProgressDialog extends Dialog { // 로딩 UI 레이아웃 custom_progress_dialog를 불러옴
    public CustomProgressDialog(Context context) {
        super(context);
        this.setContentView(R.layout.custom_progress_dialog);
    }
}
