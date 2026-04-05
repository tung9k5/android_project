package com.example.studywithai.API;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.studywithai.BuildConfig; // Đảm bảo bạn đã khai báo BuildConfig
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

    // Lấy API Key từ local.properties (thông qua BuildConfig)
    // Nếu bạn đang gán cứng chuỗi (ví dụ: "AIzaSy..."), hãy thay thế vào đây
    private static final String API_KEY = BuildConfig.GEMINI_API_KEY;

    // 1. TỐI ƯU THỜI GIAN CHỜ (TIMEOUT) LÊN 60s
    // Giúp ứng dụng không bị rớt mạng khi AI đang nghĩ những nội dung dài như Lộ trình
    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    public interface ApiCallback {
        void onSuccess(String result);
        void onError(String error);
    }

    // 2. XÁC ĐỊNH MODEL (Xử lý lỗi 404 và 429)
    private String getModelEndpoint(Context context) {
        SharedPreferences spf = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        int role = spf.getInt("ROLE_USER", 1); // 1: Free, 2: Premium

        // MẸO: Trong quá trình Code và Test, hãy DÙNG BẢN FLASH để được 15 requests/phút (Tránh lỗi 429).
        // Khi nào hoàn thiện dự án đem đi chấm điểm thì mới mở lại logic phân quyền Pro/Flash.
        String modelName = "gemini-2.5-flash";

        // Đoạn code phân quyền thực tế (Tạm đóng):
        // String modelName = (role == 2) ? "gemini-2.5-pro" : "gemini-2.5-flash";

        // Làm sạch Key (Xóa dấu ngoặc kép hoặc khoảng trắng vô tình bị dính)
        String cleanKey = API_KEY.replace("\"", "").replace(" ", "").trim();

        return "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + cleanKey;
    }

    // =========================================================================
    // HÀM 1: SINH NỘI DUNG TỪ 1 CÂU LỆNH (Dùng cho: Lộ trình, Nhiệm vụ ngày...)
    // =========================================================================
    public void generateContent(Context context, String prompt, ApiCallback callback) {
        try {
            // Tạo cấu trúc JSON gửi đi
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
                    callback.onError("Mất kết nối mạng: " + e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    handleResponse(response, callback);
                }
            });
        } catch (Exception e) {
            callback.onError("Lỗi tạo JSON: " + e.getMessage());
        }
    }

    // =========================================================================
    // HÀM 2: CHAT CÓ LƯU TRỮ LỊCH SỬ (Dùng riêng cho Tab Gia sư AI)
    // =========================================================================
    public void chatWithHistory(Context context, List<ChatMessage> history, ApiCallback callback) {
        try {
            JSONObject jsonBody = new JSONObject();
            JSONArray contentsArray = new JSONArray();

            // Duyệt qua toàn bộ lịch sử để đưa vào cấu trúc JSON chuẩn của Gemini
            // Phân biệt rõ "user" (người dùng) và "model" (AI)
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
                    callback.onError("Mất kết nối mạng: " + e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    handleResponse(response, callback);
                }
            });
        } catch (Exception e) {
            callback.onError("Lỗi tạo JSON lịch sử: " + e.getMessage());
        }
    }

    // =========================================================================
    // HÀM DÙNG CHUNG: XỬ LÝ PHẢN HỒI TỪ GOOGLE (Tránh lặp code)
    // =========================================================================
    private void handleResponse(Response response, ApiCallback callback) throws IOException {
        // 1. LỚP PHÒNG THỦ: BẮT LỖI 429 SPAM
        if (response.code() == 429) {
            callback.onError("AI đang quá tải do có nhiều người sử dụng. Bạn vui lòng đợi 1 phút rồi thử lại nhé! ⏳");
            return;
        }

        // Bắt các lỗi HTTP khác (500, 404, 400...)
        if (!response.isSuccessful()) {
            callback.onError("Lỗi từ máy chủ Google (Mã: " + response.code() + ")");
            return;
        }

        String responseData = response.body().string();
        try {
            // 2. Bóc tách JSON lấy câu trả lời
            JSONObject jsonObject = new JSONObject(responseData);
            JSONArray candidates = jsonObject.getJSONArray("candidates");
            JSONObject firstCandidate = candidates.getJSONObject(0);
            JSONObject content = firstCandidate.getJSONObject("content");
            JSONArray parts = content.getJSONArray("parts");
            String textResult = parts.getJSONObject(0).getString("text");

            // 3. Trả kết quả về
            callback.onSuccess(textResult);

        } catch (Exception e) {
            callback.onError("Lỗi đọc dữ liệu AI: " + e.getMessage());
        }
    }
}