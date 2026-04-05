package com.example.studywithai.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studywithai.Models.ChatMessage;
import com.example.studywithai.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> messageList;
    private static final int TYPE_USER = 1;
    private static final int TYPE_AI = 2;

    public ChatAdapter(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }

    // Phân loại tin nhắn để chọn đúng Layout
    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).isUser()) {
            return TYPE_USER;
        } else {
            return TYPE_AI;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_ai, parent, false);
            return new AiViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        if (holder.getItemViewType() == TYPE_USER) {
            ((UserViewHolder) holder).tvUserMessage.setText(message.getContent());
        } else {
            ((AiViewHolder) holder).tvAiMessage.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // ViewHolder cho Người dùng
    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserMessage;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserMessage = itemView.findViewById(R.id.tvUserMessage);
        }
    }

    // ViewHolder cho AI
    static class AiViewHolder extends RecyclerView.ViewHolder {
        TextView tvAiMessage;
        public AiViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAiMessage = itemView.findViewById(R.id.tvAiMessage);
        }
    }
}