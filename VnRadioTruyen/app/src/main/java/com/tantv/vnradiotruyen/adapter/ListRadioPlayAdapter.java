package com.tantv.vnradiotruyen.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
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

public class ListRadioPlayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private OnClickListRadioPlayAdapter mOnClickListRadioPlayAdapter;
    private ArrayList<TrackAudio> mTrackAudios;
    private DisplayImageOptions mOptions;
    private ImageLoader mImageLoader;
    private Context mContext;
    private OnClickImageShareFace mOnClickImageShareFace;
    private OnClickImageFeedBack mOnClickImageFeedBack;

    public ListRadioPlayAdapter(OnClickListRadioPlayAdapter mOnClickListRadioPlayAdapter, ArrayList<TrackAudio> mTrackAudios, Context context,
                                OnClickImageShareFace mOnClickImageShareFace, OnClickImageFeedBack mOnClickImageFeedBack) {
        this.mOnClickListRadioPlayAdapter = mOnClickListRadioPlayAdapter;
        this.mTrackAudios = mTrackAudios;
        this.mContext = context;
        this.mOnClickImageShareFace = mOnClickImageShareFace;
        this.mOnClickImageFeedBack = mOnClickImageFeedBack;
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

        View v;
        if (viewType == 0) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_menu_top_play, parent, false);
            return new ViewHolderLisRadioPlayTop(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_list_radio_play, parent, false);
            return new ViewHolderLisRadioPlay(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 0) {
            if (holder instanceof ViewHolderLisRadioPlayTop) {
                if (mTrackAudios.get(position).getUrlImage() != null) {
                    mImageLoader.displayImage(mTrackAudios.get(position).getUrlImage(), ((ViewHolderLisRadioPlayTop) holder).mImgIconTitleBig, mOptions);
                } else
                ((ViewHolderLisRadioPlayTop) holder).mImgIconTitleBig.setImageResource(R.drawable.icon_music_top1);
                ViewHolderLisRadioPlayTop viewHolder = (ViewHolderLisRadioPlayTop) holder;
                viewHolder.mTvNamePlaylist.setText(mTrackAudios.get(position).getTitleTruyen() + "");
                viewHolder.mTvCountTrack.setText(mTrackAudios.get(position).getSize() + " Tracks");
                viewHolder.mImgShareFaceboook.setOnClickListener(this);
                viewHolder.mImgFeedBack.setOnClickListener(this);
            }
        } else {
            if (holder instanceof ViewHolderLisRadioPlay) {

                if (!"null".equals(mTrackAudios.get(position).getUrlImage()) && mTrackAudios.get(position).getUrlImage() != null) {
                    mImageLoader.displayImage(mTrackAudios.get(position).getUrlImage(), ((ViewHolderLisRadioPlay) holder).mImgItemPlay, mOptions);
                } else {
                    ((ViewHolderLisRadioPlay) holder).mImgItemPlay.setImageResource(R.drawable.icon_avatar_defaut3);
                }
                ViewHolderLisRadioPlay viewHolder = (ViewHolderLisRadioPlay) holder;

                viewHolder.mRlItemListRadioPlay.setOnClickListener(this);
                viewHolder.mRlItemListRadioPlay.setTag(position);

                if (mTrackAudios.get(position).isChoice()) {
                    viewHolder.mTvNamePlayTrack.setTextColor(Color.parseColor("#c6a42c"));
                    viewHolder.mTvNamePlayTrack.setText(mTrackAudios.get(position).getTitle() + "");
                    viewHolder.mTvSinger.setText("N/A");
                    viewHolder.mTvSinger.setTextColor(Color.parseColor("#70c6a42c"));
                    viewHolder.mImgEqualizer.setVisibility(View.VISIBLE);


                } else {
                    viewHolder.mTvSinger.setTextColor(Color.parseColor("#50000000"));
                    viewHolder.mTvSinger.setText("N/A");
                    viewHolder.mTvNamePlayTrack.setTextColor(Color.parseColor("#000000"));
                    viewHolder.mTvNamePlayTrack.setText(mTrackAudios.get(position).getTitle() + "");
                    viewHolder.mImgEqualizer.setVisibility(View.GONE);
                }
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
                mOnClickListRadioPlayAdapter.onClickRadioPlayAdapter(position);

                break;
            }
            case R.id.imgShareFacebook: {
                mOnClickImageShareFace.onClickShareFb();
                break;
            }
            case R.id.imgFeedBack: {
                mOnClickImageFeedBack.onClickFeedBack();
                break;
            }
        }
    }


    public class ViewHolderLisRadioPlay extends RecyclerView.ViewHolder {
        private RelativeLayout mRlItemListRadioPlay;
        private TextView mTvNamePlayTrack;
        private ImageView mImgItemPlay;
        private TextView mTvSinger;
        private ImageView mImgEqualizer;

        public ViewHolderLisRadioPlay(View v) {
            super(v);
            mRlItemListRadioPlay = (RelativeLayout) v.findViewById(R.id.rlItemListRadioPlay);
            mTvNamePlayTrack = (TextView) v.findViewById(R.id.tvNamePlayTrack);
            mImgItemPlay = (ImageView) v.findViewById(R.id.imgIconImagePlay);
            mTvSinger = (TextView) v.findViewById(R.id.tvSingerListPlay);
            mImgEqualizer = (ImageView) v.findViewById(R.id.imgRecycleViewEqualizer);

        }
    }

    public class ViewHolderLisRadioPlayTop extends RecyclerView.ViewHolder {
        private TextView mTvNamePlaylist;
        private ImageView mImgIconTitleBig;
        private TextView mTvCountTrack;
        private ImageView mImgShareFaceboook;
        private ImageView mImgFeedBack;

        public ViewHolderLisRadioPlayTop(View v) {
            super(v);
            mTvNamePlaylist = (TextView) v.findViewById(R.id.tvNamePlaylist);
            mImgIconTitleBig = (ImageView) v.findViewById(R.id.imgIconTitleBig);
            mTvCountTrack = (TextView) v.findViewById(R.id.tvCountTrack);
            mImgShareFaceboook = (ImageView) v.findViewById(R.id.imgShareFacebook);
            mImgFeedBack = (ImageView) v.findViewById(R.id.imgFeedBack);
        }
    }

    public interface OnClickListRadioPlayAdapter {
        void onClickRadioPlayAdapter(int position);
    }

    public interface OnClickImageShareFace {
        void onClickShareFb();
    }

    public interface OnClickImageFeedBack {
        void onClickFeedBack();
    }
}
