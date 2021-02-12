package com.media.intellisensemedia.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.media.intellisensemedia.entitiy.Video;
import com.media.intellisensemedia.interfaces.OnFetchedListener;

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
                    video.LENGTH = Utilities.getFormatedDuration(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));
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

    public static void fetchOnlineVideos(final OnFetchedListener onFetchedListener) {
        TagsHelper tagsHelper = new TagsHelper();
        final ArrayList<Video> videos = new ArrayList<>();
        final ArrayList<String> tags = tagsHelper.getTagsFromDB();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final int[] count = {0};
        for(String tag : tags){
            tag = Utilities.getFormattedTag(tag);
            Log.d("Hardik", "fetchOnlineVideos: "+tag);
            reference.child(tag).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds : snapshot.getChildren()){
                      if(ds.getKey()!=null && ds.getKey().startsWith("@")) videos.add(ds.getValue(Video.class));
                    }
                    count[0] += 1;
                    if(tags.size() == count[0]){
                        onFetchedListener.fetched(videos);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    onFetchedListener.fetched(new ArrayList<Video>());
                }
            });
        }
    }
}
