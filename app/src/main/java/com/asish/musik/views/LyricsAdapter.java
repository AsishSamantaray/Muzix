package com.asish.musik.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.asish.musik.activities.LyricsActivity;
import com.asish.musik.databases.LyricsDatabase;
import com.asish.musik.fragments.SongPlayingFragment;
import com.asish.musik.models.Song1;

import java.util.ArrayList;
import java.util.Locale;

public class LyricsAdapter extends RecyclerView.Adapter<LyricsAdapter.MyViewHolder>  {

    ArrayList<Song1> songDetails = null;
    Context mContext = null;
    ArrayList<Song1> songs;
    LyricsDatabase lyricsDatabase;


    public LyricsAdapter(ArrayList<Song1> songDetails, Context mContext) {
        songs = new ArrayList<>();
        lyricsDatabase = new LyricsDatabase(mContext);
        this.songDetails = songDetails;
        this.mContext = mContext;
        songs.addAll(songDetails);
    }

    @NonNull
    @Override
    public LyricsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.lyrics_adapter, viewGroup, false);
        return new LyricsAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final LyricsAdapter.MyViewHolder holder, final int position) {
        final Song1 songObject = songDetails.get(position);

        holder.trackTitle.setText(songObject.songTitle);
        holder.trackArtist.setText(songObject.artist);

        holder.contentHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LyricsActivity.class);
                intent.putExtra("Lyrics", songObject.getLyrics());
                intent.putExtra("title", songObject.getSongTitle());
                intent.putExtra("artist", songObject.getArtist());
                intent.putExtra("from", "not internet");
                mContext.startActivity(intent);
            }
        });

        holder.contentHolder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle("Are you sure you want to delete??")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                lyricsDatabase.deleteFavourite(songObject.getSongTitle());
//                                removeItemFromList(songObject.getSongTitle());
                                songDetails.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position,songDetails.size());
                                notifyDataSetChanged();
                                Toast.makeText(mContext,"Successfully Deleted",Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
//                lyricsDatabase.deleteFavourite(songObject.getSongTitle());
                return true;
            }
        });

    }

    public void removeItemFromList(String obj) {

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
            for (Song1 s : songs) {
                if (s.getSongTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                    songDetails.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }

}
