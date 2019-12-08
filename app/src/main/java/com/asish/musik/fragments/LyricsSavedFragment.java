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

import com.asish.musik.R;
import com.asish.musik.databases.LyricsDatabase;
import com.asish.musik.databases.MusikDatabase;
import com.asish.musik.models.Song1;
import com.asish.musik.views.FavoriteAdapter;
import com.asish.musik.views.LyricsAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class LyricsSavedFragment extends Fragment {

    Activity myActivity;
    ContentResolver contentResolver;
    Cursor songCursor;
    Uri uri;
    TextView noFavorites = null;
    RecyclerView recyclerView = null;
    int trackPosition = 0;
    LyricsDatabase favoriteContent = null;

    ArrayList<Song1> refreshList = null;
    ArrayList<Song1> getListfromDatabase = null;

    static MediaPlayer mediaPlayer = null;
    private android.support.v7.widget.SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    LyricsAdapter favoriteAdapter;

    public LyricsSavedFragment() {
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
        View view = inflater.inflate(R.layout.fragment_lyrics_saved, container, false);
        getActivity().setTitle("Saved Lyrics");

        favoriteContent = new LyricsDatabase(myActivity);

        noFavorites = view.findViewById(R.id.noFavourites);
        recyclerView = view.findViewById(R.id.favoriteRecycler);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        display_favorites_by_searching();
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


    public void display_favorites_by_searching() {
        if (favoriteContent.checkSize() > 0) {

            refreshList = new ArrayList<Song1>();
            /*Getting the list of songs from database*/
            getListfromDatabase = favoriteContent.queryDBforList();
//            Toast.makeText(myActivity, getListfromDatabase.get(0).getSongTitle(), Toast.LENGTH_LONG).show();

            /*Getting list of songs from phone storage*/
//            Toast.makeText(myActivity, fetchFavSong.get(0).getSongTitle(), Toast.LENGTH_LONG).show();

            /*If there are no songs in phone then there cannot be any favorites*/

            for (int j = 0; j < getListfromDatabase.size(); j++) {

//                        if (getListfromDatabase.get(j).songId == fetchFavSong.get(i).songId) {
                refreshList.add((getListfromDatabase.get(j)));
//                        }
            }
//                }

            /*If refresh list is null we display that there are no favorites*/
            if (refreshList == null) {
                recyclerView.setVisibility(View.INVISIBLE);
                noFavorites.setVisibility(View.VISIBLE);
            } else {
                /*Else we setup our recycler view for displaying the favorite songs*/
                favoriteAdapter = new LyricsAdapter(refreshList, myActivity);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(myActivity);
                recyclerView.setLayoutManager(mLayoutManager);
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
}


