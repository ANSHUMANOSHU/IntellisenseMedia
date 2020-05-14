package com.media.intellisensemedia.fragmentshelper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.media.intellisensemedia.adapters.OfflineVideosAdapter;
import com.media.intellisensemedia.entitiy.Video;
import com.media.intellisensemedia.utils.VideoFetcher;
import com.media.intellisensemedia.R;

import java.util.ArrayList;

public class OfflineVideosFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Video> videos;
    private OfflineVideosAdapter adapter;
    private static final String ACTIVITY = "OFFLINE";
    private TextView sorryText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.offline_videos_fragment,container,false);

        //   I N I T I A L I Z E     V I E W S
        recyclerView =  view.findViewById(R.id.offline_videos_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sorryText = view.findViewById(R.id.sorryText);

        //   F E T C H    A L L    V I D E O S
        videos = VideoFetcher.fetchAllVideos(getContext());

        //   C H E C K   V I D E O S   E M P T Y   O R   N O T
        if (videos.isEmpty())
            sorryText.setVisibility(View.VISIBLE);

        //   S E T    A D A P T E R
        adapter = new OfflineVideosAdapter(getContext(),ACTIVITY);
        recyclerView.setAdapter(adapter);

        adapter.setVideos(videos);

        return view;
    }

}
