package com.media.intellisensemedia.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.media.intellisensemedia.entitiy.Video;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class VideoFetcher {

    @SuppressLint("InlinedApi")
    public static ArrayList<Video> fetchAllVideos(Context context) {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        HashSet<Video> videos = new HashSet<>();
        String[] proj = {MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME
                , MediaStore.Video.Media.DURATION, MediaStore.Video.Media.MINI_THUMB_MAGIC};

        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Video video = new Video();
                    video.DATA = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    video.DISPLAYNAME = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                    video.LENGTH = getFormatedDuration(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));
                    videos.add(video);
                }
                return new ArrayList<>(videos);
            }
        } catch (Exception e) {
        } finally {
            cursor.close();
        }

        return new ArrayList<>();
    }

    private static String getFormatedDuration(String string) {
        long length = Long.parseLong(string);
        int temp = (int) (length / 1000);
        int minutes = temp / 60;
        int seconds = temp % 60;
        return String.format("%02d : %02d", minutes, seconds);
    }

    @SuppressLint("InlinedApi")
    public static Video getVideo(Context context, String Data) {
        ArrayList<Video> arrayList = fetchAllVideos(context);
        for(Video video : arrayList){
            if(video.DATA.toLowerCase().equals(Data.toLowerCase())){
                return video;
            }
        }
        return null;
    }

}
