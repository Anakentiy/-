package com.example.prv.network;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.prv.PRVApp;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SupabaseConnection {
    private static final String TAG = "SupabaseConnection";

    private static final String PROJECT_URL = "https://ylmnmimzoantbdnlrtix.supabase.co";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InlsbW5taW16b2FudGJkbmxydGl4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjMzOTU0MjUsImV4cCI6MjA3ODk3MTQyNX0.wC4S6fR7AdnwGVSq6P98KscF_UHXL_Og8vET254cY94";

    static {
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        System.setProperty("http.maxConnections", "10");
        System.setProperty("http.keepAlive", "true");
    }

    public static HttpURLConnection createConnection(String endpoint, String method) throws IOException {
        URL url = new URL(PROJECT_URL + "/rest/v1/" + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();


        conn.setRequestMethod(method);
        conn.setRequestProperty("apikey", API_KEY);
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Prefer", "return=representation"); // Важно для Supabase

        conn.setConnectTimeout(15000);
        conn.setReadTimeout(20000);
        conn.setUseCaches(false);

        return conn;
    }
    private static String readResponse(HttpURLConnection conn) throws IOException {
        try {
            InputStream inputStream;
            if (conn.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                inputStream = conn.getInputStream();
            } else {
                inputStream = conn.getErrorStream();
            }

            if (inputStream == null) return "";

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка чтения ответа: " + e.getMessage());
            return "";
        }
    }
    public static String getCarsData() {
        HttpURLConnection conn = null;
        try {
            conn = createConnection("cars", "GET");
            int responseCode = conn.getResponseCode();
            Log.d(TAG, "Код ответа: " + responseCode);

            String response = readResponse(conn);

            if (response == null || response.isEmpty()) {
                Log.e(TAG, "Получен пустой ответ от сервера");
                return null;
            }

            Log.d(TAG, "Получен ответ: " + response.substring(0, Math.min(200, response.length())));
            if (responseCode == 200) {
                Log.d(TAG, "Успешный ответ: " + response.substring(0, Math.min(100, response.length())));
                return response;
            } else {
                Log.e(TAG, "Ошибка сервера: " + responseCode);
                Log.e(TAG, "Тело ответа: " + response);
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, " Критическая ошибка: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}