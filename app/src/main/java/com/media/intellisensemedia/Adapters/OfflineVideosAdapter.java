package com.media.intellisensemedia.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.media.intellisensemedia.DatabaseHelpers.DatabaseHelper;
import com.media.intellisensemedia.EntityClasses.Video;
import com.media.intellisensemedia.ExoplayerActivity;
import com.media.intellisensemedia.FavouriteActivity;
import com.media.intellisensemedia.R;

import java.io.File;
import java.util.ArrayList;

public class OfflineVideosAdapter extends RecyclerView.Adapter<OfflineVideosAdapter.ViewHolder> {

    private static final String VIDEO_URI = "VIDEOURI";
    private Context context;
    private ArrayList<Video> videos = new ArrayList<>();
    private String ACTIVITY = "";
    DatabaseHelper databaseHelper;

    public OfflineVideosAdapter(Context context, String ACTIVITY) {
        this.context = context;
        this.ACTIVITY = ACTIVITY;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.title.setText(videos.get(position).DISPLAYNAME);
        holder.title.setSelected(true);

        holder.duration.setText(videos.get(position).LENGTH);

        Glide.with(context).asBitmap().load(Uri.fromFile(new File(videos.get(position).DATA))).into(holder.thumbnailImage);

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ExoplayerActivity.class);
                intent.putExtra(VIDEO_URI, videos.get(position).DATA);
                context.startActivity(intent);
            }
        });

        holder.threeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu;
                databaseHelper = new DatabaseHelper(context);
                if (ACTIVITY.equals("OFFLINE")) {
                    popupMenu = new PopupMenu(context, holder.threeDots);
                    popupMenu.inflate(R.menu.popup_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.favouritePopup:
                                    if (!videos.get(position).STATUS_ADDED) {

                                        videos.get(position).STATUS_ADDED = true;

                                        if (databaseHelper.insertFavourite(videos.get(position)))
                                            Toast.makeText(context, "Added Successfully...", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(context, "Sorry! Some issue occurred...", Toast.LENGTH_SHORT).show();

                                    } else
                                        Toast.makeText(context, "Already Exists", Toast.LENGTH_SHORT).show();

                                    break;
                                case R.id.playlistPopup:
                                    Toast.makeText(context, "playlist", Toast.LENGTH_SHORT).show();
                                    break;
                            }

                            return true;
                        }
                    });
                    popupMenu.show();
                } else if (ACTIVITY.equals("FAVOURITE")) {
                    popupMenu = new PopupMenu(context, holder.threeDots);
                    popupMenu.inflate(R.menu.favourite_popup_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.removeFavourite) {
                                if (databaseHelper.deleteVideofromFavourites(videos.get(position).DATA)) {
                                    videos.remove(videos.get(position));
                                    if (videos.isEmpty())
                                        ((FavouriteActivity) context).sorryText.setVisibility(View.VISIBLE);
                                    notifyDataSetChanged();
                                    Toast.makeText(context, "Removed Successfully...", Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(context, "Sorry! some issue occurred...", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout itemLayout;
        ImageView thumbnailImage;
        TextView title, duration;
        ImageView threeDots;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemLayout = itemView.findViewById(R.id.layoutItem);
            thumbnailImage = itemView.findViewById(R.id.thumbnailImageLayout);
            title = itemView.findViewById(R.id.video_title);
            duration = itemView.findViewById(R.id.video_duration);
            threeDots = itemView.findViewById(R.id.thumbnailThreeDots);

        }
    }

    public void setVideos(ArrayList<Video> videos) {
        this.videos = videos;
        notifyDataSetChanged();
    }
}
