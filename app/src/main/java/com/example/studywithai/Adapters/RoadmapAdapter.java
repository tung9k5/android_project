package com.example.studywithai.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studywithai.Models.RoadmapModel;
import com.example.studywithai.R;

import java.util.List;

public class RoadmapAdapter extends RecyclerView.Adapter<RoadmapAdapter.RoadmapViewHolder> {

    private List<RoadmapModel> roadmapList;
    private OnRoadmapClickListener listener;

    // Cập nhật Interface để có thêm sự kiện cho nút 3 chấm
    public interface OnRoadmapClickListener {
        void onRoadmapClick(RoadmapModel roadmap);
        void onMoreActionClick(RoadmapModel roadmap, String actionType); // actionType: "DELETE" hoặc "PIN"
    }

    public RoadmapAdapter(List<RoadmapModel> roadmapList, OnRoadmapClickListener listener) {
        this.roadmapList = roadmapList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoadmapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_roadmap, parent, false);
        return new RoadmapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoadmapViewHolder holder, int position) {
        RoadmapModel roadmap = roadmapList.get(position);

        holder.tvRoadmapSubject.setText(roadmap.getSubjectName());
        holder.tvRoadmapGoal.setText("Mục tiêu: " + roadmap.getGoal());
        holder.pbRoadmapProgress.setProgress(roadmap.getProgress());
        holder.tvProgressText.setText(roadmap.getProgress() + "%");

        if (roadmap.getStatus().equals("DRAFT")) {
            holder.tvRoadmapStatus.setText("Bản nháp");
            holder.tvRoadmapStatus.setTextColor(Color.parseColor("#E65100"));
            holder.itemView.setAlpha(0.7f);
        } else {
            holder.tvRoadmapStatus.setText("Đang học");
            holder.tvRoadmapStatus.setTextColor(Color.parseColor("#4CAF50"));
            holder.itemView.setAlpha(1.0f);
        }

        // 1. Click vào thẻ khóa học (Vào xem nội dung)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onRoadmapClick(roadmap);
        });

        // Trong hàm onBindViewHolder của RoadmapAdapter.java:

// Hiển thị thêm dấu ghim cạnh tên nếu được ghim
        if (roadmap.isPinned()) {
            holder.tvRoadmapSubject.setText(roadmap.getSubjectName());
        } else {
            holder.tvRoadmapSubject.setText(roadmap.getSubjectName());
        }

        holder.btnMoreOptions.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), holder.btnMoreOptions);

            // BỎ ICON: Chỉ để Text thuần túy
            String pinText = roadmap.isPinned() ? "Bỏ ghim" : "Ghim khóa học";
            popup.getMenu().add(Menu.NONE, 1, 1, pinText);
            popup.getMenu().add(Menu.NONE, 2, 2, "Xóa");

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 1) {
                    listener.onMoreActionClick(roadmap, "PIN");
                } else if (item.getItemId() == 2) {
                    listener.onMoreActionClick(roadmap, "DELETE");
                }
                return true;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return roadmapList.size();
    }

    static class RoadmapViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoadmapSubject, tvRoadmapStatus, tvRoadmapGoal, tvProgressText, btnMoreOptions;
        ProgressBar pbRoadmapProgress;

        public RoadmapViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoadmapSubject = itemView.findViewById(R.id.tvRoadmapSubject);
            tvRoadmapStatus = itemView.findViewById(R.id.tvRoadmapStatus);
            tvRoadmapGoal = itemView.findViewById(R.id.tvRoadmapGoal);
            pbRoadmapProgress = itemView.findViewById(R.id.pbRoadmapProgress);
            tvProgressText = itemView.findViewById(R.id.tvProgressText);
            btnMoreOptions = itemView.findViewById(R.id.btnMoreOptions); // Ánh xạ nút 3 chấm
        }
    }
}