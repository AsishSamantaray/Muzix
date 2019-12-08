package com.asish.musik.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asish.musik.R;
import com.asish.musik.models.Songs;
import com.asish.musik.fragments.SongPlayingFragment;

import java.util.ArrayList;
import java.util.Locale;

public class MainScreenAdapter extends RecyclerView.Adapter<MainScreenAdapter.MyViewHolder> {
    Context context;
    ArrayList<Songs> songDetails;
    ArrayList<Songs> songs;

    public MainScreenAdapter(Context context, ArrayList<Songs> songDetails) {
        this.context = context;
        this.songDetails = songDetails;
        songs = new ArrayList<>();
        songs.addAll(songDetails);
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.row_mainscreen_adapter, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {

        final Songs songObject = songDetails.get(i);
        myViewHolder.trackTitle.setText(songObject.getSongTitle());
        myViewHolder.trackArtist.setText(songObject.getArtist());

        myViewHolder.contentHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Hey - "+songObject.songTitle, Toast.LENGTH_LONG).show();
//                Intent args = new Intent(context, SongPlayingActivity.class);
                SongPlayingFragment songPlayingFragment = new SongPlayingFragment();
                Bundle args = new Bundle();
                args.putString("songArtist", songObject.getArtist());
                args.putString("path", songObject.getSongData());
                args.putString("songTitle", songObject.getSongTitle());
                args.putLong("SongId", songObject.getSongId());
                args.putInt("songPosition", i);
                args.putParcelableArrayList("songData", songDetails);
                songPlayingFragment.setArguments(args);

                ((FragmentActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.details_fragment, songPlayingFragment)
                        .addToBackStack("SongPlayingFragment")
                        .commit();
//                context.startActivity(args);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (songDetails == null) {
            return  0;
        } else {
            return songDetails.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView trackTitle;
        TextView trackArtist;
        RelativeLayout contentHolder;

        public MyViewHolder(@NonNull View view) {
            super(view);
            trackTitle = view.findViewById(R.id.trackTitle);
            trackArtist = view.findViewById(R.id.trackArtist);
            contentHolder = view.findViewById(R.id.contentRow);
        }
    }

    // Filter method
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        songDetails.clear();
        if (charText.length() == 0) {
            songDetails.addAll(songs);
        }
        else {
            for (Songs s : songs) {
                if (s.getSongTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                    songDetails.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }

}
