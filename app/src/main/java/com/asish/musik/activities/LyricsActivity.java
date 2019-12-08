package com.asish.musik.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.asish.musik.R;
import com.asish.musik.databases.LyricsDatabase;

public class LyricsActivity extends AppCompatActivity {

    TextView tvLyrics;
    TextView songTitle;
    TextView songArtist;
    ImageView img_header;
    LyricsDatabase lyricsDatabase;
    FloatingActionButton fabLinks;

    CollapsingToolbarLayout collapsingToolbar;
    AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Used to add Status Bar Color..

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        }

        setContentView(R.layout.activity_lyrics);

        lyricsDatabase = new LyricsDatabase(this);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
//        mToolbar.setTitle(getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        initCollapsingToolBar();

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.TextAppearance_MyApp_Title_Collapsed);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.TextAppearance_MyApp_Title_Expanded);
        collapsingToolbar.setTitle(" ");

        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    //when collapsingToolbar at that time display actionbar title
                    collapsingToolbar.setTitle(getIntent().getStringExtra("title"));
                    isShow = true;
                } else if (isShow) {
                    //careful there must a space between double quote otherwise it dose't work
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvLyrics = findViewById(R.id.tvLyrics);
        songTitle = findViewById(R.id.songTitle);
        songArtist = findViewById(R.id.songArtist);
        img_header = findViewById(R.id.img_header);
        fabLinks = findViewById(R.id.fabLinks);

        // Get the data
        Intent intent = getIntent();
        String lyrics = intent.getStringExtra("Lyrics");
        String title = intent.getStringExtra("title");
        String artist = intent.getStringExtra("artist");
        String from = intent.getStringExtra("from");

        fabLinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if (lyricsDatabase.checkifIdExists(title) && from.equals("internet")) {
            lyricsDatabase.deleteFavourite(title);
            tvLyrics.setText(lyrics);
            songTitle.setText(title);
            songArtist.setText(artist);
        } else if(!from.equals("not internet")) {
                lyricsDatabase.storeLyrics(artist, title, lyrics);
                Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();
            tvLyrics.setText(lyrics);
            songTitle.setText(title);
            songArtist.setText(artist);

        }else {
            tvLyrics.setText(lyrics);
            songTitle.setText(title);
            songArtist.setText(artist);
        }

    }


}
