package com.tantv.vnradiotruyen.network;

import com.tantv.vnradiotruyen.model.ListRadioTruyen;
import com.tantv.vnradiotruyen.network.core.Callback;

import java.util.List;

import retrofit.http.GET;

public interface Api {
    @GET("/users/59839543/playlists?client_id=e534ffbc2d474446c8538d23b1c7605c")
    void getPlaylistSoundClound(Callback<List<ListRadioTruyen>> listRadioTruyenCallback);
}
