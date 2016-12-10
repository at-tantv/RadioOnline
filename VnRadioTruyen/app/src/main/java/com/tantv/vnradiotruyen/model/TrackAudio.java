package com.tantv.vnradiotruyen.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by tantv on 25/09/2015.
 */
@Data
@AllArgsConstructor
public class TrackAudio implements Serializable{
    private Integer id;
    private String title;
    private String titleTruyen;
    private String urlImage;
    private int size;
    private int viewType;
    private int duration;
    private boolean choice;

}
