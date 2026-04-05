package com.example.studywithai.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studywithai.API.GeminiAPIClient;
import com.example.studywithai.Databases.SqliteDbHelper;
import com.example.studywithai.R;

public class PreviewRoadmapActivity extends AppCompatActivity {

    private TextView tvPreviewSubject, tvPreviewGoal, tvRoadmapContent;
    private Button btnRegenerate, btnConfirmRoadmap;
    private ImageButton btnBack;
    private int roadmapId;
    private EditText edtAiFeedback;
    private GeminiAPIClient apiClient;

    // Khai báo biến toàn cục
    private String subject;
    private String goal;
    private String currentLevel;
    private String timeCommitment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_roadmap);

        // Ánh xạ ID
        tvPreviewSubject = findViewById(R.id.tvPreviewSubject);
        tvPreviewGoal = findViewById(R.id.tvPreviewGoal);
        tvRoadmapContent = findViewById(R.id.tvRoadmapContent); // TEXTVIEW MỚI THÊM
        btnRegenerate = findViewById(R.id.btnRegenerate);
        btnConfirmRoadmap = findViewById(R.id.btnConfirmRoadmap);
        btnBack = findViewById(R.id.btnBack);
        edtAiFeedback = findViewById(R.id.edtAiFeedback);
        apiClient = new GeminiAPIClient();

        // 1. Nhận dữ liệu truyền sang
        Intent intent = getIntent();
        roadmapId = intent.getIntExtra("ROADMAP_ID", -1);
        subject = intent.getStringExtra("SUBJECT_NAME");
        if (subject == null) subject = intent.getStringExtra("SUBJECT");
        goal = intent.getStringExtra("GOAL");
        currentLevel = intent.getStringExtra("CURRENT_LEVEL");
        timeCommitment = intent.getStringExtra("TIME_COMMITMENT");

        // Dự phòng Null
        if (subject == null) subject = "Lập trình";
        if (goal == null) goal = "Nắm vững kiến thức";
        if (currentLevel == null) currentLevel = "Người mới bắt đầu";
        if (timeCommitment == null) timeCommitment = "2-3 giờ mỗi ngày";

        // Hiển thị thông tin tĩnh
        tvPreviewSubject.setText("Môn học: " + subject);
        tvPreviewGoal.setText("Mục tiêu: " + goal);

        btnBack.setOnClickListener(v -> finish());

        // GỌI AI NGAY LẬP TỨC KHI MỞ TRANG LẦN ĐẦU
        generateAIRoadmap("");

        // Nút Yêu cầu sửa lại (-20 ⚡)
        btnRegenerate.setOnClickListener(v -> {
            SharedPreferences spf = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
            int currentEnergy = spf.getInt("ENERGY_USER", 100);

            if (currentEnergy >= 20) {
                String feedback = edtAiFeedback.getText().toString().trim();
                if(feedback.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập lý do muốn sửa!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Trừ năng lượng ngay khi bấm
                spf.edit().putInt("ENERGY_USER", currentEnergy - 20).apply();

                // Gọi AI sinh lại lộ trình với yêu cầu mới
                generateAIRoadmap(feedback);

            } else {
                Toast.makeText(this, "Không đủ 20 ⚡ để sửa lại!", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút Chốt hợp đồng (-500 ⚡)
        btnConfirmRoadmap.setOnClickListener(v -> {
            if (roadmapId == -1) return;

            SharedPreferences spf = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
            int currentEnergy = spf.getInt("ENERGY_USER", 100);

            if (currentEnergy >= 500) {
                SharedPreferences.Editor editor = spf.edit();
                editor.putInt("ENERGY_USER", currentEnergy - 500);
                editor.apply();

                SqliteDbHelper dbHelper = new SqliteDbHelper(this);
                dbHelper.updateRoadmapStatus(roadmapId, "ACTIVE");

                Toast.makeText(this, "Chốt Lộ trình thành công! Đã trừ 500 ⚡", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Bạn cần 500 ⚡. Hiện tại chỉ có " + currentEnergy + " ⚡. Hãy cày thêm nhé!", Toast.LENGTH_LONG).show();
            }
        });
    }

    // HÀM XỬ LÝ GỌI AI (Dùng chung cho cả lúc mới mở và lúc yêu cầu sửa)
    // HÀM XỬ LÝ GỌI AI (Đã tối ưu Prompt để siêu ngắn gọn)
    private void generateAIRoadmap(String feedback) {
        btnRegenerate.setText("AI Đang xử lý...");
        btnRegenerate.setEnabled(false);
        btnConfirmRoadmap.setEnabled(false); // Khóa nút chốt hợp đồng khi chưa có lộ trình

        String prompt;
        if (feedback.isEmpty()) {
            // Trường hợp 1: Vừa mở trang, chưa có feedback
            prompt = "Đóng vai một Mentor. Xây dựng lộ trình TÓM TẮT, SIÊU NGẮN GỌN cho môn [" + subject + "] với mục tiêu [" + goal + "]. " +
                    "Trình độ: " + currentLevel + ", Thời gian: " + timeCommitment + ". " +
                    "YÊU CẦU ĐỊNH DẠNG BẮT BUỘC: \n" +
                    "- Chỉ chia thành các 'Giai đoạn' (VD: Giai đoạn 1, Giai đoạn 2...).\n" +
                    "- Mỗi giai đoạn chỉ gạch đầu dòng 2 đến 3 công việc cốt lõi nhất cần làm.\n" +
                    "- Tuyệt đối KHÔNG viết đoạn văn dài, KHÔNG giải thích dông dài, KHÔNG dùng các câu chào hỏi thừa thãi.";
        } else {
            // Trường hợp 2: Yêu cầu sửa lại
            tvRoadmapContent.setText("⏳ Đang xây dựng lại lộ trình TÓM TẮT theo yêu cầu của bạn...");
            prompt = "Thiết kế lại lộ trình môn [" + subject + "] theo yêu cầu mới: '" + feedback + "'. " +
                    "Trình độ: " + currentLevel + ", Thời gian: " + timeCommitment + ". " +
                    "YÊU CẦU ĐỊNH DẠNG BẮT BUỘC: \n" +
                    "- Trình bày dạng TÓM TẮT, SIÊU NGẮN GỌN chia theo từng 'Giai đoạn'. \n" +
                    "- Chỉ gạch đầu dòng các việc cốt lõi nhất (2-3 dòng/giai đoạn). \n" +
                    "- Tuyệt đối KHÔNG viết văn dài dòng, KHÔNG giải thích lan man.";
        }

        apiClient.generateContent(this, prompt, new GeminiAPIClient.ApiCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    // Dọn dẹp ký tự Markdown bôi đậm thừa thãi để text hiển thị đẹp và đồng bộ hơn trên TextView
                    String cleanText = result.replace("**", "").replace("* ", "• ").trim();

                    // In kết quả ra màn hình
                    tvRoadmapContent.setText(cleanText);

                    // Khôi phục giao diện
                    btnRegenerate.setText("🔄 Yêu cầu AI sửa lại (Tốn 20 ⚡)");
                    btnRegenerate.setEnabled(true);
                    btnConfirmRoadmap.setEnabled(true);
                    edtAiFeedback.setText(""); // Xóa rỗng ô nhập lý do
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    tvRoadmapContent.setText("❌ Có lỗi xảy ra khi kết nối AI: " + error);
                    btnRegenerate.setText("🔄 Yêu cầu AI sửa lại (Tốn 20 ⚡)");
                    btnRegenerate.setEnabled(true);
                    btnConfirmRoadmap.setEnabled(true); // Vẫn cho phép chốt nếu lỗi mạng
                });
            }
        });
    }
}