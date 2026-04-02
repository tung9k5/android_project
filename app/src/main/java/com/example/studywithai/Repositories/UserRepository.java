package com.example.studywithai.Repositories;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.studywithai.Databases.SqliteDbHelper;
import com.example.studywithai.Models.UserModel;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class UserRepository extends SqliteDbHelper {
    public UserRepository(@Nullable Context context) {
        super(context);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getCurrentDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime zone = ZonedDateTime.now();
        return dtf.format(zone);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public long saveUserAccount(String username, String password, String email, String phone){
        String currentDate = getCurrentDate();
        ContentValues values = new ContentValues();
        values.put(USER_USERNAME, username);
        values.put(USER_PASSWORD, password);
        values.put(USER_EMAIL, email);
        values.put(USER_PHONE, phone);
        values.put(USER_ROLE, 1);
        values.put(CREATED_AT, currentDate);
        SQLiteDatabase db = this.getWritableDatabase();
        long insert = db.insert(TABLE_USER, null, values);
        db.close();
        return insert;
    }
    public boolean checkExistsUsername(String username){
        boolean checking = false;
        // kiem tra du lieu trong bang users
        // select id, username from users where username = ?
        String[] cols = { USER_ID, USER_USERNAME };
        String condition = USER_USERNAME + " =? ";
        String[] params = { username };
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, cols, condition, params, null, null, null);
        if (cursor.getCount() > 0){
            checking = true;
            // da ton tai username can dang ky
        }
        cursor.close();
        db.close();
        return checking;
    }
    @SuppressLint("Range")
    public UserModel loginUser(String username, String password){
        // xu ly dang nhap vao he thong
        UserModel user = new UserModel();
        String[] cols = { USER_ID, USER_USERNAME, USER_EMAIL, USER_ROLE };
        String condition = USER_USERNAME + " =? AND " + USER_PASSWORD + " =? ";
        String[] params = { username, password };
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.query(TABLE_USER, cols, condition, params, null, null, null);
        if (data.getCount() > 0){
            data.moveToFirst();
            user.setId(data.getInt(data.getColumnIndex(USER_ID)));
            user.setUsername(data.getString(data.getColumnIndex(USER_USERNAME)));
            user.setEmail(data.getString(data.getColumnIndex(USER_EMAIL)));
            user.setRole(data.getInt(data.getColumnIndex(USER_ROLE)));
        }
        data.close();
        db.close();
        return user;
    }
}
