package com.tantv.vnradiotruyen.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tantv.vnradiotruyen.R;
import com.tantv.vnradiotruyen.model.TrackAudio;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

/**
 * Copyright @2015
 * Created by tantv on 25/09/2015.
 */
public class ListSongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private ArrayList<TrackAudio> mTrackAudios;
    private DisplayImageOptions mOptions;
    private ImageLoader mImageLoader;
    private Context mContext;

    OnClickListSongAdapter onClickListSongAdapter;

    public ListSongAdapter(OnClickListSongAdapter onClickListSongAdapter, ArrayList<TrackAudio> mTrackAudios, Context context) {
        this.onClickListSongAdapter = onClickListSongAdapter;
        this.mTrackAudios = mTrackAudios;
        this.mContext = context;
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
        mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_list_radio_play_small, parent, false);
        return new ViewHolderLisRadioPlay(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolderLisRadioPlay) {
            if (!"null".equals(mTrackAudios.get(position).getUrlImage()) && mTrackAudios.get(position).getUrlImage() != null) {
                mImageLoader.displayImage(mTrackAudios.get(position).getUrlImage(), ((ViewHolderLisRadioPlay) holder).mImgItemPlay, mOptions);
            } else {
                ((ViewHolderLisRadioPlay) holder).mImgItemPlay.setImageResource(R.drawable.icon_avatar_defaut3);
            }
            Log.d("tantv113", position + ": Url= : " + mTrackAudios.get(position).getUrlImage());

            ViewHolderLisRadioPlay viewHolder = (ViewHolderLisRadioPlay) holder;
            viewHolder.mRlItemListRadioPlay.setOnClickListener(this);
            viewHolder.mRlItemListRadioPlay.setTag(position);

            viewHolder.mRlItemListRadioPlay.setTag(-2, viewHolder.imgIqualizer);
            if (mTrackAudios.get(position).isChoice()) {
                viewHolder.mTvNamePlayTrack.setTextColor(Color.parseColor("#c6a42c"));
                viewHolder.mTvSinger.setText("N/A");
                viewHolder.mTvSinger.setTextColor(Color.parseColor("#70c6a42c"));
                viewHolder.mTvNamePlayTrack.setText(mTrackAudios.get(position).getTitle() + "");
                viewHolder.imgIqualizer.setVisibility(View.VISIBLE);


            } else {
                viewHolder.imgIqualizer.setVisibility(View.GONE);
                viewHolder.mTvSinger.setTextColor(Color.parseColor("#50000000"));
                viewHolder.mTvNamePlayTrack.setTextColor(Color.parseColor("#000000"));
                viewHolder.mTvNamePlayTrack.setText(mTrackAudios.get(position).getTitle() + "");

            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        return mTrackAudios.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return mTrackAudios.size();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlItemListRadioPlay: {
                int position = Integer.parseInt(v.getTag() + "");
                onClickListSongAdapter.onClickListSongAdapter(position);
                break;
            }
        }
    }


    public class ViewHolderLisRadioPlay extends RecyclerView.ViewHolder {
        private RelativeLayout mRlItemListRadioPlay;
        private TextView mTvNamePlayTrack;
        private ImageView mImgItemPlay;
        private TextView mTvSinger;
        private ImageView imgIqualizer;

        public ViewHolderLisRadioPlay(View v) {
            super(v);
            mRlItemListRadioPlay = (RelativeLayout) v.findViewById(R.id.rlItemListRadioPlay);
            mTvNamePlayTrack = (TextView) v.findViewById(R.id.tvNamePlayTrack);
            mImgItemPlay = (ImageView) v.findViewById(R.id.imgIconImagePlay);
            mTvSinger = (TextView) v.findViewById(R.id.tvSingerListPlay);
            imgIqualizer = (ImageView) v.findViewById(R.id.imgRecycleViewEqualizerSmall);
        }
    }

    public interface OnClickListSongAdapter {
        void onClickListSongAdapter(int position);
    }

}
