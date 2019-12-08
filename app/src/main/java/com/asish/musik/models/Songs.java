package com.asish.musik.models;

import android.os.Parcel;
import android.os.Parcelable;


//public class Songs implements Parcelable {
public class Songs implements Parcelable {

    public long songId;
    long dateAdded;
    long albumArt;
    public String songTitle;
    public String artist;
    public String songData;

    public Songs(long songId, long dateAdded, long albumArt, String songTitle, String artist, String songData) {
        this.songId = songId;
        this.dateAdded = dateAdded;
        this.songTitle = songTitle;
        this.artist = artist;
        this.songData = songData;
        this.albumArt = albumArt;
    }

    public Songs(Parcel source) {

    }

    public long getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(long albumArt) {
        this.albumArt = albumArt;
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSongData() {
        return songData;
    }

    public void setSongData(String songData) {
        this.songData = songData;
    }

    @Override
    public int describeContents() {
        return 0;
    }
//
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(songId);
        parcel.writeString(songTitle);
        parcel.writeString(artist);
        parcel.writeString(songData);
        parcel.writeLong(dateAdded);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<Songs>() {


        @Override
        public Songs createFromParcel(Parcel source) {
            return new Songs(source);
        }

        @Override
        public Songs[] newArray(int size) {
            return new Songs[size];
        }
    };


}
