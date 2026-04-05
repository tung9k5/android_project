package com.example.studywithai.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studywithai.Models.TaskModel;
import com.example.studywithai.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<TaskModel> taskList;
    private OnTaskClickListener listener;

    // Giao diện (Interface) để truyền sự kiện click về Fragment
    public interface OnTaskClickListener {
        void onTaskClick(TaskModel task);
    }

    public TaskAdapter(List<TaskModel> taskList, OnTaskClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskModel task = taskList.get(position);
        holder.tvTaskName.setText(task.getTaskName());
        holder.tvTaskReward.setText("+" + task.getXpReward() + " XP");
        holder.cbTaskDone.setChecked(task.isCompleted());

        // Làm mờ đi nếu đã hoàn thành
        if (task.isCompleted()) {
            holder.itemView.setAlpha(0.6f);
        } else {
            holder.itemView.setAlpha(1.0f);
        }

        // Bắt sự kiện click vào toàn bộ CardView
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskClick(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbTaskDone;
        TextView tvTaskName, tvTaskReward;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cbTaskDone = itemView.findViewById(R.id.cbTaskDone);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvTaskReward = itemView.findViewById(R.id.tvTaskReward);
        }
    }
}