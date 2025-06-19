package com.ronen.alias.util;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;
import com.ronen.alias.pages.GuessScreen;
import com.ronen.alias.pages.StartScreen;
import com.ronen.alias.pages.WordScreen;

import java.util.HashMap;
import java.util.Map;


public class DataBase {

    private static FirebaseFirestore firestore;
    private static FirebaseFirestore db;

    private static ListenerRegistration listenerRegistration;

    public static void setup() {
        firestore = FirebaseFirestore.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private static void getDocument(ResultCallback<DocumentSnapshot> result){
        if (firestore == null || db == null)
            setup();

        DocumentReference docRef = db.collection("game").document("game");

        docRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        result.onCallback(documentSnapshot);
                    }
                    else
                        result.onCallback(null);
                })
                .addOnFailureListener(e -> result.onCallback(null));
    }

    private static <T> void getData(String data, ResultCallback<T> result){
        getDocument(ds -> {
            if (ds == null){
                result.onCallback(null);
                return;
            }
            T guesser = (T) ds.get(data);
            result.onCallback(guesser);
        });
    }

    public static void getAmount(ResultCallback<int[]> result){
        getData("correct", (Long correct) ->
                getData("wrong", (Long wrong) ->
                        result.onCallback(new int[]{correct.intValue(), wrong.intValue()})));
    }

    public static void getStart(ResultCallback<Long> result){
        getData("start", result);
    }

    public static void setGuesser(boolean guessing){
        if (guessing)
            putData("game", "guesser", Preferences.getPrefs().getString("id", null));
        else
            putData("game", "guesser", "null");

        putData("game", "correct", 0);
        putData("game", "wrong", 0);


        putData("game", "start", System.currentTimeMillis());

    }

    public static void putData(String document, String key, Object value){
        if (firestore == null || db == null)
            setup();

        Map<String, Object> data = new HashMap<>();
        data.put(key, value);

        db.collection("game").document(document)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Document successfully written!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error writing document", e));
    }



    public static void listenForUpdates() {
        stopListening();
        DocumentReference gameDocRef = db.collection("game").document("game");

        listenerRegistration = gameDocRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                String guesser = documentSnapshot.getString("guesser");


                if (guesser != null && !guesser.equals("null")){
                    String id = Preferences.getPrefs().getString("id", "");
                    if (id.equals(guesser)){
                        Util.switchActivities(WordScreen.class);
                    }
                    else{
                        Util.switchActivities(GuessScreen.class);

                        if (!(Util.context instanceof GuessScreen))
                            return;

                        GuessScreen gs = (GuessScreen) Util.context;
                        int correct = documentSnapshot.getLong("correct").intValue();
                        int wrong = documentSnapshot.getLong("wrong").intValue();
                        gs.updateAmount(correct, wrong);
                    }
                }
                else
                    Util.switchActivities(StartScreen.class);
            }
        });
    }

    public static void stopListening() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }


}
