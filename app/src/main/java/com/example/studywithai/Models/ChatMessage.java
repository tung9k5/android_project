package com.example.studywithai.Models;

public class ChatMessage {
    private String content;
    private boolean isUser; // true nếu là user gửi, false nếu là AI gửi

    // Constructor (Hàm khởi tạo)
    public ChatMessage(String content, boolean isUser) {
        this.content = content;
        this.isUser = isUser;
    }

    // Hàm lấy nội dung tin nhắn (Hàm mà GeminiAPIClient đang tìm kiếm)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Hàm kiểm tra xem ai là người gửi
    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }
}