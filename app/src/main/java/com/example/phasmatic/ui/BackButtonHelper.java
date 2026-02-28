package com.example.phasmatic.ui;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;

import com.example.phasmatic.R;

public class BackButtonHelper {

    public static void attach(Activity activity, int buttonId) {
        ImageButton btnBack = activity.findViewById(buttonId);
        if (btnBack == null) return;

        btnBack.setOnClickListener(v -> activity.onBackPressed());
    }
}
