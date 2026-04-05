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

import com.example.studywithai.Activities.ChatActivity;
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
        // --- Map XML Views
        rvChatSessions = view.findViewById(R.id.rvChatSessions);
        dbHelper = new SqliteDbHelper(getContext());

        // --- Get User ID
        SharedPreferences spf = requireActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        currentUserId = spf.getInt("ID_USER", 1);

        // --- Floating Action Button Click
        view.findViewById(R.id.fabNewChat).setOnClickListener(v -> showNewChatDialog());

        loadChatSessions();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadChatSessions();
    }

    // --- Load Chat History List
    private void loadChatSessions() {
        List<ChatSessionModel> sessionList = dbHelper.getAllSessions(currentUserId);
        ChatSessionAdapter adapter = new ChatSessionAdapter(sessionList, session -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("SESSION_ID", session.getId());
            intent.putExtra("SESSION_TITLE", session.getTitle());
            startActivity(intent);
        });
        rvChatSessions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChatSessions.setAdapter(adapter);
    }

    // --- Show Dialog to Create New Chat Session
    private void showNewChatDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("New Chat Session");

        final EditText input = new EditText(requireContext());
        input.setHint("E.g., Math Homework, English Practice...");
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String title = input.getText().toString().trim();
            if (title.isEmpty()) title = "New Conversation";

            int newSessionId = dbHelper.createNewChatSession(currentUserId, title);

            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("SESSION_ID", newSessionId);
            intent.putExtra("SESSION_TITLE", title);
            startActivity(intent);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}