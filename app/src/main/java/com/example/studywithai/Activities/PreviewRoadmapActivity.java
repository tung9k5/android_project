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

    // --- Global Variables
    private String subject;
    private String goal;
    private String currentLevel;
    private String timeCommitment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_roadmap);

        // --- Map XML Views
        tvPreviewSubject = findViewById(R.id.tvPreviewSubject);
        tvPreviewGoal = findViewById(R.id.tvPreviewGoal);
        tvRoadmapContent = findViewById(R.id.tvRoadmapContent);
        btnRegenerate = findViewById(R.id.btnRegenerate);
        btnConfirmRoadmap = findViewById(R.id.btnConfirmRoadmap);
        btnBack = findViewById(R.id.btnBack);
        edtAiFeedback = findViewById(R.id.edtAiFeedback);
        apiClient = new GeminiAPIClient();

        // --- Get Intent Data
        Intent intent = getIntent();
        roadmapId = intent.getIntExtra("ROADMAP_ID", -1);
        subject = intent.getStringExtra("SUBJECT_NAME");
        if (subject == null) subject = intent.getStringExtra("SUBJECT");
        goal = intent.getStringExtra("GOAL");
        currentLevel = intent.getStringExtra("CURRENT_LEVEL");
        timeCommitment = intent.getStringExtra("TIME_COMMITMENT");

        // --- Null Fallbacks
        if (subject == null) subject = "Programming";
        if (goal == null) goal = "Master the basics";
        if (currentLevel == null) currentLevel = "Beginner";
        if (timeCommitment == null) timeCommitment = "2-3 hours/day";

        // --- Set Static Info
        tvPreviewSubject.setText("Subject: " + subject);
        tvPreviewGoal.setText("Goal: " + goal);

        btnBack.setOnClickListener(v -> finish());

        // --- Auto Generate Roadmap on First Load
        generateAIRoadmap("");

        // --- Regenerate Button Click (-20 Energy)
        btnRegenerate.setOnClickListener(v -> {
            SharedPreferences spf = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
            int currentEnergy = spf.getInt("ENERGY_USER", 100);

            if (currentEnergy >= 20) {
                String feedback = edtAiFeedback.getText().toString().trim();
                if(feedback.isEmpty()) {
                    Toast.makeText(this, "Please enter your modification request!", Toast.LENGTH_SHORT).show();
                    return;
                }

                spf.edit().putInt("ENERGY_USER", currentEnergy - 20).apply();
                generateAIRoadmap(feedback);

            } else {
                Toast.makeText(this, "Not enough energy (Need 20 ⚡)!", Toast.LENGTH_SHORT).show();
            }
        });

        // --- Confirm Roadmap Button Click (-500 Energy)
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

                Toast.makeText(this, "Roadmap confirmed! -500 ⚡", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "You need 500 ⚡. Current: " + currentEnergy + " ⚡. Keep learning!", Toast.LENGTH_LONG).show();
            }
        });
    }

    // --- AI Generator Function (Optimized Prompt)
    private void generateAIRoadmap(String feedback) {
        btnRegenerate.setText("AI is processing...");
        btnRegenerate.setEnabled(false);
        btnConfirmRoadmap.setEnabled(false);

        String prompt;
        if (feedback.isEmpty()) {
            // First time load prompt
            prompt = "Act as a Mentor. Create a SUMMARIZED, SUPER CONCISE roadmap for the subject [" + subject + "] with the goal [" + goal + "]. " +
                    "Level: " + currentLevel + ", Time: " + timeCommitment + ". " +
                    "MANDATORY FORMATTING: \n" +
                    "- Divide into 'Phases' (e.g., Phase 1, Phase 2...).\n" +
                    "- Each phase must ONLY have 2 to 3 bullet points of core tasks.\n" +
                    "- Absolutely NO long paragraphs, NO lengthy explanations, NO unnecessary greetings.";
        } else {
            // Regenerate prompt
            tvRoadmapContent.setText("⏳ Rebuilding roadmap based on your feedback...");
            prompt = "Redesign the roadmap for [" + subject + "] based on this new requirement: '" + feedback + "'. " +
                    "Level: " + currentLevel + ", Time: " + timeCommitment + ". " +
                    "MANDATORY FORMATTING: \n" +
                    "- Present in a SUMMARIZED, SUPER CONCISE format divided by 'Phases'. \n" +
                    "- Only bullet point the most core tasks (2-3 lines/phase). \n" +
                    "- Absolutely NO long paragraphs, NO rambling explanations.";
        }

        apiClient.generateContent(this, prompt, new GeminiAPIClient.ApiCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    // Clean markdown for better TextView display
                    String cleanText = result.replace("**", "").replace("* ", "• ").trim();
                    tvRoadmapContent.setText(cleanText);

                    // Restore UI state
                    btnRegenerate.setText("🔄 Ask AI to modify (-20 ⚡)");
                    btnRegenerate.setEnabled(true);
                    btnConfirmRoadmap.setEnabled(true);
                    edtAiFeedback.setText("");
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    tvRoadmapContent.setText("❌ AI Connection Error: " + error);
                    btnRegenerate.setText("🔄 Ask AI to modify (-20 ⚡)");
                    btnRegenerate.setEnabled(true);
                    btnConfirmRoadmap.setEnabled(true);
                });
            }
        });
    }
}