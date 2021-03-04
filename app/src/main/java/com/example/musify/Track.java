package com.example.musify;

import java.io.Serializable;

public class Track implements Serializable {


    private String trackName;   /* Track name*/
    private String artistName;  /* Artists name*/
    private String path;        /* File path */
    private long id;            /* ID used to trace the file */
    private String size;        /* File size  */
    private String artworkpath; /* Artwork path*/

    /* private int duration etc..*/
    /* ......................... */


    // Constructors
    public Track() {
        this.trackName = "Unknown";
        this.artistName = "Unknown";
    }

    public Track(String trackName, String artistName, String path, long id) {
        this.trackName = trackName;
        this.artistName = artistName;
        this.path = path;
        this.id = id;
    }

    public Track(String trackName, String artistName, String path, long id, String artworkpath) {
        this.trackName = trackName;
        this.artistName = artistName;
        this.path = path;
        this.id = id;
        this.artworkpath = artworkpath;
    }


    // Getters - Setters
    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getID() {
        return id;
    }

    public void setID(long id) {
        this.id = id;
    }

    public String getArtworkPath() {
        return this.artworkpath;
    }

    public void setArtworkPath(String artworkpath) {
        this.artworkpath = artworkpath;
    }
}
