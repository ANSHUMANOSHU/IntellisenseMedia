package com.media.intellisensemedia.dbhelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.exoplayer2.C;
import com.media.intellisensemedia.entitiy.Playlist;
import com.media.intellisensemedia.entitiy.Video;
import com.media.intellisensemedia.utils.VideoFetcher;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class PlayListHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PlayListStorage";
    private static final String TABLE_NAME = "master";
    private static final String COLUMN_1 = "dname";
    private static final String COLUMN_2 = "tname";
    private static final String T_COLUMN = "data";
    private static final int VERSION = 1;
    private Context context;

    public PlayListHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create Table " + TABLE_NAME + " ( " + COLUMN_1 + " text, " + COLUMN_2 + " text PRIMARY KEY);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if exists " + TABLE_NAME);
        onCreate(db);
    }

    public void addPlaylist(Playlist playlist) {
        SQLiteDatabase database = getWritableDatabase();
        String SQL = "create table " + playlist.tname + " ( " + T_COLUMN + " text PRIMARY KEY);";
        database.execSQL(SQL);
        ContentValues values = new ContentValues();
        values.put(COLUMN_1, playlist.dname);
        values.put(COLUMN_2, playlist.tname);
        database.insert(TABLE_NAME, null, values);

        database.close();
    }

    public ArrayList<Playlist> fetchAllPlaylist() {
        ArrayList<Playlist> list = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        try {
            Cursor cursor = database.rawQuery("Select * from " + TABLE_NAME, null);
            if (cursor != null) {
                Playlist playlist;
                while (cursor.moveToNext()) {
                    playlist = new Playlist();
                    playlist.dname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_1));
                    playlist.tname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_2));
                    list.add(playlist);
                }
                cursor.close();
                return list;
            }

        } catch (Exception e) {
        } finally {
            database.close();
        }
        return list;
    }

    public boolean insertVideo(Playlist playlist, Video video) {
        if (!exists(playlist, video)) {
            SQLiteDatabase database = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(T_COLUMN, video.DATA);
            database.insert(playlist.tname, null, contentValues);
            database.close();
            return true;
        }
        return false;
    }

    public boolean exists(Playlist playlist, Video video) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("Select * from " + playlist.tname + " where " + T_COLUMN
                + " = \"" + video.DATA + "\"", null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                database.close();
                return true;
            }
        }
        database.close();
        return false;
    }

    public Playlist fetchAll(Playlist playlist) {
        ArrayList<Video> videos = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        try {
            Cursor cursor = database.rawQuery("Select * from " + playlist.tname, null);
            if (cursor != null) {
                Video video = null;
                String data = "";
                while (cursor.moveToNext()) {
                    data = cursor.getString(cursor.getColumnIndex(T_COLUMN));
                    video = VideoFetcher.getVideo(context, data);
                    if (video != null) {
                        videos.add(video);
                    } else {
                        delete(playlist, video);
                    }
                }
                cursor.close();
                playlist.videos = videos;
            }
        } catch (Exception ignored) {
        } finally {
            database.close();
        }
        return playlist;
    }

    public void delete(Playlist playlist) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            //  R E M O V E   E N T R Y    F R O M    M AS T E R    T A B L E
            database.delete(TABLE_NAME, COLUMN_2 + " = ?", new String[]{playlist.tname});

            //  R E M O V E   T A B L E
            database.execSQL("Drop table if exists " + playlist.tname);

        } catch (Exception e) {
        } finally {
            database.close();
        }
    }

    public void delete(Playlist playlist, Video video) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(playlist.tname, T_COLUMN + " = ?", new String[]{video.DATA});
        database.close();
    }

}
