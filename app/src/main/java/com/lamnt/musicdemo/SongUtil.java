package com.lamnt.musicdemo;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class SongUtil {
    public static List<Song> getSong(Context context) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projections = new String[]{
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION
        };

        // Select *from audio where artist = 'Đan Trường' order by ASC

        Cursor cursor = context.getContentResolver().query(uri, projections,
                null, null, MediaStore.Audio.Media.TITLE + " ASC");

        List<Song> songs = new ArrayList<>();

        if (cursor == null) { // Query error
            return songs;
        }

        if (cursor.getCount() == 0) {
            cursor.close();
            return songs;
        }

        int indexData = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        int indexTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int indexAuthor = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int indexDuration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            String data = cursor.getString(indexData);
            String title = cursor.getString(indexTitle);
            String author = cursor.getString(indexAuthor);
            long time = cursor.getLong(indexDuration);

            songs.add(new Song(data, title, author, time));

            cursor.moveToNext();
        }

        cursor.close();
        return songs;
    }
}
