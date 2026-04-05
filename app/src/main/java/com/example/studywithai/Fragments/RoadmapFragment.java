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
                    Toast.makeText(getContext(), "Vào học: " + roadmap.getSubjectName(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onMoreActionClick(RoadmapModel roadmap, String actionType) {
                if (actionType.equals("PIN")) {
                    // Đảo ngược trạng thái Ghim hiện tại
                    boolean newPinStatus = !roadmap.isPinned();
                    dbHelper.updatePinStatus(roadmap.getId(), newPinStatus);

                    String msg = newPinStatus ? "Đã ghim lên đầu!" : "Đã bỏ ghim";
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

                    loadRoadmapData(); // Tải lại để RecyclerView sắp xếp lại theo is_pinned DESC
                }
                else if (actionType.equals("DELETE")) {
                    // Hiển thị hộp thoại Xác nhận trước khi Xóa
                    new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Xóa lộ trình?")
                            .setMessage("Bạn có chắc chắn muốn xóa lộ trình '" + roadmap.getSubjectName() + "' không? Hành động này không thể hoàn tác.")
                            .setPositiveButton("Xóa", (dialog, which) -> {
                                // Xóa trong Database
                                dbHelper.deleteRoadmap(roadmap.getId());
                                Toast.makeText(getContext(), "Đã xóa thành công!", Toast.LENGTH_SHORT).show();
                                // Tải lại danh sách
                                loadRoadmapData();
                            })
                            .setNegativeButton("Hủy", null)
                            .show();
                }
            }
        });

        rvRoadmaps.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRoadmaps.setAdapter(adapter);

        fabAddRoadmap.setOnClickListener(v -> {
            // KIỂM TRA GIỚI HẠN BẢN FREE (Đếm tổng cả nháp và đang học)
            int totalCount = dbHelper.countAllRoadmaps(currentUserId);

            // Lấy role từ SharedPreferences (1: Free, 2: Premium)
            SharedPreferences spfContext = requireActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
            int userRole = spfContext.getInt("ROLE_USER", 1);

            if (userRole == 1 && totalCount >= 2) {
                Toast.makeText(getContext(), "Bản Free chỉ được giữ tối đa 2 lộ trình (Bao gồm cả bản nháp). Vui lòng xóa bớt hoặc lên Premium!", Toast.LENGTH_LONG).show();
            } else {
                showCreateRoadmapBottomSheet();
            }
        });
    }

    // Load lại dữ liệu mỗi khi quay lại Tab này
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

        String[] levels = {"Chưa biết gì", "Biết một chút", "Có nền tảng", "Nâng cao"};
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, levels);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCurrentLevel.setAdapter(levelAdapter);

        String[] times = {"15 phút / ngày", "30 phút / ngày", "1 tiếng / ngày", "2 tiếng / ngày"};
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, times);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTimeCommitment.setAdapter(timeAdapter);

        String[] tones = {"Nghiêm khắc, kỷ luật (Deadline)", "Động viên, nhẹ nhàng (Thoải mái)", "Khác"};
        ArrayAdapter<String> toneAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, tones);
        toneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMentorTone.setAdapter(toneAdapter);

        spnMentorTone.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (tones[position].equals("Khác")) layoutOtherTone.setVisibility(View.VISIBLE);
                else layoutOtherTone.setVisibility(View.GONE);
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        btnGenerateDraft.setOnClickListener(v -> {
            String subject = edtSubjectName.getText().toString().trim();
            String goal = edtGoal.getText().toString().trim();
            String finalTone = spnMentorTone.getSelectedItem().toString();

            if (subject.isEmpty() || goal.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập Tên môn và Mục tiêu!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (finalTone.equals("Khác")) {
                finalTone = edtOtherTone.getText().toString().trim();
                if (finalTone.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập phong cách!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // XỬ LÝ TRỪ NĂNG LƯỢNG THẬT
            SharedPreferences spf = requireActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
            int currentEnergy = spf.getInt("ENERGY_USER", 100);

            if(currentEnergy < 20) {
                Toast.makeText(getContext(), "Bạn cần 20 ⚡ để tư vấn! Hãy làm nhiệm vụ nhé.", Toast.LENGTH_LONG).show();
                return;
            }

            // Trừ 20 ⚡
            spf.edit().putInt("ENERGY_USER", currentEnergy - 20).apply();
            Toast.makeText(getContext(), "Đã trừ 20 ⚡ phí tư vấn!", Toast.LENGTH_SHORT).show();

            long newId = dbHelper.insertRoadmap(currentUserId, subject, goal,
                    spnCurrentLevel.getSelectedItem().toString(),
                    spnTimeCommitment.getSelectedItem().toString(),
                    finalTone, "DRAFT", 0);

            if (newId != -1) {
                bottomSheetDialog.dismiss();

                // 2. NHẢY THẲNG SANG TAB XÁC NHẬN (PreviewRoadmapActivity)
                Intent intent = new Intent(getContext(), PreviewRoadmapActivity.class);
                intent.putExtra("ROADMAP_ID", (int) newId);
                intent.putExtra("SUBJECT_NAME", subject);
                intent.putExtra("GOAL", goal);
                startActivity(intent);

                Toast.makeText(getContext(), "Đang chuẩn bị lộ trình...", Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetDialog.show();
    }
}