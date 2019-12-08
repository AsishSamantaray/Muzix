package com.asish.musik.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.asish.musik.ApiInterfaceLyrics;
import com.asish.musik.R;
import com.asish.musik.activities.LyricsActivity;
import com.asish.musik.activities.MainActivity;
import com.asish.musik.models.Result;
import com.asish.musik.models.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongFetch extends Fragment {

    TextView song_name;
    Button listen;
    Button analyze;
    Activity myActivity;
    private String outputFile;
    private MediaRecorder myAudioRecorder;
    ProgressDialog progressDialog;

    String songName;
    String artist;
    String lyrics1;

    public SongFetch() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_fetch, container, false);
        getActivity().setTitle("Muzix Voice");
        song_name = (TextView) view.findViewById(R.id.lyrics);
        listen = (Button) view.findViewById(R.id.listen);
        analyze = (Button) view.findViewById(R.id.analyze);
        analyze.setEnabled(false);
        listen.setEnabled(true);


        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myAudioRecorder != null) {
                    stopAudio();
                    Toast.makeText(myActivity, "Recording stopped", Toast.LENGTH_SHORT).show();
                }
                else{
                    listen.setBackgroundResource(R.drawable.micred);
                    recordAudio();
                }
            }
        });
        analyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myAudioRecorder != null) {
                    Toast.makeText(myActivity, "Recording, can't find song", Toast.LENGTH_SHORT).show();
                }
                else{
//                    song_name.setText("Analyzing!!!!");
                    // Set up progress before call
                    progressDialog = new ProgressDialog(myActivity);
                    progressDialog.setMax(100);
                    progressDialog.setTitle("Analyzing!!!");
                    progressDialog.setCancelable(false);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    // show it
                    progressDialog.show();
                    run();
                }
            }
        });
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
    private void recordAudio(){
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        Log.d("filename", outputFile);
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setOutputFile(outputFile);

        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        } catch (IllegalStateException ise) {
            // make something ...
            ise.printStackTrace();
        } catch (IOException ioe) {
            // make something
            ioe.printStackTrace();
        }
        Toast.makeText(myActivity, "Recording started", Toast.LENGTH_SHORT).show();

    }

    public void stopAudio(){

        if(myAudioRecorder != null){

            listen.setBackgroundResource(R.drawable.micpink);
            myAudioRecorder.stop();
            myAudioRecorder.release();
            myAudioRecorder = null;
            listen.setEnabled(false);
            analyze.setEnabled(true);
            Toast.makeText(myActivity, "Audio Recorded successfully", Toast.LENGTH_SHORT).show();

        }
    }

    private void run(){
        String url  = "https://api.audd.io";
        OkHttpClient client = new OkHttpClient();
        File file = new File(outputFile);
        MediaType media_type_mp3 = MediaType.parse("audio/mpeg");
        RequestBody data= new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("url", "https://audd.tech/")
                .addFormDataPart("file", file.getName(), RequestBody.create(media_type_mp3, file))
                .addFormDataPart("return", "lyrics")
                .addFormDataPart("api_token", "5a374601a235ee860bfe8498dae77e58")
                .build();

        Request request = new Request.Builder().url(url).post(data).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                progressDialog.dismiss();
                listen.setEnabled(true);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                progressDialog.dismiss();
                final String myResponse = response.body().string();
                jsonParse(myResponse);
                myActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        song_name.setText(artist_name);
                        progressDialog = new ProgressDialog(myActivity);
                        progressDialog.setMax(100);
                        progressDialog.setTitle("Analyzing!!!");
                        progressDialog.setCancelable(false);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        // show it
                        progressDialog.show();
                        getLyrics(songName);

                        listen.setEnabled(true);
                    }
                });
            }
        });
    }

    public void jsonParse(String responseData){

        try {
            JSONObject json = new JSONObject(responseData);
            String res = json.getString("result");
            JSONObject json1 = new JSONObject(res);
            artist = json1.getString("artist");
            songName = json1.getString("title");
            lyrics1 = songName+artist;

        } catch (JSONException e) {
//            lyrics1 =  "LyricsFetch not yet available";
//            return "Not found";
//            Toast.makeText(myActivity, "Not Found", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_search);
        if(item != null){
            item.setVisible(false);
        }
    }

    private void getLyrics(final String songName) {
        final ArrayList<String> ls = new ArrayList<>();
        final String res;
        //Creating a retrofit object
        final Retrofit[] retrofit = {new Retrofit.Builder()
                .baseUrl(ApiInterfaceLyrics.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()};

        // Create the API interface
        ApiInterfaceLyrics apiInterface = retrofit[0].create(ApiInterfaceLyrics.class);

        retrofit2.Call<Status> call = apiInterface.getLyrics(songName, "5a374601a235ee860bfe8498dae77e58");

        call.enqueue(new retrofit2.Callback<Status>() {
            @Override
            public void onFailure(retrofit2.Call<Status> call, Throwable t) {
                Toast.makeText(myActivity, t.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(retrofit2.Call<Status> call, retrofit2.Response<Status> response) {
                List<Result> lyrics = response.body().getResult();
                progressDialog.dismiss();
                if(songName != null   && artist != null && lyrics1 != null && response.body().getResult().size() != 0) {
                    Intent intent = new Intent(myActivity, LyricsActivity.class);
//                            intent.putExtra("Lyrics", );
                    intent.putExtra("title", songName);
                    intent.putExtra("artist", artist);
                    intent.putExtra("Lyrics", response.body().getResult().get(0).getLyrics());
                    intent.putExtra("from", "internet");
                    startActivity(intent);
                }
                else {
                    song_name.setText("Sorry Song is not available..");
                }

            }
        });

    }

}
