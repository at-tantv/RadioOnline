package com.tantv.vnradiotruyen.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tantv.vnradiotruyen.R;
import com.tantv.vnradiotruyen.activities.PlayMusicActivity_;
import com.tantv.vnradiotruyen.adapter.ListSongAdapter;
import com.tantv.vnradiotruyen.model.TrackAudio;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

/**
 * Created by tantv on 16/10/2015.
 */
@EFragment(R.layout.fragment_list_songs)
public class ListSongFragment extends Fragment {
    @ViewById(R.id.recyclerViewListSongPlay)
    RecyclerView mRecyclerViewListSongPlay;
    ListSongAdapter listSongAdapter;
    ArrayList<TrackAudio> mTrackAudiosPlayMusic;

    public void notifyDataSetChange(){
        listSongAdapter.notifyDataSetChanged();
    }
    @AfterViews
    void afterView() {
        configRecycleView();
        mTrackAudiosPlayMusic = new ArrayList<>();
        if (getActivity() instanceof PlayMusicActivity_) {
            mTrackAudiosPlayMusic.removeAll(mTrackAudiosPlayMusic);
            mTrackAudiosPlayMusic.addAll(((PlayMusicActivity_) getActivity()).getMTrackAudiosPlayMusic());
            mTrackAudiosPlayMusic.remove(0);
        }
        initRecyclerView();
    }


    private void initRecyclerView() {
        mRecyclerViewListSongPlay.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewListSongPlay.setLayoutManager(layoutManager);
        listSongAdapter = new ListSongAdapter((ListSongAdapter.OnClickListSongAdapter) getActivity(), mTrackAudiosPlayMusic, getActivity());
        mRecyclerViewListSongPlay.setAdapter(listSongAdapter);
    }

    private void configRecycleView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewListSongPlay.setLayoutManager(linearLayoutManager);
    }


}
