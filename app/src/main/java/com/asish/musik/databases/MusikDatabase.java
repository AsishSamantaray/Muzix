package com.asish.musik.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.asish.musik.models.Songs;

import java.util.ArrayList;

public class MusikDatabase extends SQLiteOpenHelper {

    ArrayList<Songs> _songsList = new ArrayList();


    static int DB_VERSION=13;
    static String DB_NAME="FavouriteDatabase";
    static String TABLE_NAME="FavouriteTable";
    static String COLUMN_ID="SongID";
    static String COLUMN_SONG_TITLE="SongTitle";
    static String COLUMN_SONG_ARTIST="SongArtist";
    static String COLUMN_SONG_PATH="SongPath";

    public MusikDatabase(@Nullable Context context,  @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MusikDatabase(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + MusikDatabase.TABLE_NAME + "( " + MusikDatabase.COLUMN_ID +
                " INTEGER," + MusikDatabase.COLUMN_SONG_ARTIST + " STRING," + MusikDatabase.COLUMN_SONG_TITLE + " STRING,"
                + MusikDatabase.COLUMN_SONG_PATH + " STRING);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void storeasFavourite(int id, String artist, String songTitle, String path) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MusikDatabase.COLUMN_ID, id);
        contentValues.put(MusikDatabase.COLUMN_SONG_ARTIST, artist);
        contentValues.put(MusikDatabase.COLUMN_SONG_TITLE, songTitle);
        contentValues.put(MusikDatabase.COLUMN_SONG_PATH, path);
        db.insert(MusikDatabase.TABLE_NAME, null, contentValues);
        db.close();
    }

    public ArrayList<Songs> queryDBforList() {
        try {

            SQLiteDatabase db = getReadableDatabase();
            String query_params = "SELECT " + "*" + " FROM " + MusikDatabase.TABLE_NAME;
            Cursor cSor = db.rawQuery(query_params, null);
            if (cSor.moveToFirst()) {
                do {
                    int _id = cSor.getInt(cSor.getColumnIndexOrThrow(MusikDatabase.COLUMN_ID));
                    String _artist = cSor.getString(cSor.getColumnIndexOrThrow(MusikDatabase.COLUMN_SONG_ARTIST));
                    String  _title = cSor.getString(cSor.getColumnIndexOrThrow(MusikDatabase.COLUMN_SONG_TITLE));
                    String _songPath = cSor.getString(cSor.getColumnIndexOrThrow(MusikDatabase.COLUMN_SONG_PATH));
                    _songsList.add(new Songs((long)_id, 0, 0, _title, _artist, _songPath));
                } while (cSor.moveToNext());
            } else {
                return null;
            }
        } catch ( Exception e) {
            e.printStackTrace();
        }
        return _songsList;
    }

    public int checkSize() {
        int counter = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query_params = "SELECT " + "*" + " FROM " + MusikDatabase.TABLE_NAME;
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

    public boolean checkifIdExists(int _id) {
        int storeId = -1090;
        SQLiteDatabase  db = this.getReadableDatabase();
        String query_params = "SELECT * FROM " + MusikDatabase.TABLE_NAME + " WHERE SongId = "+_id;
        Cursor cSor = db.rawQuery(query_params, null);
        if (cSor.moveToFirst()) {
            do {
                storeId = cSor.getInt(cSor.getColumnIndexOrThrow(MusikDatabase.COLUMN_ID));


            } while (cSor.moveToNext());
        } else {
            return false;
        }
        return storeId != -1090;
    }

    public void deleteFavourite(int _id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MusikDatabase.TABLE_NAME, MusikDatabase.COLUMN_ID + "=" + _id, null);
        db.close();
    }

}
