package com.ronen.alias.pages;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ronen.alias.R;
import com.ronen.alias.util.DataBase;
import com.ronen.alias.util.SoundHelper;
import com.ronen.alias.util.Util;


public class GuessScreen extends AppCompatActivity {


    private int timeleft;
    private final android.os.Handler handler = new android.os.Handler();
    private Runnable timerRunnable;

    TextView amount;
    TextView timer;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.guess_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Util.context = this;

        timer = findViewById(R.id.timer);
        amount = findViewById(R.id.amount);
        back = findViewById(R.id.back);
        back.setVisibility(View.GONE);

        back.setOnClickListener(v -> {
            DataBase.setGuesser(false);
            Util.switchActivities(StartScreen.class);
            stop();
        });

        timeleft = 60 * 1000;

        DataBase.getStart(v -> {
            timeleft = 60 * 1000 - (int)(System.currentTimeMillis() - v);
            if (timeleft <= 5 * 1000)
                back.setVisibility(View.VISIBLE);

        });

        final int timerSpeed = 100;

        toStop = false;

        Context ctx = this;

        timerRunnable = new Runnable() {
            @Override
            public void run() {

                if (toStop || !Util.context.equals(ctx)){
                    handler.removeCallbacks(timerRunnable);
                    return;
                }

                timeleft -= timerSpeed;

                if (timeleft <= -10 * 1000)
                    back.setVisibility(View.VISIBLE);

                if (timeleft <= 0){
                    SoundHelper.playBeep();
                    timer.setTextColor(Color.rgb(255, 0, 0));
                    timer.setText("Out of time!");
                    Util.vibrate(1000);
                    handler.removeCallbacks(timerRunnable);
                    return;
                }

                if (timeleft <= 8000){
                    if (timeleft % 1000 < timerSpeed) {
                        SoundHelper.playTick();
                    }
                }

                int seconds = timeleft / 1000;
                if (seconds > 30)
                    timer.setTextColor(Color.GREEN);
                else if (seconds > 20)
                    timer.setTextColor(Color.rgb(200, 200, 0));
                else if (seconds > 10)
                    timer.setTextColor(Color.rgb(255, 128, 0));
                else
                    timer.setTextColor(Color.rgb(255, 0, 0));
                timer.setText(String.valueOf(seconds));

                handler.postDelayed(this, timerSpeed);
            }
        };

        updateAmount(0, 0);
        DataBase.getAmount(r -> updateAmount(r[0], r[1]));

        handler.post(timerRunnable);
    }



    public void updateAmount(int correct, int wrong){
        if (timeleft <= 0)
            return;

        String text = "+ " + correct + " | - " + wrong;
        SpannableString styledText = new SpannableString(text);

        int mid = text.length() / 2;
        styledText.setSpan(
                new android.text.style.ForegroundColorSpan(android.graphics.Color.GREEN),
                0,
                mid,
                android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        styledText.setSpan(
                new android.text.style.ForegroundColorSpan(android.graphics.Color.BLACK),
                mid,
                mid+1,
                android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        styledText.setSpan(
                new android.text.style.ForegroundColorSpan(android.graphics.Color.RED),
                mid+1,
                text.length(),
                android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        runOnUiThread(() -> amount.setText(styledText));
    }

    private boolean toStop = false;
    void stop(){
        toStop = true;
        handler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }
}
