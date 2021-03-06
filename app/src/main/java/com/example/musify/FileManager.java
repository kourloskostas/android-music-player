package com.example.musify;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class FileManager {


    Context context;

    public FileManager(Context context) {

        this.context = context;

    }


    public List<Track> getTracks() {

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";

        ArrayList<Track> allTracks = new ArrayList<Track>();

        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String artworkpath = String.valueOf(Uri.parse("content://media/external/audio/media/" + id + "/albumart"));

                    /* TODO CHECK IF ARTWORK EXISTS ,
                    *   IF NOT SET ARTWORK PATH DEFAULT
                    if(){
                        artworkpath = "default";
                    }
                    */


                    allTracks.add(new Track(name, artist, path, id, artworkpath));

                } while (cursor.moveToNext());

            }
            cursor.close();
        }


        return allTracks;
    }


}
