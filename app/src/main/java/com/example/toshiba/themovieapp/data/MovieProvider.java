package com.example.toshiba.themovieapp.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class MovieProvider extends ContentProvider {

    private static final String TAG = MovieProvider.class.getSimpleName();
    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_WITH_ID = 101;
    private MovieDbHelper movieDbHelper;
    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MovieContract.PATH_MOVIE, CODE_MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*", CODE_MOVIE_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] columns, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String orderBy) {
        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
            case CODE_MOVIE:
                cursor = movieDbHelper.getReadableDatabase().query(
                        MovieContract.MovieData.TABLE_NAME,
                        columns,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        orderBy);
                break;
            case CODE_MOVIE_WITH_ID:
                Log.d(TAG, "Uri last path segment: " + uri.getLastPathSegment());
                selectionArgs = new String[]{uri.getLastPathSegment()};
                cursor = movieDbHelper.getReadableDatabase().query(
                        MovieContract.MovieData.TABLE_NAME,
                        columns,
                        MovieContract.MovieData.COLUMN_ID + " = ? ",
                        selectionArgs,
                        null,
                        null,
                        orderBy);
                Log.d(TAG, "Query CODE_MOVIE_WITH_ID cursor : " + cursor.getCount());
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int rowsInserted = 0;

        switch (uriMatcher.match(uri)) {
            case CODE_MOVIE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieData.TABLE_NAME, null, value);
                        if (_id != -1) rowsInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0)
                    getContext().getContentResolver().notifyChange(uri, null);
                break;
        default:
            return super.bulkInsert(uri, values);
        }

        return rowsInserted;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        Uri returnUri = null;
        switch (uriMatcher.match(uri)) {
            case CODE_MOVIE:
                long id = db.insert(MovieContract.MovieData.TABLE_NAME, null, contentValues);
                if (id > 0)
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieData.CONTENT_URI, id);
                break;
            default:
                throw new UnsupportedOperationException("Problem with uri, please check");
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int rowDeleted = 0;

        switch (uriMatcher.match(uri)) {
            case CODE_MOVIE_WITH_ID:
                String id = uri.getLastPathSegment();
                Log.d(TAG, "ID in delete: " + id);
                selectionArgs = new String[]{id};
                rowDeleted = db.delete(
                                MovieContract.MovieData.TABLE_NAME,
                                MovieContract.MovieData.COLUMN_ID + " = ? ",
                                selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
