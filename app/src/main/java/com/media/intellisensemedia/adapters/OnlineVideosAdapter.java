package com.media.intellisensemedia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.media.intellisensemedia.R;
import com.media.intellisensemedia.entitiy.Video;
import com.media.intellisensemedia.utils.Utilities;

import java.util.ArrayList;

public class OnlineVideosAdapter  extends RecyclerView.Adapter<OnlineVideosAdapter.MyViewHolder> {

    private ArrayList<Video> videos;
    private Context context;

    public OnlineVideosAdapter(ArrayList<Video> videos, Context context) {
        this.videos = videos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_online_video,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.title.setText(videos.get(position).DISPLAYNAME);
        holder.length.setText(Utilities.getFormatedDuration(videos.get(position).LENGTH));
        holder.stamp.setText(Utilities.getFormattedDate(videos.get(position).STAMP));
        holder.views.setText(Utilities.getFormattedViews(videos.get(position).VIEWS));

        Glide.with(context).load(videos.get(position).THUMBNAIL).into(holder.thumb);

    }

    @Override
    public int getItemCount() {
        return videos==null?0:videos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView title, views, stamp,length;
        ImageView thumb;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.video_title);
            views = itemView.findViewById(R.id.views);
            stamp = itemView.findViewById(R.id.stamp);
            length = itemView.findViewById(R.id.video_duration);
            thumb = itemView.findViewById(R.id.thumbnailImageLayout);
        }
    }
 }
