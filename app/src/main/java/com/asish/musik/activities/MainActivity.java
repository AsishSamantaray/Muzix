package com.asish.musik.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.asish.musik.R;
import com.asish.musik.fragments.FavoriteFragment;
import com.asish.musik.fragments.LyricsSavedFragment;
import com.asish.musik.fragments.MainScreenFragment;
import com.asish.musik.fragments.SettingFragment;
import com.asish.musik.fragments.AboutUs;
import com.asish.musik.fragments.LyricsFetch;
import com.asish.musik.fragments.SongFetch;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

//    ContentResolver contentResolver;
//    Cursor songCursor;
//    Uri uri;
//    ArrayList<Songs> getSongsList;
//
//    // Get View..
//    RelativeLayout nowPlayingBottomBar  = null;
//    ImageButton playPauseButton = null;
//    TextView songTitle = null;
//    RelativeLayout visibleLayout = null;
//    RelativeLayout noSongs = null;
//    RecyclerView recyclerView = null;
//    int trackPosition = 0;
//
//    MainScreenAdapter mainScreenAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Used to add Status Bar Color..
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        }
        setSupportActionBar(toolbar);
//        toolbar.setTitle("All Songs");
//        getSupportActionBar().setTitle("All Songs");

        // Get Views..
//        visibleLayout = findViewById(R.id.visibleLayout);
//        noSongs = findViewById(R.id.noSongs);
//        nowPlayingBottomBar = findViewById(R.id.hiddenBarMainScreen);
//        songTitle = findViewById(R.id.songTitleMainScreen);
//        playPauseButton = findViewById(R.id.playPauseButton);
//        recyclerView = findViewById(R.id.contentMain);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        getSupportFragmentManager().
                beginTransaction()
                .replace(R.id.details_fragment, new MainScreenFragment(), "MainScreenFragment")
                .commit();

        // Show song in RV..
//        getSongsList = getSongsFromPhone();
//        mainScreenAdapter = new MainScreenAdapter(MainActivity.this, getSongsList);
//        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(mainScreenAdapter);

    }

    // Get All songs from device..
//    public ArrayList<Songs> getSongsFromPhone() {
//        ArrayList<Songs> arrayList = new ArrayList();
//        Context context = getApplicationContext();
//        contentResolver = context.getContentResolver();
//        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
//        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
//        songCursor = contentResolver.query(uri, null, selection, null, sortOrder);
//        if(songCursor == null) {
//            Toast.makeText(context, "Something went wrong..", Toast.LENGTH_LONG).show();
//        }
//        else if(!songCursor.moveToFirst()) {
//            Toast.makeText(context, "No music found on SD card..", Toast.LENGTH_LONG).show();
//        }
//        else {
//            int songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
//            int sonTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
//            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
//            int songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
//            int dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED);
//            int albumArt = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
//
//
//            while(songCursor.moveToNext()) {
//                long currentId = songCursor.getLong(songId);
//                String currentTitle = songCursor.getString(sonTitle);
//                String currentArtist = songCursor.getString(songArtist);
//                if (currentArtist.equalsIgnoreCase("<unknown>")) {
//                    currentArtist = "unknown";
//                }
//                String  currentData = songCursor.getString(songData);
//                long currentDate = songCursor.getLong(dateIndex);
//                Long currentAlbumArt = songCursor.getLong(albumArt);
//
//                arrayList.add(new Songs(currentId, currentDate, currentAlbumArt, currentTitle, currentArtist, currentData));
//            }
//        }
//        return arrayList;
//    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_allsongs) {
            getSupportFragmentManager().
                    beginTransaction()
                    .replace(R.id.details_fragment, new MainScreenFragment())
                    .commit();
        } else if (id == R.id.nav_favorites) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.details_fragment, new FavoriteFragment())
                    .commit();

        } else if (id == R.id.nav_settings) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.details_fragment, new SettingFragment())
                    .commit();
//            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_aboutus) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.details_fragment, new AboutUs())
                    .commit();

        }
        else if(id == R.id.savedLyrics) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.details_fragment, new LyricsSavedFragment())
                    .commit();

        }
        else if (id == R.id.audd){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.details_fragment, new SongFetch())
                    .commit();

        }

        else if (id == R.id.muzlyrics){

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.details_fragment, new LyricsFetch())
                    .commit();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
