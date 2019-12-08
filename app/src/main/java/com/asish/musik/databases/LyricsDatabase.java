package com.asish.musik.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.asish.musik.models.Song1;

import java.util.ArrayList;

public class LyricsDatabase  extends SQLiteOpenHelper {

    ArrayList<Song1> _songsList = new ArrayList();


    static int DB_VERSION=13;
    static String DB_NAME="LyricsDatabase";
    static String TABLE_NAME="LyricsTable";
    static String COLUMN_SONG_TITLE="SongTitle";
    static String COLUMN_SONG_ARTIST="SongArtist";
    static String COLUMN_LYRICS="Lyrics";

    public LyricsDatabase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public LyricsDatabase(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "( "  + COLUMN_SONG_TITLE + " STRING," + COLUMN_SONG_ARTIST+ " STRING,"
                + COLUMN_LYRICS + " STRING);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int checkSize() {
        int counter = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query_params = "SELECT " + "*" + " FROM " + TABLE_NAME;
        Cursor cSor = db.rawQuery(query_params, null);
        if (cSor.moveToFirst()) {
            do {
                counter++;
            } while (cSor.moveToNext());
        } else {
            return 0;
        }
        return counter;
    }


    public void storeLyrics(String artist, String songTitle, String lyrics) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SONG_TITLE, songTitle);
        contentValues.put(COLUMN_SONG_ARTIST, artist);
        contentValues.put(COLUMN_LYRICS, lyrics);
        db.insert(TABLE_NAME, null, contentValues);
        db.close();
    }

    public ArrayList<Song1> queryDBforList() {
        try {

            SQLiteDatabase db = getReadableDatabase();
            String query_params = "SELECT " + "*" + " FROM " + TABLE_NAME;
            Cursor cSor = db.rawQuery(query_params, null);
            if (cSor.moveToFirst()) {
                do {
                    String  _title = cSor.getString(cSor.getColumnIndexOrThrow(COLUMN_SONG_TITLE));
                    String _artist = cSor.getString(cSor.getColumnIndexOrThrow(COLUMN_SONG_ARTIST));
                    String _songPath = cSor.getString(cSor.getColumnIndexOrThrow(COLUMN_LYRICS));
                    _songsList.add(new Song1(_title, _artist,  _songPath));
                } while (cSor.moveToNext());
            } else {
                return null;
            }
        } catch ( Exception e) {
            e.printStackTrace();
        }
        return _songsList;
    }

    public boolean checkifIdExists(String songName) {
        int storeId = -1090;
        SQLiteDatabase  db = this.getReadableDatabase();
        String query_params = "SELECT * FROM " + TABLE_NAME + " WHERE SongTitle =  '" + songName + "'";
        Cursor cSor = db.rawQuery(query_params, null);
        if (cSor.moveToFirst()) {
            do {
                storeId = cSor.getInt(cSor.getColumnIndexOrThrow(COLUMN_SONG_TITLE));


            } while (cSor.moveToNext());
        } else {
            return false;
        }
        return storeId != -1090;
    }

    public void deleteFavourite(String songName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_SONG_TITLE + "= '" + songName + "'", null);
        db.close();
    }
}
