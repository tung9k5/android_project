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
import com.example.studywithai.Models.CategoryModel;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CategoryRepository extends SqliteDbHelper {
    public CategoryRepository(@Nullable Context context) {
        super(context);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getCurrentDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime zone = ZonedDateTime.now();
        return dtf.format(zone);
    }
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public long saveNewCategory(String nameCate, String description){
//        String currentDate = getCurrentDate();
//        ContentValues values = new ContentValues();
//        values.put(CATEGORY_NAME, nameCate);
//        values.put(CATEGORY_DESC, description);
//        values.put(CREATED_AT, currentDate);
//        SQLiteDatabase db = this.getWritableDatabase();
//        long insert = db.insert(TABLE_CATEGORY, null, values);
//        db.close();
//        return insert;
//    }

//    @SuppressLint("Range")
//    public ArrayList<CategoryModel> getListCategories(){
//        ArrayList<CategoryModel> categories = new ArrayList<>();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_CATEGORY + " ORDER BY " + CREATED_AT + " DESC ", null);
//        if (data.getCount() > 0){
//            if (data.moveToFirst()){
//                do {
//                    categories.add(
//                            new CategoryModel(
//                                    data.getInt(data.getColumnIndex(CATEGORY_ID)),
//                                    data.getString(data.getColumnIndex(CATEGORY_NAME)),
//                                    data.getString(data.getColumnIndex(CATEGORY_DESC)),
//                                    data.getString(data.getColumnIndex(CREATED_AT)),
//                                    data.getString(data.getColumnIndex(UPDATED_AT))
//                            )
//                    );
//                } while (data.moveToNext());
//            }
//        }
//        data.close();
//        db.close();
//        return categories;
//    }
}
