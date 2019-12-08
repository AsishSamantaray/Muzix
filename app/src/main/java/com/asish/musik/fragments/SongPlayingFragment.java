package com.asish.musik.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.asish.musik.models.CurrentSongHelper;
import com.asish.musik.databases.MusikDatabase;
import com.asish.musik.R;
import com.asish.musik.models.Songs;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SongPlayingFragment extends Fragment implements SensorEventListener {

    static int songPosition;
    static Thread thread;
    static TextView songArtistView;
    static TextView songTitleView;
    static Button playPauseImageButton;
    static Button previousImageButton;
    static Button nextImageButton;
    static ImageView songImg;
    static TextView rightTime, leftTime;
    static int currentPosition = 0;
    static ImageButton fab, shuffleImageButton;
    static MediaSession mSession;

    static SeekBar seekBar;
    static ArrayList<Songs> fetchSongs;

    static MediaPlayer mediaPlayer;
    static MusikDatabase favoriteContent;


    static CurrentSongHelper currentSongHelper = new CurrentSongHelper();


    // variables for shake detection
    private static final float SHAKE_THRESHOLD = 9f; // m/S**2
    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 1000;
    private long mLastShakeTime;
    private SensorManager mSensorMgr;

    static String MY_PREFS_SHUFFLE = "Shuffle feature";
    //After launch
    static String MY_PREFS_NAME = "ShakeFeature";

    static Activity myActivity;

    public SongPlayingFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_playing, container, false);
        getActivity().setTitle("Now Playing");

        currentSongHelper.isPlaying = true;
//        currentSongHelper.isLoop = false;
        currentSongHelper.isShuffle = false;

        // Get Views..
        songTitleView = view.findViewById(R.id.songTextLabel);
        songArtistView = view.findViewById(R.id.songArtistLabel);
        playPauseImageButton = view.findViewById(R.id.btnPause);
        previousImageButton = view.findViewById(R.id.btnPrevious);
        nextImageButton = view.findViewById(R.id.btnNext);
        seekBar = view.findViewById(R.id.seekBar);
        songImg = view.findViewById(R.id.songImg);
        songTitleView.setSelected(true);
        leftTime = view.findViewById(R.id.leftTime);
        rightTime = view.findViewById(R.id.rightTime);
        fab = view.findViewById(R.id.button_favorite);
        shuffleImageButton = view.findViewById(R.id.shuffle_favorite);

        fab.setAlpha(0.8f);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        favoriteContent = new MusikDatabase(myActivity);
        currentSongHelper = new CurrentSongHelper();

        String path = null;
        String _songTitle = null;
        String _songArtist = null;
        long songId= 0;
        try {
            path = getArguments().getString("path");
            _songTitle = getArguments().getString("songTitle");
            _songArtist = getArguments().getString("songArtist");
            songId = getArguments().getInt("SongId");
            currentPosition = getArguments().getInt("songPosition");
            fetchSongs = getArguments().getParcelableArrayList("songData");
            if (_songArtist.equalsIgnoreCase("<unknown>")) {
                _songArtist = "unknown";
            }

            currentSongHelper.songPath = path;
            currentSongHelper.songTitle = _songTitle;
            currentSongHelper.songArtist = _songArtist;
            currentSongHelper.songId = songId;

            currentSongHelper.currentPosition = currentPosition;


            updateTextViews(currentSongHelper.songTitle , fetchSongs.get(currentPosition).getArtist() );
            getAlbumArt(fetchSongs.get(currentPosition).getSongData());
        } catch ( Exception e) {
            e.printStackTrace();
        }
        //change 4
        // val fromBottomBar = arguments?.get("BottomBar") as? String

