package com.example.pawe.firstdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Paweł on 2016-11-27.
 */

public class DBDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DB.db";
    private static final String INT_TYPE = " INT ";
    private static final String TEXT_TYPE = " TEXT ";
    private static final String DATE_TYPE = " TEXT ";
    private static final String COMMA_SEP = ", ";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBContract.DB.TABLE_PERSONS + " ( " +
                    DBContract.DB._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DBContract.DB.COLUMN_FIRSTNAME + TEXT_TYPE + COMMA_SEP +
                    DBContract.DB.COLUMN_DATE + DATE_TYPE + COMMA_SEP +
                    DBContract.DB.COLUMN_IMAGE_PATH + TEXT_TYPE + COMMA_SEP+
                    DBContract.DB.COLUMN_LASTNAME + TEXT_TYPE + " );";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.DB.TABLE_PERSONS;

    public DBDbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /* nie rob nic */
    }
}
