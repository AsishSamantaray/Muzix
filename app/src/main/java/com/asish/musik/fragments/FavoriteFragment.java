package com.asish.musik.fragments;


import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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

import com.asish.musik.views.FavoriteAdapter;
import com.asish.musik.databases.MusikDatabase;
import com.asish.musik.R;
import com.asish.musik.models.Songs;

import java.util.ArrayList;


public class FavoriteFragment extends Fragment {
    Activity myActivity;
    ContentResolver contentResolver;
    Cursor songCursor;
    Uri uri;
    TextView noFavorites = null;
    RelativeLayout nowPlayingBottomBar= null;
    ImageButton playPauseButton = null;
    TextView songTitle = null;
    RecyclerView recyclerView = null;
    int trackPosition = 0;
    MusikDatabase favoriteContent = null;

    ArrayList<Songs> refreshList =null;
    ArrayList<Songs> getListfromDatabase =null;

    static  MediaPlayer mediaPlayer = null;
    private android.support.v7.widget.SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    FavoriteAdapter favoriteAdapter;

    public FavoriteFragment() {
        // Required empty public constructor
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
                    favoriteAdapter.filter(newText);
                    return true;
                }
            });

        }
        super.onCreateOptionsMenu(menu, inflater);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        getActivity().setTitle("Favorite");

        favoriteContent = new MusikDatabase(myActivity);

        noFavorites = view.findViewById(R.id.noFavourites);
        nowPlayingBottomBar = view.findViewById(R.id.hiddenBarFavScreen);
        songTitle = view.findViewById(R.id.songTitleFavScreen);
        playPauseButton = view.findViewById(R.id.playPauseButton);
        recyclerView = view.findViewById(R.id.favoriteRecycler);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        display_favorites_by_searching();
        bottomBarSetup();
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

    public void display_favorites_by_searching() {
        if (favoriteContent.checkSize() > 0) {

            refreshList = new ArrayList<Songs>();
            /*Getting the list of songs from database*/
            getListfromDatabase = favoriteContent.queryDBforList();
//            Toast.makeText(myActivity, getListfromDatabase.get(0).getSongTitle(), Toast.LENGTH_LONG).show();

            /*Getting list of songs from phone storage*/
            ArrayList<Songs> fetchFavSong = getSongsFromPhone();
//            Toast.makeText(myActivity, fetchFavSong.get(0).getSongTitle(), Toast.LENGTH_LONG).show();

            /*If there are no songs in phone then there cannot be any favorites*/
            if (fetchFavSong != null) {
                /*Then we check all the songs in the phone*/
//                for (int i=0 ; i<fetchFavSong.size(); i++) {
                    /*We iterate through every song in database*/
                    for (int j=0; j < getListfromDatabase.size(); j++) {

//                        if (getListfromDatabase.get(j).songId == fetchFavSong.get(i).songId) {
                            refreshList.add((getListfromDatabase.get(j)));
//                        }
                    }
//                }
            } else {
            }
            /*If refresh list is null we display that there are no favorites*/
            if (refreshList == null) {
                recyclerView.setVisibility(View.INVISIBLE);
                noFavorites.setVisibility(View.VISIBLE);
            } else {
                /*Else we setup our recycler view for displaying the favorite songs*/
                favoriteAdapter = new FavoriteAdapter(refreshList, myActivity);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(myActivity);
                recyclerView.setLayoutManager( mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(favoriteAdapter);
//                recyclerView.setHasFixedSize(true);
            }
        } else {
/*If initially the checkSize() function returned 0 then also we display the no
favorites present message*/
            recyclerView.setVisibility(View.INVISIBLE);
            noFavorites.setVisibility(View.VISIBLE);
        }
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
                if(SongPlayingFragment.mediaPlayer != null ) {
                    mediaPlayer = SongPlayingFragment.mediaPlayer;
                }
//                FavoriteFragment.mediaPlayer = SongPlayingFragment.mediaPlayer;
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

//                if (getFragmentManager() != null) {
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.details_fragment, songPlayingFragment)
                            .addToBackStack("SongPlayingFragment")
                            .commit();
//                }
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
