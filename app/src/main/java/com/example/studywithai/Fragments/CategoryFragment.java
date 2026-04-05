package com.example.studywithai.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studywithai.Activities.ChatActivity; // Lát nữa ta sẽ tạo file này
import com.example.studywithai.Adapters.ChatSessionAdapter;
import com.example.studywithai.Databases.SqliteDbHelper;
import com.example.studywithai.Models.ChatSessionModel;
import com.example.studywithai.R;

import java.util.List;

public class CategoryFragment extends Fragment {

    private RecyclerView rvChatSessions;
    private SqliteDbHelper dbHelper;
    private int currentUserId;

    public CategoryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvChatSessions = view.findViewById(R.id.rvChatSessions);
        dbHelper = new SqliteDbHelper(getContext());

        SharedPreferences spf = requireActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        currentUserId = spf.getInt("ID_USER", 1); // Lấy ID người dùng

        // Nút tạo đoạn chat mới
        view.findViewById(R.id.fabNewChat).setOnClickListener(v -> showNewChatDialog());

        loadChatSessions();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadChatSessions(); // Load lại lịch sử mỗi khi quay lại tab này
    }

    private void loadChatSessions() {
        List<ChatSessionModel> sessionList = dbHelper.getAllSessions(currentUserId);
        ChatSessionAdapter adapter = new ChatSessionAdapter(sessionList, session -> {
            // Mở màn hình chat chi tiết
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("SESSION_ID", session.getId());
            intent.putExtra("SESSION_TITLE", session.getTitle());
            startActivity(intent);
        });
        rvChatSessions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChatSessions.setAdapter(adapter);
    }

    private void showNewChatDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Tạo đoạn chat mới");

        final EditText input = new EditText(requireContext());
        input.setHint("VD: Hỏi bài tập Toán, Luyện Tiếng Anh...");
        builder.setView(input);

        builder.setPositiveButton("Tạo", (dialog, which) -> {
            String title = input.getText().toString().trim();
            if (title.isEmpty()) title = "Cuộc trò chuyện mới";

            // Tạo phiên chat trong SQLite
            int newSessionId = dbHelper.createNewChatSession(currentUserId, title);

            // Chuyển sang màn hình chat
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("SESSION_ID", newSessionId);
            intent.putExtra("SESSION_TITLE", title);
            startActivity(intent);
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}