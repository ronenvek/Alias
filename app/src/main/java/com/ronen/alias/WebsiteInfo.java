package com.ronen.alias;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class WebsiteInfo {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface ResultCallback{
        void onCallback(String result);
    }

    public static void getWebsiteInfo(String url, ResultCallback callback) {
        executor.execute(() -> {
            final StringBuilder builder = new StringBuilder();

            try {
                Document doc = Jsoup.connect(url).ignoreContentType(true).get();
                Element body = doc.body();
                builder.append(body.wholeText());

            } catch (Exception e) {
                callback.onCallback("Error: " + e.getMessage());
                return;
            }

            callback.onCallback(builder.toString());

        });
    }


}
