
package com.asish.musik.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("song_id")
    @Expose
    private String songId;
    @SerializedName("artist_id")
    @Expose
    private String artistId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("title_with_featured")
    @Expose
    private String titleWithFeatured;
    @SerializedName("full_title")
    @Expose
    private String fullTitle;
    @SerializedName("artist")
    @Expose
    private String artist;
    @SerializedName("lyrics")
    @Expose
    private String lyrics;
    @SerializedName("media")
    @Expose
    private String media;

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleWithFeatured() {
        return titleWithFeatured;
    }

    public void setTitleWithFeatured(String titleWithFeatured) {
        this.titleWithFeatured = titleWithFeatured;
    }

    public String getFullTitle() {
        return fullTitle;
    }

    public void setFullTitle(String fullTitle) {
        this.fullTitle = fullTitle;
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

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

}
