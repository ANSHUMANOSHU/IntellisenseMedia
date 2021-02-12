package com.media.intellisensemedia.fragmentshelper;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.media.intellisensemedia.R;
import com.media.intellisensemedia.adapters.OfflineVideosAdapter;
import com.media.intellisensemedia.adapters.OnlineVideosAdapter;
import com.media.intellisensemedia.entitiy.Video;
import com.media.intellisensemedia.interfaces.OnFetchedListener;
import com.media.intellisensemedia.utils.VideoFetcher;

import java.util.ArrayList;

public class OnlineVideosFragment extends Fragment {

    private RecyclerView recyclerView;
    private OnlineVideosAdapter adapter;
    private ProgressDialog progressDialog;

    public OnlineVideosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_online_videos, container, false);

        //   I N I T I A L I Z E     V I E W S
        recyclerView =  view.findViewById(R.id.onlineVideoRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressDialog= new ProgressDialog(getContext());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        //   F E T C H    A L L    V I D E O S
        VideoFetcher.fetchOnlineVideos(new OnFetchedListener() {
            @Override
            public void fetched(ArrayList<Video> videos) {
                //FETCH COMPLETE
                progressDialog.dismiss();
                adapter = new OnlineVideosAdapter(videos,getContext());
                recyclerView.setAdapter(adapter);
            }
        });

        return view;
    }
}
