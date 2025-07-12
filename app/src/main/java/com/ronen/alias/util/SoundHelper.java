package com.ronen.alias.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.ronen.alias.R;

public class SoundHelper {
    private static SoundPool soundPool;
    private static int soundIdTick = 0;
    private static int soundIdBeep = 0;
    private static boolean loaded = false;

    public static void initSoundPool(Context context) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(audioAttributes)
                .build();

        soundIdTick = soundPool.load(context, R.raw.ticknew, 1);
        soundIdBeep = soundPool.load(context, R.raw.goodbeep, 1);

        soundPool.setOnLoadCompleteListener((sp, id, status) -> {
            loaded = (status == 0);
        });
    }

    public static void playTick() {
        if (loaded && soundPool != null) {
            soundPool.play(soundIdTick, 1f, 1f, 1, 0, 1f);
        }
    }

    public static void playBeep() {
        if (loaded && soundPool != null) {
            soundPool.play(soundIdBeep, 1f, 1f, 1, 0, 1f);
        }
    }

    public static void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
            loaded = false;
        }
    }
}
