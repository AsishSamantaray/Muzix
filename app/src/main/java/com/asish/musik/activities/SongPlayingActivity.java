package com.asish.musik.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.asish.musik.models.CurrentSongHelper;
import com.asish.musik.R;
import com.asish.musik.models.Songs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class SongPlayingActivity extends AppCompatActivity implements SensorEventListener, MediaPlayer.OnCompletionListener {
    int songPosition;
    Thread thread;
    TextView songArtistView;
    TextView songTitleView;
    Button playPauseImageButton;
    Button previousImageButton;
    Button nextImageButton;
    ImageView songImg;
    TextView rightTime, leftTime;
    int currentPosition = 0;
    SeekBar seekBar;
    ArrayList<Songs> mySongs;

    static MediaPlayer mediaPlayer;

    CurrentSongHelper currentSongHelper;

    // variables for shake detection
    private static final float SHAKE_THRESHOLD = 9f; // m/S**2
    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 800;
    private long mLastShakeTime;
    private SensorManager mSensorMgr;

    //After launch
    static String MY_PREFS_NAME = "ShakeFeature";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_playing);
        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        updateThread();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        currentSongHelper = new CurrentSongHelper();

        currentSongHelper.isPlaying = true;
        currentSongHelper.isLoop = false;
        currentSongHelper.isShuffle = false;

        // Get a sensor manager to listen for shakes
        mSensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);

// Listen for shakes
        Sensor accelerometer = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            mSensorMgr.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // Get Views..
        songTitleView = findViewById(R.id.songTextLabel);
        songArtistView = findViewById(R.id.songArtistLabel);
        playPauseImageButton = findViewById(R.id.btnPause);
        previousImageButton = findViewById(R.id.btnPrevious);
        nextImageButton = findViewById(R.id.btnNext);
        seekBar = findViewById(R.id.seekBar);
        songImg = findViewById(R.id.songImg);
        songTitleView.setSelected(true);
        leftTime = findViewById(R.id.leftTime);
        rightTime = findViewById(R.id.rightTime);

        // Get Data from MainActivity..
        Intent intent = getIntent();
        String songArtist = intent.getStringExtra("songArtist");
        final String path = intent.getStringExtra("path");
        String songTitle = intent.getStringExtra("songTitle");
        long SongId = intent.getIntExtra("SongId", 0);
        songPosition = intent.getIntExtra("songPosition", 0);
        mySongs = (ArrayList) intent.getParcelableArrayListExtra("songData");
        getAlbumArt(mySongs.get(songPosition).getSongData());

//        currentSongHelper.songPath =

        Uri u = Uri.parse(path);
        mediaPlayer = MediaPlayer.create(SongPlayingActivity.this, u);
        mediaPlayer.start();
        updateThread();

        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                int currentPos = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                int diff = duration - currentPos;
                leftTime.setText(String.format("%d:%02d", TimeUnit.MILLISECONDS.toMinutes(currentPos), TimeUnit.MILLISECONDS.toSeconds(currentPos)%60));
                rightTime.setText(String.format("%d:%02d", TimeUnit.MILLISECONDS.toMinutes(diff), TimeUnit.MILLISECONDS.toSeconds(diff)%60));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });


        // Set Data..
        songTitleView.setText(songTitle);
        songArtistView.setText(songArtist);
        clickHandler();

