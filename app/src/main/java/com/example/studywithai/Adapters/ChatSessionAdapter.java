package com.example.studywithai.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studywithai.Models.ChatSessionModel;
import com.example.studywithai.R;
import java.util.List;

public class ChatSessionAdapter extends RecyclerView.Adapter<ChatSessionAdapter.SessionViewHolder> {

    private List<ChatSessionModel> sessionList;
    private OnSessionClickListener listener;

    public interface OnSessionClickListener {
        void onSessionClick(ChatSessionModel session);
    }

    public ChatSessionAdapter(List<ChatSessionModel> sessionList, OnSessionClickListener listener) {
        this.sessionList = sessionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạm mượn layout item_roadmap của bạn hoặc tạo 1 layout đơn giản tương tự
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        ChatSessionModel session = sessionList.get(position);
        holder.tvTitle.setText(session.getTitle());
        holder.tvDate.setText(session.getCreatedAt());

        holder.itemView.setOnClickListener(v -> listener.onSessionClick(session));
    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate;
        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(android.R.id.text1);
            tvDate = itemView.findViewById(android.R.id.text2);
        }
    }
}