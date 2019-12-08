package com.asish.musik.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.asish.musik.ApiInterfaceLyrics;
import com.asish.musik.R;
import com.asish.musik.activities.LyricsActivity;
import com.asish.musik.models.Result;
import com.asish.musik.models.Status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class LyricsFetch extends Fragment {

   static Activity myActivity2;
    Button analyze;
//    TextView song_name;
    EditText lyrics_here;


    public LyricsFetch() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lyrics, container, false);
        getActivity().setTitle("Muzix LyricsFetch");
        analyze = (Button) view.findViewById(R.id.search_for);
        lyrics_here= (EditText) view.findViewById(R.id.lyricshere);

        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_search);
        if(item != null) {
            item.setVisible(false);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        analyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = lyrics_here.getText().toString();
                if(TextUtils.isEmpty(query)){
                    Toast.makeText(myActivity2, "Enter some lines of a song..", Toast.LENGTH_SHORT).show();
                }
                else {
//                    song_name.setText("Analyzing!!!");

                        run(query);

//                        song_name.setText("Error Occured...");

                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myActivity2 = (Activity) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        myActivity2 = activity;

    }


    private void run(String songName)  {
        //Creating a retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiInterfaceLyrics.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create the API interface
        ApiInterfaceLyrics apiInterface = retrofit.create(ApiInterfaceLyrics.class);

        retrofit2.Call<Status> call = apiInterface.getLyrics(songName, "5a374601a235ee860bfe8498dae77e58");

        // Set up progress before call
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(myActivity2);
        progressDialog.setMax(100);
//        progressDialog.setMessage("Its loading....");
        progressDialog.setTitle("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // show it
        progressDialog.show();

        call.enqueue(new Callback<Status>() {
            @Override
            public void onFailure(retrofit2.Call<Status> call, Throwable t) {
                Toast.makeText(myActivity2, t.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call<Status> call, Response<Status> response) {
                List<Result> lyrics = response.body().getResult();


//                Toast.makeText(myActivity2, response.body().getStatus(), Toast.LENGTH_LONG).show();

                ArrayList<String> ls = new ArrayList<>();
                ArrayList<String> titles = new ArrayList<>();
                ArrayList<String> artists = new ArrayList<>();

                for(int i=0; i<lyrics.size(); i++) {
                    ls.add(lyrics.get(i).getLyrics());
                    titles.add(lyrics.get(i).getTitle());
                    artists.add(lyrics.get(i).getArtist());
                }
                progressDialog.dismiss();

                Intent intent = new Intent(myActivity2, LyricsActivity.class);
                intent.putExtra("Lyrics", ls.get(0));
                intent.putExtra("title", titles.get(0));
                intent.putExtra("artist", artists.get(0));
                intent.putExtra("from", "internet");

                startActivity(intent);

//                new AlertDialog.Builder(myActivity2)
//                        .setTitle(etSongName.getText().toString())
//                        .setMessage(ls.get(0))
//                        .show();
            }
        });
    }

}
