package com.media.intellisensemedia.dbhelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.media.intellisensemedia.entitiy.Video;
import com.media.intellisensemedia.utils.VideoFetcher;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class FavouriteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MediaStorage";
    private static final String TABLE_NAME = "favourites";
    private static final String COLUMN_1 = "data";
    private static final int VERSION = 1;
    private Context context;

    public FavouriteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create Table " + TABLE_NAME + " ( " + COLUMN_1 + " text PRIMARY KEY);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if exists " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insert(Video video) {
        if(!exists(video.DATA)) {
            SQLiteDatabase database = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_1, video.DATA);
            database.insert(TABLE_NAME, null, values);
            database.close();
            return true;
        }
        return false;
    }

    private boolean exists(String data) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM "+TABLE_NAME + " WHERE "+COLUMN_1+" = \""+data+"\"",null);
        if(cursor!=null){
            if(cursor.moveToNext()) {
                cursor.close();
                return true;
            }else
                cursor.close();
        }
        return false;
    }

    public ArrayList<Video> fetchAll() {
        ArrayList<Video> videos = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        try {
            Cursor cursor = database.rawQuery("Select * from " + TABLE_NAME, null);
            if (cursor != null) {
                Video video = null;
                String data = "";
                while (cursor.moveToNext()) {
                    data = cursor.getString(cursor.getColumnIndex(COLUMN_1));
                    video = VideoFetcher.getVideo(context, data);
                    if (video != null) {
                        videos.add(video);
                    }
                    else{
                        delete(data);
                    }
                }
                cursor.close();
            }
        } catch (Exception ignored) {
        } finally {
            database.close();
        }
        return videos;
    }

    public void delete(String DATA) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(TABLE_NAME, COLUMN_1 + " = ?", new String[]{DATA});
        database.close();
    }

}
