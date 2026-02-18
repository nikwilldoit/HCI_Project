package com.example.mega;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class dbConnect extends SQLiteOpenHelper {

    //database info
    private static final String DB_NAME = "HCIDB";
    private static final int DB_VERSION = 1;

    //table name
    public static final String TABLE_USERS = "users";

    //column names
    public static final String COL_ID = "id";
    public static final String COL_FULLNAME = "fullname";
    public static final String COL_EMAIL = "emailAddress";
    public static final String COL_PASSWORD = "password";
    public static final String COL_DATE_OF_BIRTH = "date_of_birth";
    public static final String COL_PHONE_NUMBER = "phone_number";
    public static final String COL_BIO = "bio";

    public dbConnect(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE " + TABLE_USERS + " (" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_FULLNAME + " TEXT, " +
                        COL_EMAIL + " TEXT, " +
                        COL_PASSWORD + " TEXT, " +
                        COL_DATE_OF_BIRTH + " TEXT, " +
                        COL_PHONE_NUMBER + " TEXT, " +
                        COL_BIO + " TEXT" +
                        ");";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public void insertUser(Users user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_FULLNAME, user.getFullname());
        values.put(COL_EMAIL, user.getEmailAddress());
        values.put(COL_PASSWORD, user.getPassword());
        values.put(COL_DATE_OF_BIRTH, user.getDate_of_birth());
        values.put(COL_PHONE_NUMBER, user.getPhone_number());
        values.put(COL_BIO, user.getBio());

        db.insert(TABLE_USERS, null, values);
        db.close();
    }

}
