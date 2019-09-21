package com.lamnt.musicdemo;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

public class SongPlayer {
    private SongState songState = SongState.IDLE;
    private MediaPlayer mediaPlayer;

    public void play(Context context, String data) {
        if (songState != SongState.IDLE) {
            return;
        }

        try {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(data);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Quy định luồng chạy

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC).build();

            mediaPlayer.setAudioAttributes(audioAttributes);


            mediaPlayer.prepare(); // Load từ local

            // mediaPlayer.seekTo(10000); // Chuyển bài nhạc đến giây thứ bao nhiêu
            //  mediaPlayer.prepareAsync(); // Load từ internet

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    songState = SongState.PLAYING;
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    songState = SongState.IDLE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            songState = SongState.IDLE;
        }
    }

    public void pause() {
        if (songState == SongState.PLAYING) {
            mediaPlayer.pause();
            songState = SongState.PAUSED;
        }
    }

    public void resume() {
        if (songState == SongState.PAUSED) {
            mediaPlayer.start();
            songState = SongState.PLAYING;
        }
    }

    public void stop() {
        if (songState != SongState.IDLE) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            songState = SongState.IDLE;
        }
    }

    public boolean isStateIdle(){
        return songState == SongState.IDLE;
    }

    private enum SongState {
        IDLE,
        PLAYING,
        PAUSED
    }
}