//        var fromFavBottomBar = getArguments().get("FavBottomBar");
        if (getArguments().get("FavBottomBar") != null) {
            mediaPlayer = FavoriteFragment.mediaPlayer;
        } else {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            try {
                mediaPlayer.setDataSource(myActivity, Uri.parse(path));
                mediaPlayer.prepare();
            } catch ( Exception e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                int currentPos = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration() ;
                int diff = duration - currentPos;
                leftTime.setText(
                        String.format(
                                "%d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(currentPos),
                                TimeUnit.MILLISECONDS.toSeconds(currentPos) % 60
                        )
                );
                rightTime.setText(
                        String.format(
                                "%d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(diff),
                                TimeUnit.MILLISECONDS.toSeconds(diff) % 60
                        )
                );


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });


        processInformation(mediaPlayer );
        if (currentSongHelper.isPlaying ) {
            mediaPlayer.pause();
            currentSongHelper.isPlaying = false;

            playPauseImageButton.setBackgroundResource(R.drawable.icon_pause);
        } else {
            playPauseImageButton.setBackgroundResource(R.drawable.icon_play);
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onSongComplete();
            }
        });

        //change 5
//        audioVisualization?.linkTo(DbmHandler.Factory.newVisualizerHandler(activity as Context, mediaPlayer?.audioSessionId as Int))
        if (mediaPlayer.isPlaying()) {
            playPauseImageButton.setBackgroundResource(R.drawable.icon_pause);
        } else {
            playPauseImageButton.setBackgroundResource(R.drawable.icon_play);
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onSongComplete();
            }
        });

        clickHandler();

        //var visualizationHandler = DbmHandler.Factory.newVisualizerHandler(myActivity as Context, 0)
        //audioVisualization?.linkTo(visualizationHandler)


        SharedPreferences prefsForShuffle = myActivity.getSharedPreferences(MY_PREFS_SHUFFLE, Context.MODE_PRIVATE);

        boolean isShuffleAllowed = prefsForShuffle.getBoolean("feaure", false);
        if (isShuffleAllowed ) {
            currentSongHelper.isShuffle = true;
            currentSongHelper.isLoop = false;
            shuffleImageButton.setBackgroundResource(R.drawable.shuffle_icon);
//            loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {

            currentSongHelper.isShuffle = false;
            shuffleImageButton.setBackgroundResource(R.drawable.baseline_shuffle_black_48);
        }


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
    public void onResume() {
        super.onResume();
        if (mediaPlayer.isPlaying()) {
            currentSongHelper.isPlaying = true;
            playPauseImageButton.setBackgroundResource(R.drawable.icon_pause);
        } else {
            currentSongHelper.isPlaying = false;
            playPauseImageButton.setBackgroundResource(R.drawable.icon_play);
        }


        mSensorMgr.registerListener(this, mSensorMgr.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();

        mSensorMgr.unregisterListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSensorMgr = (SensorManager) myActivity.getSystemService(Context.SENSOR_SERVICE);
        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    //    public void updateSongTime() {
    static Runnable updateSongTime =new Runnable() {
        @Override
        public void run() {
            int getCurrent = mediaPlayer.getCurrentPosition();
            leftTime.setText(String.format("%d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(getCurrent),
                    TimeUnit.MILLISECONDS.toSeconds(getCurrent) -
                            TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent))));

            seekBar.setProgress(getCurrent);

            new Handler().postDelayed(this, 1000);
        }
    };
