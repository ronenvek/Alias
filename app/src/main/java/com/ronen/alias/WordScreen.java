package com.ronen.alias;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordScreen extends AppCompatActivity {

	TextView word;
    TextView amount;
    TextView timer;
    Button back;

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

	    word = findViewById(R.id.text);
        timer = findViewById(R.id.timer);
        amount = findViewById(R.id.amount);
        back = findViewById(R.id.back);

        back.setOnClickListener(v -> Util.switchActivities(StartScreen.class, this));
        back.setVisibility(View.GONE);

        correct = -1;
        wrong = 0;

        context = this;

        findViewById(R.id.next).setOnClickListener(v -> guess(true));
        findViewById(R.id.skip).setOnClickListener(v -> guess(false));

        timeleft = 60 * 1000;
        final int timerSpeed = 1000;

         timerRunnable = new Runnable() {
            @Override
            public void run() {
                timeleft -= timerSpeed;

                if (timeleft <= 0){
                    timer.setText("Out of time!");
                    back.setVisibility(View.VISIBLE);
                    Util.vibrate(500, context);
                    handler.removeCallbacks(timerRunnable);
                    return;
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

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ListAdapter(data, false, v -> {});
        recyclerView.setAdapter(adapter);

        guess(true);

        handler.post(timerRunnable);
    }

    public static List<String> words;

    private void guess(boolean right){
        if (timeleft <= 0)
            return;

        if (right)
            correct++;
        else
            wrong++;

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

        showWord();
    }

    private void showWord(){
        if (words == null || words.isEmpty()){
            Util.switchActivities(StartScreen.class, this);
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

}
