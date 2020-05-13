package com.media.intellisensemedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.media.intellisensemedia.Adapters.OfflineVideosAdapter;
import com.media.intellisensemedia.DatabaseHelpers.DatabaseHelper;
import com.media.intellisensemedia.EntityClasses.Video;

import java.util.ArrayList;

public class FavouriteActivity extends AppCompatActivity {

    private ArrayList<Video> favouriteVideos;
    private RecyclerView recyclerView;
    public TextView sorryText;
    private OfflineVideosAdapter adapter;
    private static final String ACTIVITY = "FAVOURITE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        //  I N I T I A L I Z E    V I E W S
        recyclerView = findViewById(R.id.favouriteVideosRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sorryText = findViewById(R.id.sorryText1);

        //  S E T   A D A P T E R
        adapter = new OfflineVideosAdapter(this,ACTIVITY);
        recyclerView.setAdapter(adapter);

        //  F E T C H   A L L   F A V O U R I T E   V I D E O S   A N D   S E T   T O   R E C Y C L E R   V I E W   L I S T
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        favouriteVideos = databaseHelper.fetchAllFavourites();

        //  C H E C K   I F   F A V O U R I T E S   A R E   E M PT Y
        if (favouriteVideos.isEmpty()) {
            sorryText.setVisibility(View.VISIBLE);
        }

        adapter.setVideos(favouriteVideos);
    }
}
