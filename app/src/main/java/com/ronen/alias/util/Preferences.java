package com.ronen.alias.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;


public class Preferences {

    static SharedPreferences sharedPreferences = Util.context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
    static SharedPreferences.Editor editor;

    public static SharedPreferences getPrefs(){
        return sharedPreferences;
    }

    public static SharedPreferences.Editor getEditor(){
        if (editor != null)
            save();
        editor = sharedPreferences.edit();
        return editor;
    }

    public static void save(){
        editor.apply();
    }

}
