package com.example.studywithai.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.studywithai.Fragments.CategoryFragment;
import com.example.studywithai.Fragments.HomeFragment;
import com.example.studywithai.Fragments.QuestionFragment;
import com.example.studywithai.Fragments.RoadmapFragment;
import com.example.studywithai.Fragments.SettingsFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0){
            return new HomeFragment();
        } else if (position == 1) {
            return new RoadmapFragment();
        } else if (position == 2) {
            return new CategoryFragment();
        } else if (position == 3) {
            return  new QuestionFragment();
        }else if (position == 4){
            return new SettingsFragment();
        }
        return new HomeFragment();
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
