package com.media.intellisensemedia.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.media.intellisensemedia.ExoplayerActivity;
import com.media.intellisensemedia.FavouriteActivity;
import com.media.intellisensemedia.PlaylistVideosActivity;
import com.media.intellisensemedia.R;
import com.media.intellisensemedia.dbhelpers.FavouriteHelper;
import com.media.intellisensemedia.dbhelpers.PlayListHelper;
import com.media.intellisensemedia.entitiy.Playlist;
import com.media.intellisensemedia.entitiy.Video;
import com.media.intellisensemedia.fragmentshelper.OfflineVideosFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OfflineVideosAdapter extends RecyclerView.Adapter<OfflineVideosAdapter.ViewHolder> {

    //   R E Q U I R E D    V A R I A B L E S
    private static final String VIDEO_URI = "VIDEOURI";
    private Context context;
    private String ACTIVITY = "";

    // A R R A Y    L L I S T S
    private ArrayList<Video> videos = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    //    D A T A B A S E    H E L P E R S
    private PlayListHelper playListHelper;
    private FavouriteHelper favouriteHelper;

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
                favouriteHelper = new FavouriteHelper(context);
                playListHelper = new PlayListHelper(context);

                //  O F F L I N E    F R A G M E N T    P O P U P    M E N U

                if (ACTIVITY.equals(OfflineVideosFragment.ACTIVITY)) {
                    popupMenu = new PopupMenu(context, holder.threeDots);
                    popupMenu.inflate(R.menu.popup_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.favouritePopup:
                                    if (favouriteHelper.insert(videos.get(position)))
                                        Toast.makeText(context, "Added Successfully...", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(context, "Video Already Added...", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.playlistPopup:
                                    showPlaylistDialog();
                                    break;
                            }

                            return true;
                        }

                        private void showPlaylistDialog() {
                            final PlayListHelper helper = new PlayListHelper(context);  //playlist helper
                            final ArrayList<String> displayNames = new ArrayList<>();

                            //dialog creation
                            final Dialog dialog = new Dialog(context);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                            final View view = LayoutInflater.from(context).inflate(R.layout.playlist_selector_dialog
                                    , null, false);   //inflate dialog layout

                            ListView listView = view.findViewById(R.id.playlistView);   // Get list view
                            Button createNewPlaylist = view.findViewById(R.id.createNewPLaylistButton);  // get button view
                            final TextView sorryText = view.findViewById(R.id.sorry);

                            //   R E C E I V E    P L A Y L I S T   F R O M   H E L P E R

                            final ArrayList<Playlist> playlists = helper.fetchAllPlaylist();

                            if (playlists.isEmpty()) {
                                sorryText.setVisibility(View.VISIBLE);
                            }

                            // E X T R A C T   D I S P L A Y   N A M E   F R O M   P L A Y L I S T S

                            for (Playlist playlist : playlists)
                                displayNames.add(playlist.dname);

                            //  S E T   D I S P L A Y   N A M E S  T O   A R R A Y  A D A P T E R   I N   L I S T   V I E W

                            adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, displayNames);
                            listView.setAdapter(adapter);

                            //  L I S T   I T E M   C L I C K   L I S T E N E R

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                                    Toast.makeText(context, "Adding Video \n\nPlease wait...", Toast.LENGTH_SHORT).show();
                                    if (helper.insertVideo(playlists.get(pos), videos.get(position)))
                                        Toast.makeText(context, "Successfully added video", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(context, "Already Added", Toast.LENGTH_SHORT).show();

                                    dialog.dismiss();
                                }
                            });

                            //  T O    C R E A T E    N E W   P L A Y T L I S T

                            createNewPlaylist.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final Playlist playlist = new Playlist();
                                    final Dialog createrDialog = new Dialog(context);

                                    View view1 = LayoutInflater.from(context).inflate(R.layout.new_playlist_dialog
                                            , null, false);

                                    final EditText editText = view1.findViewById(R.id.displayEdit);
                                    Button create = view1.findViewById(R.id.createPLaylist);

                                    create.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String displayname = editText.getText().toString();
                                            if (displayname.isEmpty()) {
                                                Toast.makeText(context, "Field can't be empty...", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            playlist.dname = displayname;
                                            playlist.tname = "dab" + new Date().getTime();
                                            playlist.videos = new ArrayList<>();
                                            helper.addPlaylist(playlist);

                                            displayNames.clear();
                                            ArrayList<Playlist> playlists1 = helper.fetchAllPlaylist();
                                            for (Playlist playlist1 :playlists1)
                                                displayNames.add(playlist1.dname);

                                            adapter.notifyDataSetChanged();
                                            sorryText.setVisibility(View.GONE);
                                            createrDialog.dismiss();

                                        }
                                    });
                                    createrDialog.setContentView(view1);
                                    createrDialog.show();
                                }
                            });

                            //   S E T   V I E W   T O   D I A L O G   A N D   S H O W   D I A L O G
                            dialog.setContentView(view);
                            dialog.show();
                        }
                    });
                    popupMenu.show();


                 //  F A V O U R I T E    A C T I V I T Y    P O P U P    M E N U

                } else if (ACTIVITY.equals(FavouriteActivity.ACTIVITY)) {
                    popupMenu = new PopupMenu(context, holder.threeDots);
                    popupMenu.inflate(R.menu.inner_popup_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.removeVideo) {
                                favouriteHelper.delete(videos.get(position).DATA);
                                videos.clear();
                                setVideos(favouriteHelper.fetchAll());
                                if (videos.isEmpty()) {
                                    ((FavouriteActivity) context).sorryText.setVisibility(View.VISIBLE);
                                }
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }


                //  P L A Y L I S T   V I D E O S   A C T I V I T Y   P O P U P   M E N U

                else if (ACTIVITY.equals(PlaylistVideosActivity.ACTIVITY)){
                    popupMenu = new PopupMenu(context,holder.threeDots);
                    popupMenu.inflate(R.menu.inner_popup_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.removeVideo){
                                playListHelper.delete(PlaylistVideosActivity.PLAYLIST_NAME,videos.get(position));
                                videos.clear();
                                setVideos(playListHelper.fetchAll(PlaylistVideosActivity.PLAYLIST_NAME).videos);
                                if (videos.isEmpty()){
                                    ((PlaylistVideosActivity) context).sorryText.setVisibility(View.VISIBLE);
                                }
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
