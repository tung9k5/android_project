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

        // --- Map XML Views
        rvChatMessages = findViewById(R.id.rvChatMessages);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        tvChatTitle = findViewById(R.id.tvChatTitle);

        // --- Get Intent Data
        currentSessionId = getIntent().getIntExtra("SESSION_ID", -1);
        String title = getIntent().getStringExtra("SESSION_TITLE");

        if (tvChatTitle != null) {
            tvChatTitle.setText(title != null ? title : "AI Tutor");
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // --- Initialize Database and API
        dbHelper = new SqliteDbHelper(this);
        apiClient = new GeminiAPIClient();

        // --- Load Chat History
        chatList = dbHelper.getChatHistory(currentSessionId);

        if (chatList.isEmpty()) {
            String welcomeMsg = "Hello! I am your AI Tutor. What would you like to ask about '" + title + "'?";
            chatList.add(new ChatMessage(welcomeMsg, false));
            dbHelper.insertChatMessage(currentSessionId, "ai", welcomeMsg);
        }

        // --- Setup RecyclerView
        chatAdapter = new ChatAdapter(chatList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChatMessages.setLayoutManager(layoutManager);
        rvChatMessages.setAdapter(chatAdapter);

        if (chatList.size() > 0) {
            rvChatMessages.scrollToPosition(chatList.size() - 1);
        }

        // --- Handle Send Button Click
        btnSend.setOnClickListener(v -> {
            String messageText = edtMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {

                // --- Lock UI to prevent spam
                btnSend.setEnabled(false);
                btnSend.setAlpha(0.5f);

                // --- Display User Message
                chatList.add(new ChatMessage(messageText, true));
                chatAdapter.notifyItemInserted(chatList.size() - 1);
                rvChatMessages.scrollToPosition(chatList.size() - 1);
                edtMessage.setText("");

                dbHelper.insertChatMessage(currentSessionId, "user", messageText);

                // --- Call AI API
                apiClient.chatWithHistory(this, chatList, new GeminiAPIClient.ApiCallback() {
                    @Override
                    public void onSuccess(String result) {
                        runOnUiThread(() -> {
                            String cleanText = result.replace("**", "");

                            chatList.add(new ChatMessage(cleanText, false));
                            chatAdapter.notifyItemInserted(chatList.size() - 1);
                            rvChatMessages.scrollToPosition(chatList.size() - 1);
                            dbHelper.insertChatMessage(currentSessionId, "ai", cleanText);

                            btnSend.setEnabled(true);
                            btnSend.setAlpha(1.0f);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            btnSend.setEnabled(true);
                            btnSend.setAlpha(1.0f);
                            Toast.makeText(ChatActivity.this, "AI Connection Error: " + error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        });
    }
}