package com.example.studywithai.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.studywithai.MainActivity;
import com.example.studywithai.R;

public class SettingsFragment extends Fragment {

    private TextView tvProfileName, tvLevelTitle, tvShoptvEnergy;
    private ProgressBar pbXp;
    private Button btnLogout;


    public SettingsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvLevelTitle = view.findViewById(R.id.tvLevelTitle);
        pbXp = view.findViewById(R.id.pbXp);
        btnLogout = view.findViewById(R.id.btnLogout);
        tvShoptvEnergy = view.findViewById(R.id.tvShopEnergy);

        // Lấy thông tin Gamification
        SharedPreferences spf = requireActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        String username = spf.getString("USERNAME_USER", "User");
        int level = spf.getInt("LEVEL_USER", 1);
        int xp = spf.getInt("XP_USER", 0);

        tvProfileName.setText(username);
        tvLevelTitle.setText("Level " + level + " - Apprenticeship");
        pbXp.setProgress(xp % 100);

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = spf.edit();
            editor.clear();
            editor.apply();

            startActivity(new Intent(getActivity(), MainActivity.class));
            requireActivity().finish();
        });
        tvShoptvEnergy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEnergyShop();
            }
        });

    }
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

            // Lưu năng lượng
            android.content.SharedPreferences spf = requireActivity().getSharedPreferences("USER_INFO", android.content.Context.MODE_PRIVATE);
            int currentEnergy = spf.getInt("ENERGY_USER", 100);
            spf.edit().putInt("ENERGY_USER", currentEnergy + addedEnergy).apply();

            android.widget.Toast.makeText(getContext(), "Transaction successful! +" + addedEnergy + "⚡", android.widget.Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        };

        btnBuyPack1.setOnClickListener(buyEvent);
        btnBuyPack2.setOnClickListener(buyEvent);
        btnBuyPack3.setOnClickListener(buyEvent);

        bottomSheetDialog.show();
    }
}