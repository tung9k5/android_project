package com.example.studywithai.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studywithai.Activities.PreviewRoadmapActivity;
import com.example.studywithai.Adapters.RoadmapAdapter;
import com.example.studywithai.Databases.SqliteDbHelper;
import com.example.studywithai.Models.RoadmapModel;
import com.example.studywithai.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class RoadmapFragment extends Fragment {

    private RecyclerView rvRoadmaps;
    private FloatingActionButton fabAddRoadmap;

    private List<RoadmapModel> roadmapList;
    private RoadmapAdapter adapter;
    private SqliteDbHelper dbHelper;
    private int currentUserId;

    public RoadmapFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_roadmap, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvRoadmaps = view.findViewById(R.id.rvRoadmaps);
        fabAddRoadmap = view.findViewById(R.id.fabAddRoadmap);

        dbHelper = new SqliteDbHelper(getContext());
        SharedPreferences spf = requireActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        currentUserId = spf.getInt("ID_USER", 0);

        roadmapList = new ArrayList<>();
        adapter = new RoadmapAdapter(roadmapList, new RoadmapAdapter.OnRoadmapClickListener() {
            @Override
            public void onRoadmapClick(RoadmapModel roadmap) {
                if (roadmap.getStatus().equals("DRAFT")) {
                    Intent intent = new Intent(getContext(), PreviewRoadmapActivity.class);
                    intent.putExtra("ROADMAP_ID", roadmap.getId());
                    intent.putExtra("SUBJECT_NAME", roadmap.getSubjectName());
                    intent.putExtra("GOAL", roadmap.getGoal());
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Start studying: " + roadmap.getSubjectName(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onMoreActionClick(RoadmapModel roadmap, String actionType) {
                if (actionType.equals("PIN")) {
                    boolean newPinStatus = !roadmap.isPinned();
                    dbHelper.updatePinStatus(roadmap.getId(), newPinStatus);

                    String msg = newPinStatus ? "Pinned to the top!" : "Unpinned";
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

                    loadRoadmapData();
                }
                else if (actionType.equals("DELETE")) {
                    new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Delete the route?")
                            .setMessage("Are you sure you want to delete the route? '" + roadmap.getSubjectName() + "' No? This action cannot be undone.")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                dbHelper.deleteRoadmap(roadmap.getId());
                                Toast.makeText(getContext(), "Deleted successfully!", Toast.LENGTH_SHORT).show();
                                loadRoadmapData();
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            }
        });

        rvRoadmaps.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRoadmaps.setAdapter(adapter);

        fabAddRoadmap.setOnClickListener(v -> {

            int totalCount = dbHelper.countAllRoadmaps(currentUserId);

            // Lấy role từ SharedPreferences (1: Free, 2: Premium)
            SharedPreferences spfContext = requireActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
            int userRole = spfContext.getInt("ROLE_USER", 1);

            if (userRole == 1 && totalCount >= 2) {
                Toast.makeText(getContext(), "The free version only allows you to keep a maximum of 2 routes (including drafts). Please delete some or upgrade to Premium!", Toast.LENGTH_LONG).show();
            } else {
                showCreateRoadmapBottomSheet();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRoadmapData();
    }

    private void loadRoadmapData() {
        roadmapList.clear();
        roadmapList.addAll(dbHelper.getRoadmapsByUserId(currentUserId));
        adapter.notifyDataSetChanged();
    }

    private void showCreateRoadmapBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_roadmap_form, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextInputEditText edtSubjectName = bottomSheetView.findViewById(R.id.edtSubjectName);
        TextInputEditText edtGoal = bottomSheetView.findViewById(R.id.edtGoal);
        Spinner spnCurrentLevel = bottomSheetView.findViewById(R.id.spnCurrentLevel);
        Spinner spnTimeCommitment = bottomSheetView.findViewById(R.id.spnTimeCommitment);
        Spinner spnMentorTone = bottomSheetView.findViewById(R.id.spnMentorTone);
        View layoutOtherTone = bottomSheetView.findViewById(R.id.layoutOtherTone);
        TextInputEditText edtOtherTone = bottomSheetView.findViewById(R.id.edtOtherTone);
        Button btnGenerateDraft = bottomSheetView.findViewById(R.id.btnGenerateDraft);

        String[] levels = {"Know nothing", "Know a little","Have a basic understanding", "Advanced"};
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, levels);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCurrentLevel.setAdapter(levelAdapter);

        String[] times = {"30 minutes/day", "1 hour/day", "2 hours/day", "More than 2 hours/day"};
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, times);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTimeCommitment.setAdapter(timeAdapter);

        String[] tones = {"Strict, disciplined (Deadline)", "Encouraging, gentle (Relaxed)", "Other"};
        ArrayAdapter<String> toneAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, tones);
        toneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMentorTone.setAdapter(toneAdapter);

        spnMentorTone.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (tones[position].equals("Other")) layoutOtherTone.setVisibility(View.VISIBLE);
                else layoutOtherTone.setVisibility(View.GONE);
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        btnGenerateDraft.setOnClickListener(v -> {
            String subject = edtSubjectName.getText().toString().trim();
            String goal = edtGoal.getText().toString().trim();
            String finalTone = spnMentorTone.getSelectedItem().toString();

            if (subject.isEmpty() || goal.isEmpty()) {
                Toast.makeText(getContext(), "Please enter the Subject Name and Objectives!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (finalTone.equals("Other")) {
                finalTone = edtOtherTone.getText().toString().trim();
                if (finalTone.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter the style!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // XỬ LÝ TRỪ NĂNG LƯỢNG THẬT
            SharedPreferences spf = requireActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
            int currentEnergy = spf.getInt("ENERGY_USER", 100);

            if(currentEnergy < 20) {
                Toast.makeText(getContext(), "You need 20 ⚡ to provide advice! Please complete the task.", Toast.LENGTH_LONG).show();
                return;
            }

            spf.edit().putInt("ENERGY_USER", currentEnergy - 20).apply();
            Toast.makeText(getContext(), "20 ⚡ consultation fee has been deducted!", Toast.LENGTH_SHORT).show();

            long newId = dbHelper.insertRoadmap(currentUserId, subject, goal,
                    spnCurrentLevel.getSelectedItem().toString(),
                    spnTimeCommitment.getSelectedItem().toString(),
                    finalTone, "DRAFT", 0);

            if (newId != -1) {
                bottomSheetDialog.dismiss();

                Intent intent = new Intent(getContext(), PreviewRoadmapActivity.class);
                intent.putExtra("ROADMAP_ID", (int) newId);
                intent.putExtra("SUBJECT_NAME", subject);
                intent.putExtra("GOAL", goal);
                startActivity(intent);

                Toast.makeText(getContext(), "Preparing the itinerary...", Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetDialog.show();
    }
}