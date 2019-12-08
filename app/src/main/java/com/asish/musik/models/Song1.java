package com.asish.musik.models;

public class Song1 {

    public String songTitle;
    public String artist;
    public String lyrics;

    public Song1(String songTitle, String artist, String lyrics) {
        this.songTitle = songTitle;
        this.artist = artist;
        this.lyrics = lyrics;
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

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }
}
