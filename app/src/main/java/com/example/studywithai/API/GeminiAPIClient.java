package com.example.studywithai.API;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.studywithai.BuildConfig;
import com.example.studywithai.Models.ChatMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiAPIClient {

    private static final String API_KEY = BuildConfig.GEMINI_API_KEY;

    // --- Optimize Timeout Settings
    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    public interface ApiCallback {
        void onSuccess(String result);
        void onError(String error);
    }

    // --- Get Model Endpoint
    private String getModelEndpoint(Context context) {
        SharedPreferences spf = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        int role = spf.getInt("ROLE_USER", 1);

        String modelName = "gemini-2.5-flash";
        String cleanKey = API_KEY.replace("\"", "").replace(" ", "").trim();

        return "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + cleanKey;
    }

    // --- Generate Content (Single Prompt)
    public void generateContent(Context context, String prompt, ApiCallback callback) {
        try {
            JSONObject jsonBody = new JSONObject();
            JSONArray contentsArray = new JSONArray();
            JSONObject contentsObject = new JSONObject();
            JSONArray partsArray = new JSONArray();
            JSONObject partsObject = new JSONObject();

            partsObject.put("text", prompt);
            partsArray.put(partsObject);
            contentsObject.put("parts", partsArray);
            contentsArray.put(contentsObject);
            jsonBody.put("contents", contentsArray);

            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(getModelEndpoint(context))
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onError("Network disconnected: " + e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    handleResponse(response, callback);
                }
            });
        } catch (Exception e) {
            callback.onError("JSON creation error: " + e.getMessage());
        }
    }

    // --- Chat With History
    public void chatWithHistory(Context context, List<ChatMessage> history, ApiCallback callback) {
        try {
            JSONObject jsonBody = new JSONObject();
            JSONArray contentsArray = new JSONArray();

            for (ChatMessage msg : history) {
                JSONObject contentsObject = new JSONObject();
                contentsObject.put("role", msg.isUser() ? "user" : "model");

                JSONArray partsArray = new JSONArray();
                JSONObject partsObject = new JSONObject();
                partsObject.put("text", msg.getContent());

                partsArray.put(partsObject);
                contentsObject.put("parts", partsArray);

                contentsArray.put(contentsObject);
            }

            jsonBody.put("contents", contentsArray);

            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(getModelEndpoint(context))
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onError("Network disconnected: " + e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    handleResponse(response, callback);
                }
            });
        } catch (Exception e) {
            callback.onError("JSON history error: " + e.getMessage());
        }
    }

    // --- Handle AI Response Common Logic
    private void handleResponse(Response response, ApiCallback callback) throws IOException {
        if (response.code() == 429) {
            callback.onError("AI is currently overloaded. Please wait 1 minute and try again! ⏳");
            return;
        }

        if (!response.isSuccessful()) {
            callback.onError("Google Server Error (Code: " + response.code() + ")");
            return;
        }

        String responseData = response.body().string();
        try {
            JSONObject jsonObject = new JSONObject(responseData);
            JSONArray candidates = jsonObject.getJSONArray("candidates");
            JSONObject firstCandidate = candidates.getJSONObject(0);
            JSONObject content = firstCandidate.getJSONObject("content");
            JSONArray parts = content.getJSONArray("parts");
            String textResult = parts.getJSONObject(0).getString("text");

            callback.onSuccess(textResult);

        } catch (Exception e) {
            callback.onError("AI Data parsing error: " + e.getMessage());
        }
    }
}