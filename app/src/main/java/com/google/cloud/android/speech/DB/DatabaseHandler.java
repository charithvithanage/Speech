package com.google.cloud.android.speech.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "BANK_DATABASE";

    private static final String TABLE_QUESTION = "table_questions";

    private static final String KEY_ID = "id";
    private static final String KEY_QUESTION = "question";
    private static final String KEY_ANSWER = "answer";
    private static final String KEY_SOURCE = "source";

    private static final String CREATE_TABLE_QUESTIONS = "CREATE TABLE " + TABLE_QUESTION + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_QUESTION + " TEXT," + KEY_ANSWER + " TEXT," + KEY_SOURCE + " TEXT)";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_QUESTIONS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);
        onCreate(db);
    }

    public String getAnswer(String question) {
        SQLiteDatabase db = this.getWritableDatabase();
        String answer = null;
        Cursor cursor = db.query(TABLE_QUESTION, new String[]{KEY_ANSWER}, KEY_ANSWER + "=?", new String[]{question}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    answer = cursor.getString(cursor.getColumnIndex(KEY_ANSWER));
                } while (cursor.moveToNext());
            }
        }
        return answer;
    }
}
