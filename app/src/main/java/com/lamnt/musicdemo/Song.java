package com.lamnt.musicdemo;

public class Song {
    private String data;
    private String name;
    private String artist;
    private int time;

    public Song(String data, String name, String artist,int time) {
        this.data = data;
        this.name = name;
        this.artist = artist;
        this.time = time;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Song{" +
                "data='" + data + '\'' +
                ", name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                '}';
    }
}
