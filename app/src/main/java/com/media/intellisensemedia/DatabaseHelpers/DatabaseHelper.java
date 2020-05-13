package com.media.intellisensemedia.DatabaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.media.intellisensemedia.EntityClasses.Video;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MediaStorage";
    private static final String TABLE_NAME = "favourites";
    private static final int VERSION = 1;
    public static final String COLUMN_1 = "displayname";
    public static final String COLUMN_2 = "duration";
    public static final String COLUMN_3 = "data";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create Table " + TABLE_NAME + " ( " + COLUMN_1 + " text, " + COLUMN_2 + " text, " + COLUMN_3 + " text );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if exists " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertFavourite(Video video) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_1, video.DISPLAYNAME);
        values.put(COLUMN_2, video.LENGTH);
        values.put(COLUMN_3, video.DATA);

        try {
            database.insert(TABLE_NAME, null, values);

        } catch (Exception e) {
            return false;
        } finally {
            database.close();
        }
        return true;
    }

    public ArrayList<Video> fetchAllFavourites() {
        ArrayList<Video> videos = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        try {
            Cursor cursor = database.rawQuery("Select * from " + TABLE_NAME, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Video video = new Video(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_1))
                            , cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_2))
                            , cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_3)));
                    videos.add(video);
                }
            }
            cursor.close();
        } catch (Exception ignored) {
            return videos;
        } finally {
            database.close();
        }
        return videos;
    }

    public boolean deleteVideofromFavourites(String DATA) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            database.delete(TABLE_NAME, COLUMN_3 + " = ?", new String[]{DATA});
        } catch (Exception e) {
            return false;
        } finally {
            database.close();
        }
        return true;
    }

}
