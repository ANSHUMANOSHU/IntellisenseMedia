package com.media.intellisensemedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.media.intellisensemedia.adapters.PlaylistAdapter;
import com.media.intellisensemedia.dbhelpers.PlayListHelper;
import com.media.intellisensemedia.entitiy.Playlist;
import java.util.ArrayList;

public class PlayListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView sorryText;
    private PlaylistAdapter adapter;
    private ArrayList<Playlist> playlists;
    private PlayListHelper playListHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        //     I N I T   V I E W S
        recyclerView = findViewById(R.id.playlistRecycler);
        sorryText = findViewById(R.id.sorryTextPlaylist);

        //     S E T     A D A P T E R
        adapter = new PlaylistAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //    F E T C H    P L A Y L I S T S
        playListHelper = new PlayListHelper(this);

        playlists = playListHelper.fetchAllPlaylist();

        //    S E T   L I S T   T O   R E C Y C L E R    A D A P T E R   I F    N O T    E M P T Y
        if (playlists != null)
            adapter.setPlaylists(playlists);
        else
            sorryText.setVisibility(View.VISIBLE);
    }
}
