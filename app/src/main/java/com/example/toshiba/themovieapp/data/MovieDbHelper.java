package com.example.toshiba.themovieapp.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.toshiba.themovieapp.data.MovieContract.MovieData;


public class MovieDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieData.TABLE_NAME + " (" +
                MovieData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieData.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieData.COLUMN_POSTER + " BLOB NOT NULL, " +
                MovieData.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieData.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieData.COLUMN_RATING + " INTEGER NOT NULL" +
                "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieData.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
