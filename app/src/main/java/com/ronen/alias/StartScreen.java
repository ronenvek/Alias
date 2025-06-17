package com.ronen.alias;

import static com.ronen.alias.WordScreen.words;

import android.os.Bundle;

import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;


public class StartScreen extends AppCompatActivity {

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

        findViewById(R.id.start).setOnClickListener(v -> Util.switchActivities(WordScreen.class, this));

        if (words == null || words.isEmpty())
            updateList();
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
}
