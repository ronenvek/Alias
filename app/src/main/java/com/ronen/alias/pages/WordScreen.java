package com.ronen.alias.pages;

import static com.ronen.alias.util.Util.beep;
import static com.ronen.alias.util.Util.tick;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ronen.alias.util.DataBase;
import com.ronen.alias.util.ListAdapter;
import com.ronen.alias.R;
import com.ronen.alias.util.Util;

import java.util.ArrayList;
import java.util.List;

public class WordScreen extends AppCompatActivity {

	TextView word;
    TextView amount;
    TextView timer;
    ImageButton back;

    int correct;
    int wrong;

    private int timeleft;
    private final android.os.Handler handler = new android.os.Handler();
    private Runnable timerRunnable;


    RecyclerView recyclerView;
    ListAdapter adapter;
    List<String> data = new ArrayList<>();

    WordScreen context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.word_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Util.context = this;

        correct = 0;
        wrong = 0;

	    word = findViewById(R.id.text);
        timer = findViewById(R.id.timer);
        amount = findViewById(R.id.amount);
        back = findViewById(R.id.back);

        word.setTextColor(Util.resolveAttrColor());


        back.setOnClickListener(v -> {
            DataBase.setGuesser(false);
            Util.switchActivities(StartScreen.class);
        });
        back.setVisibility(View.GONE);

        findViewById(R.id.next).setOnClickListener(v -> guess(true));
        findViewById(R.id.skip).setOnClickListener(v -> guess(false));

        timeleft = 60 * 1000;

        DataBase.getStart(v -> {
            timeleft = 60 * 1000 - (int)(System.currentTimeMillis() - v);
        });

        final int timerSpeed = 100;

         timerRunnable = new Runnable() {
            @Override
            public void run() {
                timeleft -= timerSpeed;

                if (timeleft <= 0){
                    tick.stop();
                    beep.start();
                    timer.setTextColor(Color.rgb(255, 0, 0));
                    timer.setText("Out of time!");
                    back.setVisibility(View.VISIBLE);
                    Util.vibrate(1000);
                    handler.removeCallbacks(timerRunnable);
                    return;
                }

                if (timeleft <= 8000 && !tick.isPlaying())
                    tick.start();

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

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ListAdapter(data, true, v -> DataBase.putData("badwords", v, true));
        recyclerView.setAdapter(adapter);

        handler.post(timerRunnable);

        showWord();
        updateAmount();

        DataBase.getAmount(r -> {
            correct = r[0];
            wrong = r[1];
            updateAmount();
        });
    }

    public static List<String> words;

    public void updateAmount(){
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

    private void guess(boolean right){
        if (timeleft <= 0)
            return;

        if (right)
            DataBase.putData("game", "correct", ++correct);
        else
            DataBase.putData("game", "wrong", ++wrong);

        updateAmount();

        showWord();
    }

    private void showWord(){
        if (words == null || words.isEmpty()){
            DataBase.setGuesser(false);
            Util.switchActivities(StartScreen.class);
            return;
        }
        int loc = (int)(Math.random()*words.size());
        runOnUiThread(() -> {
            String newWord = words.remove(loc);
            word.setText(newWord);
            data.add(newWord);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(timerRunnable);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }


}
