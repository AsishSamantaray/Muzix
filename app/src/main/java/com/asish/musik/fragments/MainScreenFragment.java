package com.asish.musik.fragments;


import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asish.musik.views.MainScreenAdapter;
import com.asish.musik.R;
import com.asish.musik.models.Songs;

import java.util.ArrayList;

public class MainScreenFragment extends Fragment {

    Activity myActivity;

    ContentResolver contentResolver;
    Cursor songCursor;
    Uri uri;
    ArrayList<Songs> getSongsList;
    private MenuItem searchMenuItem;
    private android.support.v7.widget.SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;



    // Get View..
    RelativeLayout nowPlayingBottomBar  = null;
    ImageButton playPauseButton = null;
    TextView songTitle = null;
    RelativeLayout visibleLayout = null;
    RelativeLayout noSongs = null;
    RecyclerView recyclerView;
    int trackPosition = 0;

    MainScreenAdapter mainScreenAdapter;

    public MainScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_screen, container, false);
        // Get Views..
        visibleLayout = view.findViewById(R.id.visibleLayout);
        noSongs = view.findViewById(R.id.noSongs);
        nowPlayingBottomBar = view.findViewById(R.id.hiddenBarMainScreen);
        songTitle = view.findViewById(R.id.songTitleMainScreen);
        playPauseButton = view.findViewById(R.id.playPauseButton);
        recyclerView = view.findViewById(R.id.contentMain);
        getActivity().setTitle("All Songs");

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Show song in RV..
        getSongsList = getSongsFromPhone();
        mainScreenAdapter = new MainScreenAdapter(myActivity, getSongsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(myActivity));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mainScreenAdapter);

        bottomBarSetup();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myActivity = (Activity) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        myActivity = activity;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (android.support.v7.widget.SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchEditText.setTextColor(getResources().getColor(R.color.white));
            searchView.setQueryHint("Search");

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    mainScreenAdapter.filter(newText);
                    return true;
                }
            });

        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Get All songs from device..
    public ArrayList<Songs> getSongsFromPhone() {
        ArrayList<Songs> arrayList = new ArrayList();
        Context context = myActivity.getApplicationContext();
        contentResolver = context.getContentResolver();
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        songCursor = contentResolver.query(uri, null, selection, null, sortOrder);
        if(songCursor == null) {
            Toast.makeText(context, "Something went wrong..", Toast.LENGTH_LONG).show();
        }
        else if(!songCursor.moveToFirst()) {
            Toast.makeText(context, "No music found on SD card..", Toast.LENGTH_LONG).show();
        }
        else {
            int songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int sonTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED);
            int albumArt = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);


            while(songCursor.moveToNext()) {
                long currentId = songCursor.getLong(songId);
                String currentTitle = songCursor.getString(sonTitle);
                String currentArtist = songCursor.getString(songArtist);
                if (currentArtist.equalsIgnoreCase("<unknown>")) {
                    currentArtist = "unknown";
                }
                String  currentData = songCursor.getString(songData);
                long currentDate = songCursor.getLong(dateIndex);
                Long currentAlbumArt = songCursor.getLong(albumArt);

                arrayList.add(new Songs(currentId, currentDate, currentAlbumArt, currentTitle, currentArtist, currentData));
            }
        }
        return arrayList;
    }

    ///Bottom bar
    public void bottomBarSetup() {

        try {
            bottomBarClickHandler();
            songTitle.setText(SongPlayingFragment.currentSongHelper.songTitle);
//        SongPlayingFragment.mediaPlayer.setOnCompletionListener({
////                songTitle.setText(SongPlayingFragment.currentSongHelper.songTitle);
////        SongPlayingFragment.onSongComplete();
////            })

            if (SongPlayingFragment.mediaPlayer.isPlaying()) {
                nowPlayingBottomBar.setVisibility(View.VISIBLE);
            } else {
                nowPlayingBottomBar.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void bottomBarClickHandler() {
        nowPlayingBottomBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavoriteFragment.mediaPlayer = SongPlayingFragment.mediaPlayer;
                SongPlayingFragment songPlayingFragment = new SongPlayingFragment();
                Bundle args = new Bundle();

                args.putString("songArtist",
                        SongPlayingFragment.currentSongHelper.songArtist);
                args.putString("songTitle",
                        SongPlayingFragment.currentSongHelper.songTitle);
                args.putString("path",
                        SongPlayingFragment.currentSongHelper.songPath);
                args.putLong("SongID",
                        SongPlayingFragment.currentSongHelper.songId);
                args.putInt("songPosition",
                        SongPlayingFragment.currentSongHelper.currentPosition);
                args.putParcelableArrayList("songData",
                        SongPlayingFragment.fetchSongs);

                args.putString("FavBottomBar", "success");

                songPlayingFragment.setArguments(args);

                if (getFragmentManager() != null) {
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.details_fragment, songPlayingFragment)
                            .addToBackStack("SongPlayingFragment")
                            .commit();
                }
//                ((FragmentActivity) myActivity).getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.details_fragment, new SongPlayingFragment())
//                        .addToBackStack("SongPlayingFragment")
//                        .commit();
            }
        });

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SongPlayingFragment.mediaPlayer.isPlaying()) {
                    SongPlayingFragment.mediaPlayer.pause();
                    trackPosition = SongPlayingFragment.mediaPlayer.getCurrentPosition();
                    playPauseButton.setBackgroundResource(R.drawable.play_icon);
                } else {
                    SongPlayingFragment.mediaPlayer.seekTo(trackPosition);
                    SongPlayingFragment.mediaPlayer.start();
                    playPauseButton.setBackgroundResource(R.drawable.pause_icon);
                }
            }
        });
    }


}
