package com.media.intellisensemedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.media.intellisensemedia.adapters.OfflineVideosAdapter;
import com.media.intellisensemedia.dbhelpers.PlayListHelper;
import com.media.intellisensemedia.entitiy.Playlist;
import com.media.intellisensemedia.entitiy.Video;

import java.util.ArrayList;

public class PlaylistVideosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OfflineVideosAdapter adapter;
    private TextView sorryText;
    private static final String ACTIVITY = "VIDEOSPLAYLIST";
    public static final String  PLAYLIST = "PLAYLIST";
    private PlayListHelper playListHelper;

    private ArrayList<Video> videos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_videos);

        //    I N I T    V I E W S
        recyclerView = findViewById(R.id.playlistVideosRecycler);
        sorryText = findViewById(R.id.sorryTextPlaylistVideos);

        //   S E T    A D A P T E R
        adapter = new OfflineVideosAdapter(this,ACTIVITY);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //   F E T C H   A L L   V I D E O S    F R O M    P L A Y L I S T
        playListHelper = new PlayListHelper(this);
        Playlist playlist = (Playlist) getIntent().getSerializableExtra(PLAYLIST);
        playlist = playListHelper.fetchAll(playlist);
        if (playlist.videos.isEmpty())
            sorryText.setVisibility(View.VISIBLE);
        else
            adapter.setVideos(playlist.videos);
        
    }
}
