package com.example.studywithai.Activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studywithai.API.GeminiAPIClient;
import com.example.studywithai.Adapters.ChatAdapter;
import com.example.studywithai.Databases.SqliteDbHelper;
import com.example.studywithai.Models.ChatMessage;
import com.example.studywithai.R;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvChatMessages;
    private EditText edtMessage;
    private ImageButton btnSend, btnBack;
    private TextView tvChatTitle;

    private List<ChatMessage> chatList;
    private ChatAdapter chatAdapter;
    private SqliteDbHelper dbHelper;
    private GeminiAPIClient apiClient;

    private int currentSessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 1. Ánh xạ các View từ file XML
        rvChatMessages = findViewById(R.id.rvChatMessages);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        tvChatTitle = findViewById(R.id.tvChatTitle);

        // 2. Nhận ID và Tên cuộc hội thoại từ Intent truyền sang
        currentSessionId = getIntent().getIntExtra("SESSION_ID", -1);
        String title = getIntent().getStringExtra("SESSION_TITLE");

        // Set tên lên thanh Header
        if (tvChatTitle != null) {
            tvChatTitle.setText(title != null ? title : "Gia sư AI");
        }

        // Xử lý nút Back
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // 3. Khởi tạo Database và API
        dbHelper = new SqliteDbHelper(this);
        apiClient = new GeminiAPIClient();

        // 4. Lấy lịch sử chat từ SQLite của đúng phiên này
        chatList = dbHelper.getChatHistory(currentSessionId);

        // Nếu là đoạn chat mới tinh (chưa có tin nhắn), thêm 1 câu chào mặc định
        if (chatList.isEmpty()) {
            String welcomeMsg = "Chào bạn! Mình là Gia sư AI. Bạn muốn hỏi gì về chủ đề '" + title + "' ?";
            chatList.add(new ChatMessage(welcomeMsg, false));
            // Lưu câu chào này vào CSDL luôn
            dbHelper.insertChatMessage(currentSessionId, "ai", welcomeMsg);
        }

        // 5. Cấu hình RecyclerView để hiển thị tin nhắn
        chatAdapter = new ChatAdapter(chatList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // setStackFromEnd(true) giúp danh sách luôn cuộn xuống tin nhắn mới nhất ở dưới cùng
        layoutManager.setStackFromEnd(true);
        rvChatMessages.setLayoutManager(layoutManager);
        rvChatMessages.setAdapter(chatAdapter);

        // Cuộn xuống cuối danh sách nếu có lịch sử cũ
        if (chatList.size() > 0) {
            rvChatMessages.scrollToPosition(chatList.size() - 1);
        }

        // 6. Xử lý sự kiện bấm nút Gửi
        btnSend.setOnClickListener(v -> {
            String messageText = edtMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {

                // 1. KHÓA NÚT GỬI NGAY LẬP TỨC
                btnSend.setEnabled(false);
                btnSend.setAlpha(0.5f); // Làm mờ nút đi để user biết là đang xử lý

                chatList.add(new ChatMessage(messageText, true));
                chatAdapter.notifyItemInserted(chatList.size() - 1);
                rvChatMessages.scrollToPosition(chatList.size() - 1);
                edtMessage.setText("");

                dbHelper.insertChatMessage(currentSessionId, "user", messageText);

                apiClient.chatWithHistory(this, chatList, new GeminiAPIClient.ApiCallback() {
                    @Override
                    public void onSuccess(String result) {
                        runOnUiThread(() -> {
                            String cleanText = result.replace("**", "");
                            chatList.add(new ChatMessage(cleanText, false));
                            chatAdapter.notifyItemInserted(chatList.size() - 1);
                            rvChatMessages.scrollToPosition(chatList.size() - 1);
                            dbHelper.insertChatMessage(currentSessionId, "ai", cleanText);

                            // 2. MỞ KHÓA NÚT GỬI KHI THÀNH CÔNG
                            btnSend.setEnabled(true);
                            btnSend.setAlpha(1.0f);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            // 3. MỞ KHÓA NÚT GỬI NẾU CÓ LỖI (Để user còn thử lại)
                            btnSend.setEnabled(true);
                            btnSend.setAlpha(1.0f);
                            Toast.makeText(ChatActivity.this, "Lỗi kết nối AI: " + error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        });
    }
}