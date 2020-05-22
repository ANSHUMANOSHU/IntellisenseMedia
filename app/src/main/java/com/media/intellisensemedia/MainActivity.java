package com.media.intellisensemedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.media.intellisensemedia.dbhelpers.FavouriteHelper;
import com.media.intellisensemedia.entitiy.Video;
import com.media.intellisensemedia.fragmentshelper.OfflineVideosFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize views
        initViews();

    }


    // INITIALIZING VIEWS
    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // A T T A C H    L I S T E N E R
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.bottom_videos:
                break;
            case R.id.bottom_movies:
                break;
            case R.id.bottom_offline:
                transaction.replace(R.id.frame_container, new OfflineVideosFragment());
                transaction.commit();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);

        MenuItem item = menu.findItem(R.id.search_bar);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.favourites:
                Intent intent = new Intent(this,FavouriteActivity.class);
                startActivity(intent);
                break;
            case R.id.playlists:
                Intent intent1 = new Intent(this,PlayListActivity.class);
                startActivity(intent1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
