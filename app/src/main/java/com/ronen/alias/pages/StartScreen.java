package com.ronen.alias.pages;

import static com.ronen.alias.pages.WordScreen.words;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ronen.alias.R;
import com.ronen.alias.util.DataBase;
import com.ronen.alias.util.Preferences;
import com.ronen.alias.util.Util;
import com.ronen.alias.util.WebsiteInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;


public class StartScreen extends AppCompatActivity {

    Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.start_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Util.context = this;
        Util.setupSounds();

        start = findViewById(R.id.start);

        start.setOnClickListener(null);

        String id = Preferences.getPrefs().getString("id", null);
        if (id == null) {
            id = UUID.randomUUID().toString();
            Preferences.getEditor().putString("id", id);
            Preferences.save();
        }

        DataBase.setup();

        if (words == null || words.isEmpty())
            updateList();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            DataBase.listenForUpdates();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                start.setOnClickListener(v -> {
                    if (words == null || words.isEmpty())
                        return;
                    DataBase.setGuesser(true);
                    Util.switchActivities(WordScreen.class);
                });
            }, 500);


        }, 1000);
    }

    private final String url = "https://raw.githubusercontent.com/ronenvek/Alias/refs/heads/main/words";
    public void updateList(){
        WebsiteInfo.getWebsiteInfo(url, result -> {
            if (result.startsWith("Error:")){
                Toast.makeText(this, result, Toast.LENGTH_SHORT);
                return;
            }
            words = new ArrayList<>(Arrays.asList(result.split("\n")));
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }
}
