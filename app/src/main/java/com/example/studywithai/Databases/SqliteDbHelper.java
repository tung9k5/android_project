package com.example.studywithai.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import com.example.studywithai.Models.RoadmapModel;
import com.example.studywithai.Models.TaskModel;

import java.util.ArrayList;
import java.util.List;

public class SqliteDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "study_ai";
    private static final int DB_VERSION = 10;

    // 1.BẢNG USERS
    protected static final String TABLE_USER = "users";
    protected static final String USER_ID = "id";
    protected static final String USER_USERNAME = "username";
    protected static final String USER_PASSWORD = "password";
    protected static final String USER_EMAIL = "email";
    protected static final String USER_PHONE = "phone";
    protected static final String USER_ROLE = "role";
    protected static final String USER_GRADE_LEVEL = "grade_level";
    protected static final String USER_XP = "xp";
    protected static final String USER_LEVEL = "level";
    protected static final String USER_ENERGY = "energy";

    // 2. BẢNG CHAT AI SESSION
    protected static final String TABLE_CHAT_SESSION = "chat_sessions";
    protected static final String SESSION_ID = "id";
    protected static final String SESSION_USER_ID = "user_id";
    protected static final String SESSION_TITLE = "title";
    protected static final String SESSION_SUBJECT = "subject";

    // 3. BẢNG CHAT MESSAGES AI
    protected static final String TABLE_CHAT_MESSAGE = "chat_messages";
    protected static final String MSG_ID = "id";
    protected static final String MSG_SESSION_ID = "session_id";
    protected static final String MSG_SENDER = "sender";
    protected static final String MSG_TYPE = "message_type";
    protected static final String MSG_CONTENT = "content";

    // 4. BẢNG QUIZZES
    protected static final String TABLE_QUIZ = "quizzes";
    protected static final String QUIZ_ID = "id";
    protected static final String QUIZ_USER_ID = "user_id";
    protected static final String QUIZ_TITLE = "title";
    protected static final String QUIZ_ENERGY_COST = "energy_cost";
    protected static final String QUIZ_XP_REWARD = "xp_reward";

    // 5. BẢNG QUESTIONS
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

        String CREATE_TABLE_ROADMAP = "CREATE TABLE roadmaps (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "subject_name TEXT, " +
                "goal TEXT, " +
                "current_level TEXT, " +
                "time_commitment TEXT, " +
                "duration TEXT, " +
                "mentor_tone TEXT, " +
                "status TEXT, " +
                "progress INTEGER, " +
                "is_pinned INTEGER DEFAULT 0)";
        db.execSQL(CREATE_TABLE_ROADMAP);

        String CREATE_TABLE_DAILY_TASKS = "CREATE TABLE daily_tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date TEXT, " +
                "name TEXT, " +
                "xp INTEGER, " +
                "target_tab INTEGER, " +
                "is_completed INTEGER DEFAULT 0)";
        db.execSQL(CREATE_TABLE_DAILY_TASKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_SESSION);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_MESSAGE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);
            db.execSQL("DROP TABLE IF EXISTS roadmaps");
            db.execSQL("DROP TABLE IF EXISTS daily_tasks");
            onCreate(db);
        }
    }

    // ================= CÁC HÀM XỬ LÝ LỘ TRÌNH (ROADMAP) =================

    public long insertRoadmap(int userId, String subjectName, String goal, String currentLevel, String timeCommitment, String mentorTone, String status, int progress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("subject_name", subjectName);
        values.put("goal", goal);
        values.put("current_level", currentLevel);
        values.put("time_commitment", timeCommitment);
        values.put("duration", "");
        values.put("mentor_tone", mentorTone);
        values.put("status", status);
        values.put("progress", progress);
        long id = db.insert("roadmaps", null, values);
        db.close();
        return id;
    }

    public List<RoadmapModel> getRoadmapsByUserId(int userId) {
        List<RoadmapModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM roadmaps WHERE user_id = ? ORDER BY is_pinned DESC, id DESC", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                list.add(new RoadmapModel(
                        cursor.getInt(0),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(8),
                        cursor.getInt(9),
                        cursor.getInt(10) == 1
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void updatePinStatus(int roadmapId, boolean isPinned) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_pinned", isPinned ? 1 : 0);
        db.update("roadmaps", values, "id = ?", new String[]{String.valueOf(roadmapId)});
        db.close();
    }

    public void updateRoadmapStatus(int roadmapId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", newStatus);
        db.update("roadmaps", values, "id = ?", new String[]{String.valueOf(roadmapId)});
        db.close();
    }

    public int countAllRoadmaps(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM roadmaps WHERE user_id = ?", new String[]{String.valueOf(userId)});
        int count = 0;
        if(cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public void deleteRoadmap(int roadmapId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("roadmaps", "id = ?", new String[]{String.valueOf(roadmapId)});
        db.close();
    }

    public RoadmapModel getHighlightedRoadmap(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        RoadmapModel roadmap = null;
        Cursor cursor = db.rawQuery("SELECT * FROM roadmaps WHERE user_id = ? AND status = 'ACTIVE' ORDER BY is_pinned DESC, id DESC LIMIT 1", new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            roadmap = new RoadmapModel(
                    cursor.getInt(0),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(8),
                    cursor.getInt(9),
                    cursor.getInt(10) == 1
            );
        }
        cursor.close();
        db.close();
        return roadmap;
    }

    // ================= CÁC HÀM XỬ LÝ CHAT =================
    public long insertChatMessage(int sessionId, String sender, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MSG_SESSION_ID, sessionId);
        values.put(MSG_SENDER, sender);
        values.put(MSG_CONTENT, content);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
        values.put(CREATED_AT, sdf.format(new java.util.Date()));

        long id = db.insert(TABLE_CHAT_MESSAGE, null, values);
        db.close();
        return id;
    }

    public List<com.example.studywithai.Models.ChatMessage> getChatHistory(int sessionId) {
        List<com.example.studywithai.Models.ChatMessage> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CHAT_MESSAGE + " WHERE " + MSG_SESSION_ID + " = ? ORDER BY id ASC", new String[]{String.valueOf(sessionId)});

        if (cursor.moveToFirst()) {
            do {
                String sender = cursor.getString(2);
                String textContent = cursor.getString(4);
                boolean isUser = (sender != null && sender.equals("user"));
                com.example.studywithai.Models.ChatMessage msg = new com.example.studywithai.Models.ChatMessage(textContent, isUser);
                list.add(msg);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // ================= CÁC HÀM XỬ LÝ NHIỆM VỤ (DAILY TASKS) =================
    public void insertDailyTask(String date, String name, int xp, int targetTab) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("name", name);
        values.put("xp", xp);
        values.put("target_tab", targetTab);
        db.insert("daily_tasks", null, values);
        db.close();
    }

    public List<TaskModel> getTasksByDate(String date) {
        List<TaskModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM daily_tasks WHERE date = ?", new String[]{date});
        if (cursor.moveToFirst()) {
            do {
                boolean isCompleted = cursor.getInt(5) == 1;
                list.add(new TaskModel(
                        cursor.getString(2),
                        cursor.getInt(3),
                        isCompleted,
                        cursor.getInt(4)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public void clearOldTasks(String todayDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("daily_tasks", "date != ?", new String[]{todayDate});
        db.close();
    }

    // 1. Tạo một phiên chat mới (Trả về ID của phiên chat đó)
    public int createNewChatSession(int userId, String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();
        values.put(SESSION_USER_ID, userId);
        values.put(SESSION_TITLE, title); // Ví dụ: "Giải toán Đại số", "Hỏi về OOP"...

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
        values.put(CREATED_AT, sdf.format(new java.util.Date()));

        long id = db.insert(TABLE_CHAT_SESSION, null, values);
        db.close();
        return (int) id;
    }

    // 2. Lấy danh sách toàn bộ phiên chat của User để hiển thị ra lịch sử
    public java.util.List<com.example.studywithai.Models.ChatSessionModel> getAllSessions(int userId) {
        java.util.List<com.example.studywithai.Models.ChatSessionModel> list = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Lấy danh sách, cuộc hội thoại mới nhất xếp trên cùng
        android.database.Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CHAT_SESSION + " WHERE " + SESSION_USER_ID + " = ? ORDER BY id DESC", new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                // Bạn cần tạo 1 class ChatSessionModel gồm: id, title, created_at
                list.add(new com.example.studywithai.Models.ChatSessionModel(
                        cursor.getInt(0),   // id
                        cursor.getString(2), // title
                        cursor.getString(4)  // created_at (Ngày tạo)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
}