//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                mp.reset();
//                try {
//                    mp.setDataSource(SongPlayingActivity.this, Uri.parse(mySongs.get(songPosition+1).getSongData()));
//                    mp.prepare();
//                    songTitleView.setText(mySongs.get(songPosition+1).getSongTitle());
//                    songArtistView.setText(mySongs.get(songPosition+1).getArtist());
//                    getAlbumArt(mySongs.get(songPosition+1).getSongData());
//                    startMusic();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    // Load Album Art..
    public void getAlbumArt(String songDta) {
        MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
        metaRetriver.setDataSource(songDta);
        try {
            byte[] art = metaRetriver.getEmbeddedPicture();
            Bitmap songImage = BitmapFactory
                    .decodeByteArray(art, 0, art.length);
            songImg.setImageBitmap(songImage);
        } catch (Exception e) {
            songImg.setImageResource(R.drawable.cover_img);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    // Shake Functionality..
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
                    SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
                    boolean isAllowed = prefs.getBoolean("feature", false);
                    if (isAllowed) {
                        nextMusic();
//                        Toast.makeText(this, "Shaked..", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Click Handler
    public void clickHandler() {
        playPauseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()) {
                    pauseMusic();
                }
                else
                    startMusic();
            }
        });

        nextImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMusic();
            }
        });

        previousImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousMusic();
            }
        });
    }

    public void pauseMusic() {
        if(mediaPlayer != null) {
            mediaPlayer.pause();
            playPauseImageButton.setBackgroundResource(R.drawable.icon_play);
        }
    }

    public void startMusic() {
        if(mediaPlayer != null) {
            mediaPlayer.start();
            updateThread();
            playPauseImageButton.setBackgroundResource(R.drawable.icon_pause);
        }
    }

    public void  playSong(int songIndex){
        // Play song
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(mySongs.get(songPosition).getSongData());
            mediaPlayer.prepare();
            mediaPlayer.start();
            // Displaying Song title
//            String songTitle = mySongs.get(songPosition).getSongTitle();
            songTitleView.setText(mySongs.get(songPosition).getSongTitle());
            songArtistView.setText(mySongs.get(songPosition).getArtist());

            // Changing Button Image to pause image
            playPauseImageButton.setBackgroundResource(R.drawable.icon_pause);

            updateThread();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nextMusic() {
        mediaPlayer.stop();
        mediaPlayer.release();
        songPosition = ((songPosition+1)%mySongs.size());
//                nowPlaying++;
//                position = ((position+1)%duration);
        Uri u = Uri.parse(mySongs.get(songPosition).getSongData());
        mediaPlayer = MediaPlayer.create(SongPlayingActivity.this, u);
//                sName = mySongs.get(position).getName();
        songTitleView.setText(mySongs.get(songPosition).getSongTitle());
        songArtistView.setText(mySongs.get(songPosition).getArtist());


        getAlbumArt(mySongs.get(songPosition).getSongData());
        startMusic();
    }

    public void previousMusic() {
        mediaPlayer.stop();
        mediaPlayer.release();
        songPosition = ((songPosition-1<0)?(mySongs.size()-1):(songPosition-1));
        Uri u = Uri.parse(mySongs.get(songPosition).getSongData());
        mediaPlayer = MediaPlayer.create(SongPlayingActivity.this, u);
//                sName = mySongs.get(position).getName();
        getAlbumArt(mySongs.get(songPosition).getSongData());
        songTitleView.setText(mySongs.get(songPosition).getSongTitle());
        songArtistView.setText(mySongs.get(songPosition).getArtist());
        startMusic();
    }

    public void updateThread() {
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while(mediaPlayer != null) {
                        thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int newPosition = mediaPlayer.getCurrentPosition();
                                int newDuration = mediaPlayer.getDuration();
                                seekBar.setMax(newDuration);
                                seekBar.setProgress(newPosition);
                                leftTime.setText(String.format("%d:%02d", TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getCurrentPosition()), TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getCurrentPosition())%60));
                                rightTime.setText(String.format("%d:%02d", TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition()), TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition())%60));
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    // Function to handle the event where the song completes playing

    public void onSongComplete() {

    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        Toast.makeText(this, ""+songPosition, Toast.LENGTH_SHORT).show();
        // no repeat or shuffle ON - play next song
        if(songPosition < (mySongs.size() - 1)){
            playSong(songPosition+ 1);
            songPosition = songPosition + 1;
        }else{
            // play first song
            playSong(0);
            songPosition = 0;
        }

    }

//    @Override
//    public void onDestroy(){
//        super.onDestroy();
//        mediaPlayer.release();
//    }
}
