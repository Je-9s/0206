package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "PasswordManager.db";
    public static final int DATABASE_VERSION = 1;

    // Таблица пользователей
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    // Таблица паролей
    public static final String TABLE_PASSWORDS = "passwords";
    public static final String COLUMN_PASSWORD_ID = "password_id";
    public static final String COLUMN_SERVICE = "service";
    public static final String COLUMN_LOGIN = "login";
    public static final String COLUMN_PASSWORD_VALUE = "password_value";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_USER_ID_FK = "user_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создание таблицы пользователей
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Создание таблицы паролей
        String CREATE_PASSWORDS_TABLE = "CREATE TABLE " + TABLE_PASSWORDS + "("
                + COLUMN_PASSWORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SERVICE + " TEXT,"
                + COLUMN_LOGIN + " TEXT,"
                + COLUMN_PASSWORD_VALUE + " TEXT,"
                + COLUMN_NOTES + " TEXT,"
                + COLUMN_USER_ID_FK + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")" + ")";
        db.execSQL(CREATE_PASSWORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PASSWORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Методы для работы с пользователями
    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USERNAME + " = ?" + " AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // Методы для работы с паролями
    public boolean addPassword(int userId, String service, String login, String password, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SERVICE, service);
        values.put(COLUMN_LOGIN, login);
        values.put(COLUMN_PASSWORD_VALUE, password);
        values.put(COLUMN_NOTES, notes);
        values.put(COLUMN_USER_ID_FK, userId);

        long result = db.insert(TABLE_PASSWORDS, null, values);
        return result != -1;
    }

    public Cursor getPasswords(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_PASSWORDS,
                new String[]{COLUMN_PASSWORD_ID, COLUMN_SERVICE, COLUMN_LOGIN, COLUMN_PASSWORD_VALUE, COLUMN_NOTES},
                COLUMN_USER_ID_FK + "=?",
                new String[]{String.valueOf(userId)},
                null, null, COLUMN_SERVICE + " ASC");
    }

    public boolean deletePassword(int passwordId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PASSWORDS, COLUMN_PASSWORD_ID + "=?", new String[]{String.valueOf(passwordId)}) > 0;
    }

    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_USERNAME + "=?",
                new String[]{username},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
            cursor.close();
            return id;
        }
        return -1;
    }
}