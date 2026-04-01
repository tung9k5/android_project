package com.example.studywithai.Databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SqliteDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "study_ai";
    private static final int DB_VERSION = 2;

    // dinh nghia bang du lieu luu tru thong tin dang nhap
    protected static final String TABLE_USER = "users"; // ten bang DL
    // khai bao cac cot DL nam trong bang
    protected static final String USER_ID = "id";
    protected static final String USER_USERNAME = "username";
    protected static final String USER_PASSWORD = "password";
    protected static final String USER_EMAIL = "email";
    protected static final String USER_PHONE = "phone";
    protected static final String USER_ROLE = "role";

    // dinh nghia luu tru thong tin Categories
    protected static final String TABLE_CATEGORY = "categories";
    protected static final String CATEGORY_ID = "id";
    protected static final String CATEGORY_NAME = "name";
    protected static final String CATEGORY_DESC = "description";


    protected static final String CREATED_AT = "created_at";
    protected static final String UPDATED_AT = "updated_at";


    public SqliteDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // tao bang user
        String userTable = " CREATE TABLE " + TABLE_USER + " ( "
                         + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                         + USER_USERNAME + " VARCHAR(30) NOT NULL, "
                         + USER_PASSWORD + " VARCHAR(200) NOT NULL, "
                         + USER_EMAIL + " VARCHAR(60) NOT NULL, "
                         + USER_PHONE + " VARCHAR(20), "
                         + USER_ROLE + " TINYINT DEFAULT(1), "
                         + CREATED_AT + " DATETIME, "
                         + UPDATED_AT + " DATETIME ) ";
        db.execSQL(userTable);

        String categoryTable = " CREATE TABLE " + TABLE_CATEGORY + " ( "
                               + CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                               + CATEGORY_NAME + " VARCHAR(100) NOT NULL, "
                               + CATEGORY_DESC + " VARCHAR(255), "
                               + CREATED_AT + " DATETIME, "
                               + UPDATED_AT + " DATETIME ) ";
        db.execSQL(categoryTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
            onCreate(db);
        }
    }
}
