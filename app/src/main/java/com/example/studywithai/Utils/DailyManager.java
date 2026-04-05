package com.example.studywithai.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DailyManager {

    private SharedPreferences spf;
    private SharedPreferences.Editor editor;

    public DailyManager(Context context) {
        spf = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        editor = spf.edit();
    }

    // Lấy chuỗi ngày hiện tại (VD: "2026-04-05")
    public String getTodayString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Hàm kiểm tra và cập nhật Streak mỗi khi mở app
    public void checkAndUpdateStreak() {
        String today = getTodayString();
        String lastLoginDate = spf.getString("LAST_LOGIN_DATE", "");
        int currentStreak = spf.getInt("USER_STREAK", 0);

        if (lastLoginDate.isEmpty()) {
            // Lần đầu tải app
            editor.putString("LAST_LOGIN_DATE", today);
            editor.putInt("USER_STREAK", 1);
            editor.apply();
            return;
        }

        if (!lastLoginDate.equals(today)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date lastDate = sdf.parse(lastLoginDate);
                Date currentDate = sdf.parse(today);

                // Tính khoảng cách giữa 2 ngày
                long diffInMillis = currentDate.getTime() - lastDate.getTime();
                long diffInDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

                if (diffInDays == 1) {
                    // Đăng nhập liên tiếp -> Tăng Streak
                    currentStreak++;
                } else if (diffInDays > 1) {
                    // Mất chuỗi -> Reset về 1
                    currentStreak = 1;
                }

                // Cập nhật ngày mới nhất
                editor.putString("LAST_LOGIN_DATE", today);
                editor.putInt("USER_STREAK", currentStreak);
                editor.apply();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int getCurrentStreak() {
        return spf.getInt("USER_STREAK", 1);
    }

    // Kiểm tra xem hôm nay đã điểm danh chưa
    public boolean isAttendanceClaimedToday() {
        String today = getTodayString();
        String lastClaimed = spf.getString("LAST_CLAIMED_ATTENDANCE", "");
        return today.equals(lastClaimed);
    }

    // Xác nhận điểm danh và cộng năng lượng
    public void claimAttendanceReward(int rewardEnergy) {
        String today = getTodayString();
        int currentEnergy = spf.getInt("ENERGY_USER", 100);

        editor.putString("LAST_CLAIMED_ATTENDANCE", today);
        editor.putInt("ENERGY_USER", currentEnergy + rewardEnergy);
        editor.apply();
    }
}