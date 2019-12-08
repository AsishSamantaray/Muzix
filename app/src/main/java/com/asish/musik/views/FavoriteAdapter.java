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

import com.asish.musik.R;
import com.asish.musik.models.Songs;
import com.asish.musik.fragments.SongPlayingFragment;

import java.util.ArrayList;
import java.util.Locale;

public class FavoriteAdapter  extends RecyclerView.Adapter<FavoriteAdapter.MyViewHolder> {

    ArrayList<Songs> songDetails = null;
    Context mContext = null;
    ArrayList<Songs> songs;

    public FavoriteAdapter(ArrayList<Songs> songDetails, Context mContext) {
        songs = new ArrayList<>();
        this.songDetails = songDetails;
        this.mContext = mContext;
        songs.addAll(songDetails);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_mainscreen_adapter, viewGroup, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final Songs songObject = songDetails.get(position);

        holder.trackTitle.setText(songObject.songTitle);
        holder.trackArtist.setText(songObject.artist);

        holder.contentHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SongPlayingFragment songPlayingFragment = new  SongPlayingFragment();
                Bundle args= new Bundle();
                args.putString("songArtist",songObject.artist);
                args.putString("path",songObject.songData);
                args.putString("songTitle",songObject.songTitle);
                args.putInt("SongId",(int)songObject.songId);
                args.putInt("songPosition",position);
                args.putParcelableArrayList("songData",songDetails);
                songPlayingFragment.setArguments(args);
                ((FragmentActivity) mContext).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.details_fragment,songPlayingFragment)
                        .addToBackStack("SongPlayingFragmentfavorite") //u missing
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        if(songDetails == null) {
            return 0;
        }
        else {
            return songDetails.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView trackTitle = null;
        TextView trackArtist = null;
        RelativeLayout contentHolder = null;

        MyViewHolder(@NonNull View view) {
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
