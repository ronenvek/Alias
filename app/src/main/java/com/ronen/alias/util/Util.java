package com.ronen.alias.util;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;

import com.ronen.alias.R;

public class Util {

    public static Context context;

    public static void switchActivities(Object page) {
        if (page.toString().equals(context.getClass().toString()))
            return;
        Intent switchActivityIntent = new Intent(context, (Class<?>) page);
        context.startActivity(switchActivityIntent);
    }

    public static int resolveAttrColor() {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true);
        return typedValue.data;
    }

    public static void vibrate(int duration) {
        Vibrator vibrator = context.getSystemService(Vibrator.class);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(duration);
            }
        }
    }
}
