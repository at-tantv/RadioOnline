package com.tantv.vnradiotruyen.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ListRadioTruyen implements Serializable{
    @SerializedName("genre")
    @Expose
    private String genre;
    @SerializedName("track_count")
    @Expose
    private Integer trackCount;

    @SerializedName("tracks")
    @Expose
    private List<Track> tracks = new ArrayList<>();

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("artwork_url")
    @Expose
    private String artworkUrl;

    @Data
    public class Track {

        @SerializedName("id")
        @Expose
        private Integer id;

        @SerializedName("title")
        @Expose
        private String title;

        @SerializedName("artwork_url")
        @Expose
        private String artworkUrl;

        @SerializedName("duration")
        @Expose
        private int duration;

    }

}


