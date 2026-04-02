package com.example.studywithai.Databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class SqliteDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "study_ai";
    private static final int DB_VERSION = 5;

    //  1.BẢNG USERS
    protected static final String TABLE_USER = "users";
    protected static final String USER_ID = "id";
    protected static final String USER_USERNAME = "username";
    protected static final String USER_PASSWORD = "password";
    protected static final String USER_EMAIL = "email";
    protected static final String USER_PHONE = "phone";
    protected static final String USER_ROLE = "role";
    protected static final String USER_GRADE_LEVEL = "grade_level"; // 1: Cấp 2, 2: Cấp 3, 3: ĐH trở lên
    protected static final String USER_XP = "xp";
    protected static final String USER_LEVEL = "level";
    protected static final String USER_ENERGY = "energy";

    //2. BẢNG CHAT AI SESSION
    protected static final String TABLE_CHAT_SESSION = "chat_sessions";
    protected static final String SESSION_ID = "id";
    protected static final String SESSION_USER_ID = "user_id";
    protected static final String SESSION_TITLE = "title";
    protected static final String SESSION_SUBJECT = "subject";

    //3. BẢNG CHAT MESSAGES AI
    protected static final String TABLE_CHAT_MESSAGE = "chat_messages";
    protected static final String MSG_ID = "id";
    protected static final String MSG_SESSION_ID = "session_id";
    protected static final String MSG_SENDER = "sender";
    protected static final String MSG_TYPE = "message_type";
    protected static final String MSG_CONTENT = "content";

    //4. BẢNG QUIZZES
    protected static final String TABLE_QUIZ = "quizzes";
    protected static final String QUIZ_ID = "id";
    protected static final String QUIZ_USER_ID = "user_id";
    protected static final String QUIZ_TITLE = "title";
    protected static final String QUIZ_ENERGY_COST = "energy_cost";
    protected static final String QUIZ_XP_REWARD = "xp_reward";

    //5. BẢNG QUESTIONS
    protected static final String TABLE_QUESTION = "questions";
    protected static final String QUESTION_ID = "id";
    protected static final String QUESTION_QUIZ_ID = "quiz_id";
    protected static final String QUESTION_TEXT = "question_text";
    protected static final String OPTION_A = "option_a";
    protected static final String OPTION_B = "option_b";
    protected static final String OPTION_C = "option_c";
    protected static final String OPTION_D = "option_d";
    protected static final String CORRECT_ANSWER = "correct_answer";
    protected static final String AI_EXPLANATION = "ai_explanation";


    protected static final String CREATED_AT = "created_at";
    protected static final String UPDATED_AT = "updated_at";

    public SqliteDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String userTable = "CREATE TABLE " + TABLE_USER + " ("
                + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USER_USERNAME + " VARCHAR(30) NOT NULL, "
                + USER_PASSWORD + " VARCHAR(200) NOT NULL, "
                + USER_EMAIL + " VARCHAR(60) NOT NULL, "
                + USER_PHONE + " VARCHAR(20), "
                + USER_ROLE + " TINYINT DEFAULT(1), "
                + USER_GRADE_LEVEL + " INTEGER DEFAULT(1), "
                + USER_XP + " INTEGER DEFAULT(0), "
                + USER_LEVEL + " INTEGER DEFAULT(1), "
                + USER_ENERGY + " INTEGER DEFAULT(100), "
                + CREATED_AT + " DATETIME, "
                + UPDATED_AT + " DATETIME)";
        db.execSQL(userTable);

        String sessionTable = "CREATE TABLE " + TABLE_CHAT_SESSION + " ("
                + SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SESSION_USER_ID + " INTEGER, "
                + SESSION_TITLE + " VARCHAR(100), "
                + SESSION_SUBJECT + " VARCHAR(50), "
                + CREATED_AT + " DATETIME, "
                + UPDATED_AT + " DATETIME)";
        db.execSQL(sessionTable);

        String messageTable = "CREATE TABLE " + TABLE_CHAT_MESSAGE + " ("
                + MSG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MSG_SESSION_ID + " INTEGER, "
                + MSG_SENDER + " VARCHAR(10), "
                + MSG_TYPE + " VARCHAR(20) DEFAULT('text'), "
                + MSG_CONTENT + " TEXT, "
                + CREATED_AT + " DATETIME)";
        db.execSQL(messageTable);

        String quizTable = "CREATE TABLE " + TABLE_QUIZ + " ("
                + QUIZ_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + QUIZ_USER_ID + " INTEGER, "
                + QUIZ_TITLE + " VARCHAR(150), "
                + QUIZ_ENERGY_COST + " INTEGER DEFAULT(10), "
                + QUIZ_XP_REWARD + " INTEGER DEFAULT(50), "
                + CREATED_AT + " DATETIME)";
        db.execSQL(quizTable);

        String questionTable = "CREATE TABLE " + TABLE_QUESTION + " ("
                + QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + QUESTION_QUIZ_ID + " INTEGER, "
                + QUESTION_TEXT + " TEXT, "
                + OPTION_A + " VARCHAR(255), "
                + OPTION_B + " VARCHAR(255), "
                + OPTION_C + " VARCHAR(255), "
                + OPTION_D + " VARCHAR(255), "
                + CORRECT_ANSWER + " VARCHAR(1), "
                + AI_EXPLANATION + " TEXT)";
        db.execSQL(questionTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_SESSION);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_MESSAGE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);
            db.execSQL("DROP TABLE IF EXISTS categories");
            onCreate(db);
        }
    }
}