//    }

    public static void clickHandler() {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favoriteContent.checkifIdExists((int)currentSongHelper.songId)) {
                    fab.setBackgroundResource(R.drawable.button_favorite);
                    favoriteContent.deleteFavourite((int)currentSongHelper.songId);
                    /*Toast is prompt message at the bottom of screen indicating that an
                    action has been performed*/
                    Toast.makeText(myActivity, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                } else {
                    /*If the song was not a favorite, we then add it to the favorites using
                    the method we made in our database*/
                    fab.setBackgroundResource(R.drawable.fav_on);
                    favoriteContent.storeasFavourite((int)currentSongHelper.songId,
                            currentSongHelper.songArtist, currentSongHelper.songTitle, currentSongHelper.songPath);
                    Toast.makeText(myActivity, "Added to Favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });

        shuffleImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editorShuffle =
                        myActivity.getSharedPreferences(MY_PREFS_SHUFFLE, Context.MODE_PRIVATE).edit();
//                SharedPreferences.Editor editorLoop = myActivity.getSharedPreferences(MY_PREFS_LOOP, Context.MODE_PRIVATE).edit();

                if (currentSongHelper.isShuffle) {
                    shuffleImageButton.setBackgroundResource(R.drawable.baseline_shuffle_black_48);
                    currentSongHelper.isShuffle = false;

                    editorShuffle.putBoolean("feature", false);
                    editorShuffle.apply();
                } else {
                    currentSongHelper.isShuffle = true;
                    currentSongHelper.isLoop = false;
                    shuffleImageButton.setBackgroundResource(R.drawable.shuffle_icon);
//                    loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)

                    editorShuffle.putBoolean("feature", true);
                    editorShuffle.apply();

//                    editorLoop.putBoolean("feature", false);
//                    editorLoop.apply();
                }
            }
        });


        nextImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSongHelper.isPlaying = true;
                playPauseImageButton.setBackgroundResource(R.drawable.icon_pause);
                if (currentSongHelper.isShuffle ) {
                    playNext("PlayNextLikeNormalShuffle");

                } else {
                    playNext("PlayNextNormal");
                }
            }
        });

        previousImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSongHelper.isPlaying = true;
                playPrevious();
            }
        });

        playPauseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(myActivity, "play it", Toast.LENGTH_SHORT).show();
                if (mediaPlayer.isPlaying()) {
                    currentSongHelper.isPlaying = true;

                    //currentSongHelper?.isPlaying = false
                    playPauseImageButton.setBackgroundResource(R.drawable.icon_play);
                    mediaPlayer.pause();
                } else {
                    currentSongHelper.isPlaying = false;
                    playPauseImageButton.setBackgroundResource(R.drawable.icon_pause);
                    mediaPlayer.seekTo(seekBar.getProgress());
                    mediaPlayer.start();
                }
            }
        });
    }

    public static void playNext(String check) {


        if (check.equalsIgnoreCase("PlayNextNormal")) {

            currentPosition = currentPosition + 1;
        } else if (check.equalsIgnoreCase("PlayNextLikeNormalShuffle")) {

            Random randomObject = new Random();

            int randomPosition = randomObject.nextInt(fetchSongs.size()+(1));

            currentPosition = randomPosition;


        }

        if (currentPosition == fetchSongs.size()) {
            currentPosition = 0;
        }
        currentSongHelper.isLoop = false;
        Songs nextSongs = fetchSongs.get(currentPosition);
        currentSongHelper.songPath = nextSongs.songData;
        currentSongHelper.songTitle = nextSongs.songTitle;
        currentSongHelper.songId = nextSongs.songId;

        updateTextViews(currentSongHelper.songTitle, fetchSongs.get(currentPosition).getArtist());
        getAlbumArt(fetchSongs.get(currentPosition).getSongData());

        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(myActivity, Uri.parse(currentSongHelper.songPath));
            mediaPlayer.prepare();
            mediaPlayer.start();
            processInformation(mediaPlayer);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (favoriteContent.checkifIdExists((int)currentSongHelper.songId)) {
            fab.setBackgroundResource(R.drawable.fav_on);
        } else {
            fab.setBackgroundResource(R.drawable.button_favorite);
        }
    }


    public static void playPrevious() {

        currentPosition = currentPosition - 1;
        if (currentPosition == -1) {
            currentPosition = 0;
        }
        if (currentSongHelper.isPlaying) {
            playPauseImageButton.setBackgroundResource(R.drawable.icon_pause);
        } else {
            playPauseImageButton.setBackgroundResource(R.drawable.icon_play);
        }
        currentSongHelper.isLoop = false;

        Songs nextSong = fetchSongs.get(currentPosition);
        currentSongHelper.songPath = nextSong.songData;
        currentSongHelper.songTitle = nextSong.songTitle;
        currentSongHelper.songArtist = nextSong.artist;
        currentSongHelper.songId = nextSong.songId;

        updateTextViews(currentSongHelper.songTitle , fetchSongs.get(currentPosition).getArtist() );
        getAlbumArt(fetchSongs.get(currentPosition).getSongData());
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(myActivity, Uri.parse(currentSongHelper.songPath));
            mediaPlayer.prepare();
            mediaPlayer.start();
            processInformation(mediaPlayer);
        } catch ( Exception e) {
            e.printStackTrace();
        }
        if (favoriteContent.checkifIdExists((int)currentSongHelper.songId)) {
            fab.setBackgroundResource(R.drawable.fav_on);
        } else {
            fab.setBackgroundResource(R.drawable.button_favorite);
        }
    }


    public static  void onSongComplete() {
        if (currentSongHelper.isShuffle) {
            playNext("PlayNextLikeNormalShuffle");
            currentSongHelper.isPlaying = true;
        } else {
            if (currentSongHelper.isLoop) {
                currentSongHelper.isPlaying = true;
                Songs nextSong = fetchSongs.get(currentPosition);
                currentSongHelper.currentPosition = currentPosition;
                currentSongHelper.songPath = nextSong.songData;
                currentSongHelper.songTitle = nextSong.songTitle;
                currentSongHelper.songArtist = nextSong.artist;
                currentSongHelper.songId = nextSong.songId;

                updateTextViews(currentSongHelper.songTitle , fetchSongs.get(currentPosition).getArtist() );
                getAlbumArt(fetchSongs.get(currentPosition).getSongData());
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(myActivity, Uri.parse(currentSongHelper.songPath));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    processInformation(mediaPlayer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                playNext("PlayNextNormal");
                currentSongHelper.isPlaying = true;
            }
        }
        if (favoriteContent.checkifIdExists((int)currentSongHelper.songId)) {
            fab.setBackgroundResource(R.drawable.fav_on);
        } else {
            fab.setBackgroundResource(R.drawable.button_favorite);
        }
    }


    public static void updateTextViews(String songTitle, String songArtist) {

        String songTitleUpdated = songTitle;
        String songArtistUpdated=songArtist;
        if (songTitle.equalsIgnoreCase("<unknown>")){
            songTitleUpdated= "unknown";
        }
        if (songArtist.equalsIgnoreCase("<unknown>")){
            songArtistUpdated= "unknown";
        }
        songTitleView.setText(songTitleUpdated);
        songArtistView.setText(songArtistUpdated);

    }

    public static void processInformation(MediaPlayer mediaPlayer) {


        int finalTime = mediaPlayer.getDuration();
        /*Obtaining the current position*/
        int startTime = mediaPlayer.getCurrentPosition();
        /*Here we format the time and set it to the start time text*/
        seekBar.setMax(finalTime);
        leftTime.setText(String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(startTime),
                TimeUnit.MILLISECONDS.toSeconds(startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime)))
        );
        /*Similar to above is done for the end time text*/
        rightTime.setText(String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(finalTime),
                TimeUnit.MILLISECONDS.toSeconds(finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime)))
        );

        seekBar.setProgress(startTime);
        new Handler().postDelayed(updateSongTime, 1000);
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            if ((curTime - mLastShakeTime) > MIN_TIME_BETWEEN_SHAKES_MILLISECS) {

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                double acceleration = Math.sqrt(Math.pow(x, 2) +
                        Math.pow(y, 2) +
                        Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;
//                Toast.makeText(this, "Acceleration is " + acceleration + "m/s^2", Toast.LENGTH_SHORT).show();

                if (acceleration > SHAKE_THRESHOLD) {
                    mLastShakeTime = curTime;
                    SharedPreferences prefs = myActivity.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
                    boolean isAllowed = prefs.getBoolean("feature", false);
                    if (isAllowed) {
                        playPauseImageButton.setBackgroundResource(R.drawable.icon_pause);
                        playNext("PlayNextNormal");
                        Toast.makeText(myActivity, "Shaked..", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_search);
        if(item != null) {
            item.setVisible(false);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Load Album Art..
    public static void getAlbumArt(String songDta) {
        MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
        try {
            metaRetriver.setDataSource(songDta);
//            Toast.makeText(myActivity, "Can't Play..", Toast.LENGTH_SHORT).show();
            byte[] art = metaRetriver.getEmbeddedPicture();
            Bitmap songImage = BitmapFactory
                    .decodeByteArray(art, 0, art.length);
            songImg.setImageBitmap(songImage);
        } catch (Exception e) {
            songImg.setImageResource(R.drawable.cover_img);
        }
    }




}
