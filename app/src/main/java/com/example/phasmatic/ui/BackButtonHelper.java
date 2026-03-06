package com.example.phasmatic.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

import com.example.phasmatic.R;

public class BackButtonHelper {

    public static void attach(Activity activity, int buttonId) {
        ImageButton btnBack = activity.findViewById(buttonId);
        if (btnBack == null) return;

        btnBack.setOnClickListener(v -> activity.onBackPressed());
    }

    public static void attachToGoLogin(Activity activity, int buttonId) {
        ImageButton btnBack = activity.findViewById(buttonId);
        if (btnBack == null) return;

        btnBack.setOnClickListener(v -> {
            Intent i = new Intent(activity, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(i);
            activity.finish();
        });
    }

    public static void attachToGoUserInfo(Activity activity,
                                          int buttonId,
                                          String userId,
                                          String fullName,
                                          String email,
                                          String phone) {
        ImageButton btnBack = activity.findViewById(buttonId);
        if (btnBack == null) return;

        btnBack.setOnClickListener(v -> {
            Intent i = new Intent(activity, UserInfoActivity.class);
            i.putExtra("userId", userId);
            i.putExtra("userFullName", fullName);
            i.putExtra("userEmail", email);
            i.putExtra("userPhone", phone);
            activity.startActivity(i);
            activity.finish();
        });
    }

    public static void attachToGoModeSelection(Activity activity,
                                               int buttonId,
                                               String userId,
                                               String fullName,
                                               String email,
                                               String phone) {
        ImageButton btnBack = activity.findViewById(buttonId);
        if (btnBack == null) return;

        btnBack.setOnClickListener(v -> {
            Intent i = new Intent(activity, ModeSelectionActivity.class);
            i.putExtra("userId", userId);
            i.putExtra("userFullName", fullName);
            i.putExtra("userEmail", email);
            i.putExtra("userPhone", phone);
            activity.startActivity(i);
            activity.finish();
        });
    }
//    public static void attachToGoForum(Activity activity,
//                                               int buttonId,
//                                               String userId,
//                                               String fullName,
//                                               String email,
//                                               String phone) {
//        ImageButton btnBack = activity.findViewById(buttonId);
//        if (btnBack == null) return;
//
//        btnBack.setOnClickListener(v -> {
//            Intent i = new Intent(activity, ModeSelectionActivity.class);
//            i.putExtra("userId", userId);
//            i.putExtra("userFullName", fullName);
//            i.putExtra("userEmail", email);
//            i.putExtra("userPhone", phone);
//            activity.startActivity(i);
//            activity.finish();
//        });
//    }
}
