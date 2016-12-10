package com.tantv.vnradiotruyen.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tantv.vnradiotruyen.R;
import com.tantv.vnradiotruyen.model.Truyen;

import java.util.ArrayList;

/**
 * Copyright @2015
 * Created by tantv on 24/09/2015.
 */
public class TruyenDemKhuyaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private OnClickTruyenDemKhuyaAdapter mOnClickTruyenDemKhuyaAdapter;
    private ArrayList<Truyen> mTruyens;
    private DisplayImageOptions mOptions;
    private ImageLoader mImageLoader;
    private Context mContext;
    private int mWidthScreen;
    private int mHeightScreen;
    public TruyenDemKhuyaAdapter(int w,int h,OnClickTruyenDemKhuyaAdapter mOnClickTruyenDemKhuyaAdapter, ArrayList<Truyen> mTruyens, Context context) {
        this.mWidthScreen = w;
        this.mHeightScreen = h;
        this.mOnClickTruyenDemKhuyaAdapter = mOnClickTruyenDemKhuyaAdapter;
        this.mTruyens = mTruyens;
        this.mContext = context;
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
        mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        Log.d("hehe",mWidthScreen+","+mHeightScreen);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_truyen_dem_khuya, parent, false);
        return new ViewHolderBlogRadio(v);
    }
    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ViewHolderBlogRadio viewHolderBlogRadio = (ViewHolderBlogRadio) holder;
        viewHolderBlogRadio.mTvNameRadio.setText(mTruyens.get(position).getName() + "");
        viewHolderBlogRadio.mTvAuthorItemBlog.setText(mTruyens.get(position).getAuthor() + "");
        Bitmap largeIcon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_truyen_avatar);
        Log.d("hehe","baby1"+"");
        ImageSize targetSize = new ImageSize(50, 50);
        if (!mTruyens.get(position).getUrlImage().equals("null") && mTruyens.get(position).getUrlImage() !=null) {
            Log.d("hehe", "!baby" + "" + mTruyens.get(position).getUrlImage());
            //mImageLoader.displayImage(mTruyens.get(position).getUrlImage(),((ViewHolderBlogRadio) holder).mImgIconBlogRadio,mOptions);
            mImageLoader.loadImage(mTruyens.get(position).getUrlImage(), targetSize, mOptions, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            ((ViewHolderBlogRadio) holder).mImgIconBlogRadio.setImageBitmap(scaleDown(loadedImage,100,true));
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        } else {
                ((ViewHolderBlogRadio) holder).mImgIconBlogRadio.setImageBitmap(largeIcon);
        }
        viewHolderBlogRadio.mRlMenuBlog.setOnClickListener(this);
        viewHolderBlogRadio.mRlMenuBlog.setTag(position);

    }

    @Override
    public int getItemCount() {
        return mTruyens.size();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlMenuBlog: {
                int position = Integer.parseInt(v.getTag() + "");
                mOnClickTruyenDemKhuyaAdapter.onClickAdapter(position);
                break;
            }
        }

    }

    public class ViewHolderBlogRadio extends RecyclerView.ViewHolder {
        private RelativeLayout mRlMenuBlog;
        private ImageView mImgIconBlogRadio;
        private TextView mTvNameRadio;
        private TextView mTvAuthorItemBlog;

        public ViewHolderBlogRadio(View v) {
            super(v);
            mRlMenuBlog = (RelativeLayout) v.findViewById(R.id.rlMenuBlog);
            mImgIconBlogRadio = (ImageView) v.findViewById(R.id.imgIconTruyenDemKhuya);
            mTvNameRadio = (TextView) v.findViewById(R.id.tvNameRadio);
            mTvAuthorItemBlog = (TextView) v.findViewById(R.id.tvAuthorItemBlog);

        }
    }

    public interface OnClickTruyenDemKhuyaAdapter {
        void onClickAdapter(int position);
    }
}

