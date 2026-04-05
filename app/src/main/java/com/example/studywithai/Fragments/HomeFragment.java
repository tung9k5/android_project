package com.example.studywithai.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studywithai.API.GeminiAPIClient;
import com.example.studywithai.Adapters.TaskAdapter;
import com.example.studywithai.Databases.SqliteDbHelper;
import com.example.studywithai.Models.RoadmapModel;
import com.example.studywithai.Models.TaskModel;
import com.example.studywithai.R;
import com.example.studywithai.Utils.DailyManager;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvGreeting, tvStreak, tvEnergy, tvHighlightSubject, tvHighlightProgressText, tvHighlightSubtitle;
    private Button btnContinueLearning, btnAttendance;
    private RecyclerView rvDailyTasks;
    private TaskAdapter taskAdapter;
    private List<TaskModel> taskList;
    private View layoutEnergyShop;
    private android.widget.ProgressBar pbHighlightProgress;

    private DailyManager dailyManager;
    private SqliteDbHelper dbHelper;
    private String todayDate;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Map XML Views
        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvStreak = view.findViewById(R.id.tvStreak);
        tvEnergy = view.findViewById(R.id.tvEnergy);
        btnContinueLearning = view.findViewById(R.id.btnContinueLearning);
        rvDailyTasks = view.findViewById(R.id.rvDailyTasks);
        tvHighlightSubject = view.findViewById(R.id.tvHighlightSubject);
        tvHighlightSubtitle = view.findViewById(R.id.tvHighlightSubtitle);
        tvHighlightProgressText = view.findViewById(R.id.tvHighlightProgressText);
        pbHighlightProgress = view.findViewById(R.id.pbHighlightProgress);
        btnAttendance = view.findViewById(R.id.btnAttendance);

        // --- Initialize Database and Managers
        dbHelper = new SqliteDbHelper(getContext());
        dailyManager = new DailyManager(getContext());

        // --- Update and Get Streak
        dailyManager.checkAndUpdateStreak();
        int streak = dailyManager.getCurrentStreak();
        if(tvStreak != null) tvStreak.setText("🔥 " + streak);

        todayDate = dailyManager.getTodayString();

        // --- Handle Daily Attendance
        if (dailyManager.isAttendanceClaimedToday()) {
            btnAttendance.setText("Reward Claimed");
            btnAttendance.setEnabled(false);
            btnAttendance.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.GRAY));
        } else {
            btnAttendance.setText("🎁 Claim 50 ⚡");
            btnAttendance.setEnabled(true);
            btnAttendance.setOnClickListener(v -> {
                dailyManager.claimAttendanceReward(50);
                tvEnergy.setText("⚡ " + requireContext().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE).getInt("ENERGY_USER", 100));
                btnAttendance.setText("Reward Claimed");
                btnAttendance.setEnabled(false);
                btnAttendance.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.GRAY));
                Toast.makeText(getContext(), "Attendance claimed! +50⚡", Toast.LENGTH_SHORT).show();
            });
        }

        // --- Open Energy Shop
        if(tvEnergy != null) {
            tvEnergy.setOnClickListener(v -> showEnergyShop());
        }

        // --- Setup Task RecyclerView
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onTaskClick(TaskModel task) {
                if (getActivity() instanceof com.example.studywithai.Activities.MenuActivity) {
                    ((com.example.studywithai.Activities.MenuActivity) getActivity()).switchToTab(task.getTargetTab());
                }
            }
        });
        rvDailyTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDailyTasks.setAdapter(taskAdapter);

        loadDailyTasks();
    }

    // --- Load Tasks from SQLite or AI
    private void loadDailyTasks() {
        List<TaskModel> savedTasks = dbHelper.getTasksByDate(todayDate);

        if (savedTasks.size() > 0) {
            taskList.clear();
            taskList.addAll(savedTasks);
            taskAdapter.notifyDataSetChanged();
        } else {
            dbHelper.clearOldTasks(todayDate);
            generateTasksFromAI();
        }
    }

    // --- Call Gemini API to generate daily tasks
    private void generateTasksFromAI() {
        GeminiAPIClient apiClient = new GeminiAPIClient();

        taskList.clear();
        taskList.add(new TaskModel("🤖 AI is generating tasks...", 0, false, 0));
        taskAdapter.notifyDataSetChanged();

        String prompt = "Create 3 random study tasks for a student today. Return strictly in JSON ARRAY format (without markdown code blocks): [{\"name\":\"Short task name\",\"xp\":30,\"tab\":1}]. The 'tab' value must be from 1 to 3.";

        apiClient.generateContent(getContext(), prompt, new GeminiAPIClient.ApiCallback() {
            @Override
            public void onSuccess(String result) {
                String cleanJson = result.replace("```json", "").replace("```", "").trim();
                try {
                    org.json.JSONArray arr = new org.json.JSONArray(cleanJson);

                    dbHelper.insertDailyTask(todayDate, "Daily Login", 10, 0);

                    for(int i = 0; i < arr.length(); i++){
                        org.json.JSONObject obj = arr.getJSONObject(i);
                        dbHelper.insertDailyTask(todayDate, obj.getString("name"), obj.getInt("xp"), obj.getInt("tab"));
                    }

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> loadDailyTasks());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            dbHelper.insertDailyTask(todayDate, "Daily Login", 10, 0);
                            dbHelper.insertDailyTask(todayDate, "Complete 1 Quiz", 50, 2);
                            loadDailyTasks();
                        });
                    }
                }
            }

            @Override
            public void onError(String error) {
                if(getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Network error, using fallback tasks!", Toast.LENGTH_SHORT).show();
                        dbHelper.insertDailyTask(todayDate, "Daily Login", 10, 0);
                        dbHelper.insertDailyTask(todayDate, "Review learned theory", 20, 1);
                        loadDailyTasks();
                    });
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences spf = requireActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        int currentEnergy = spf.getInt("ENERGY_USER", 100);
        int currentUserId = spf.getInt("ID_USER", 0);

        if (tvEnergy != null) tvEnergy.setText("⚡ " + currentEnergy);

        // --- Display Highlighted Roadmap
        SqliteDbHelper dbHelper = new SqliteDbHelper(getContext());
        RoadmapModel highlightCourse = dbHelper.getHighlightedRoadmap(currentUserId);

        if (highlightCourse != null) {
            tvHighlightSubtitle.setText(highlightCourse.isPinned() ? "Pinned Roadmap" : "Continue Roadmap");
            tvHighlightSubject.setText(highlightCourse.getSubjectName());
            pbHighlightProgress.setProgress(highlightCourse.getProgress());
            tvHighlightProgressText.setText(highlightCourse.getProgress() + "%");

            btnContinueLearning.setText("Continue Learning");
            btnContinueLearning.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Enter course: " + highlightCourse.getSubjectName(), Toast.LENGTH_SHORT).show();
            });
        } else {
            tvHighlightSubtitle.setText("Explore Now");
            tvHighlightSubject.setText("No active roadmap");
            pbHighlightProgress.setProgress(0);
            tvHighlightProgressText.setText("0%");

            btnContinueLearning.setText("Create Roadmap");
            btnContinueLearning.setOnClickListener(v -> {
                ((com.example.studywithai.Activities.MenuActivity) requireActivity()).switchToTab(1);
            });
        }
    }

    // --- Mock Energy Payment Shop
    private void showEnergyShop() {
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(requireActivity());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_shop, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        View btnBuyPack1 = bottomSheetView.findViewById(R.id.btnBuyPack1);
        View btnBuyPack2 = bottomSheetView.findViewById(R.id.btnBuyPack2);
        View btnBuyPack3 = bottomSheetView.findViewById(R.id.btnBuyPack3);

        android.view.View.OnClickListener buyEvent = v -> {
            int addedEnergy = 0;
            if (v.getId() == R.id.btnBuyPack1) addedEnergy = 150;
            else if (v.getId() == R.id.btnBuyPack2) addedEnergy = 600;
            else if (v.getId() == R.id.btnBuyPack3) addedEnergy = 2000;

            SharedPreferences spf = requireActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
            int currentEnergy = spf.getInt("ENERGY_USER", 100);
            spf.edit().putInt("ENERGY_USER", currentEnergy + addedEnergy).apply();

            if (tvEnergy != null) tvEnergy.setText("⚡ " + (currentEnergy + addedEnergy));

            Toast.makeText(getContext(), "Mock transaction successful! +" + addedEnergy + "⚡", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        };

        btnBuyPack1.setOnClickListener(buyEvent);
        btnBuyPack2.setOnClickListener(buyEvent);
        btnBuyPack3.setOnClickListener(buyEvent);

        bottomSheetDialog.show();
    }
}