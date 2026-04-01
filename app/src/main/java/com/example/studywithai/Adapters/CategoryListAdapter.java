package com.example.studywithai.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studywithai.Models.CategoryModel;
import com.example.studywithai.R;

import java.util.ArrayList;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryItemViewHolder> {
    public ArrayList<CategoryModel> categoryModels;
    public Context context;
    public CategoryListAdapter(ArrayList<CategoryModel> models, Context myContext){
        categoryModels = models;
        context = myContext;
    }

    @NonNull
    @Override
    public CategoryListAdapter.CategoryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_list,parent,false);
        return new CategoryItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryListAdapter.CategoryItemViewHolder holder, int position) {
        CategoryModel model = categoryModels.get(position);
        holder.tvNameCategory.setText(model.getName());
        holder.tvCreateAt.setText(String.format("Created at: %s", model.getCreatedAt()));
    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public class CategoryItemViewHolder extends RecyclerView.ViewHolder{
        TextView tvNameCategory, tvCreateAt;
        View viewItem;
        public CategoryItemViewHolder(@NonNull View itemView) {
            super(itemView);
            viewItem = itemView;
            tvNameCategory = viewItem.findViewById(R.id.nameCategory);
            tvCreateAt     = viewItem.findViewById(R.id.timeCategory);
        }
    }
}
