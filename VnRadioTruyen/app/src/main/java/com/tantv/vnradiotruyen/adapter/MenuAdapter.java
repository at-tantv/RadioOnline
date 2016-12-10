package com.tantv.vnradiotruyen.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tantv.vnradiotruyen.R;
import com.tantv.vnradiotruyen.model.MenuObject;

import java.util.ArrayList;

/**
 * Created by tantv on 24/09/2015.
 */
public class MenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    ArrayList<MenuObject> mMenuObjects;
    OnclickMenuAdapter onclickMenuAdapter;

    public MenuAdapter(ArrayList<MenuObject> menuObjects, OnclickMenuAdapter onclickMenuAdapter) {
        this.mMenuObjects = menuObjects;
        this.onclickMenuAdapter = onclickMenuAdapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == 0) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_typemenu, parent, false);
            return new ViewHolderHeader(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_menu, parent, false);
            return new ViewHolder(v);
        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolder) {
            ViewHolder vhItem = (ViewHolder) holder;
            vhItem.mImgIcon.setImageResource(mMenuObjects.get(position).getMImgIcon());
            vhItem.mTxtName.setText(mMenuObjects.get(position).getMTxtName());
            vhItem.mRlItemMenu.setOnClickListener(this);
            vhItem.mRlItemMenu.setTag(position);
        } else {
            ViewHolderHeader vhHeader = (ViewHolderHeader) holder;
            vhHeader.mTvTitle.setText(mMenuObjects.get(position).getMTxtName());
            vhHeader.mRlItemMenuHeader.setOnClickListener(this);
            vhHeader.mRlItemMenuHeader.setTag(position);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return mMenuObjects.get(position).getType();
    }


    @Override
    public int getItemCount() {
        return mMenuObjects.size();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlItemMenu: {
                int position = Integer.parseInt(v.getTag()+"");
                onclickMenuAdapter.onClickMenu(position);
                break;
            }
            case R.id.rlItemMenuHeader: {
                int position = Integer.parseInt(v.getTag()+"");
                onclickMenuAdapter.onClickMenu(position);
                break;
            }
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImgIcon;
        private TextView mTxtName;
        private RelativeLayout mRlItemMenu;

        public ViewHolder(View v) {
            super(v);
            mImgIcon = (ImageView) v.findViewById(R.id.imgIcon);
            mTxtName = (TextView) v.findViewById(R.id.tvName);
            mRlItemMenu = (RelativeLayout) v.findViewById(R.id.rlItemMenu);
        }
    }

    public class ViewHolderHeader extends RecyclerView.ViewHolder {
        private RelativeLayout mRlItemMenuHeader;
        private TextView mTvTitle;

        public ViewHolderHeader(View v) {
            super(v);
            mTvTitle = (TextView) v.findViewById(R.id.tvTxtName);
            mRlItemMenuHeader = (RelativeLayout) v.findViewById(R.id.rlItemMenuHeader);
        }
    }

    public interface OnclickMenuAdapter {
        void onClickMenu(int position);
    }
}
