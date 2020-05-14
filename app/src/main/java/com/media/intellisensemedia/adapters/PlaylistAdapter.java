package com.media.intellisensemedia.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.media.intellisensemedia.PlaylistVideosActivity;
import com.media.intellisensemedia.R;
import com.media.intellisensemedia.entitiy.Playlist;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Playlist> playlists = new ArrayList<>();


    public PlaylistAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.playlist_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.displayName.setText(playlists.get(position).dname);
        holder.displayName.setSelected(true);
        holder.displayName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlaylistVideosActivity.class);
                intent.putExtra(PlaylistVideosActivity.PLAYLIST,playlists.get(position));
                context.startActivity(intent);
            }
        });

        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView deleteIcon;
        TextView displayName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deleteIcon = itemView.findViewById(R.id.deletePlaylistIcon);
            displayName = itemView.findViewById(R.id.playListName);

        }
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }
}
