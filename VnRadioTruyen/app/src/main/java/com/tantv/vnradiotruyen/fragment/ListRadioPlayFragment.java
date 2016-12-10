package com.tantv.vnradiotruyen.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tantv.vnradiotruyen.R;
import com.tantv.vnradiotruyen.activities.MainActivity_;
import com.tantv.vnradiotruyen.adapter.ListRadioPlayAdapter;
import com.tantv.vnradiotruyen.model.TrackAudio;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Copyright @2015
 * Created by tantv on 25/09/2015.
 */
@EFragment(R.layout.fragment_recyclerview_list_radio_play)
public class ListRadioPlayFragment extends Fragment implements ListRadioPlayAdapter.OnClickListRadioPlayAdapter, ListRadioPlayAdapter.OnClickImageFeedBack, ListRadioPlayAdapter.OnClickImageShareFace {

    OnClickItemListRadioFragment OnClickItemListRadioFragment;
    @Getter
    private int mPosition = 0;
    @FragmentArg
    int positionArgs;
    @FragmentArg
    ArrayList<TrackAudio> mTrackArgs;
    @ViewById(R.id.recyclerViewListRadioPlay)
    RecyclerView mRecyclerViewListRadioPlay;
    private ProgressDialog mProgressDialog;
    CountDownTimer mCountDownTimer;
    boolean mIsRunningTimer;

    @AfterViews
    void afterView() {

        configRecycleView();
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Loading.....");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        new CountDownTimer(500, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                mProgressDialog.hide();
                initRecyclerView();
                if (getActivity() instanceof MainActivity_) {
                    OnClickItemListRadioFragment = ((MainActivity_) getActivity()).getOnClickItemListRadioFragment();
                }

                mRecyclerViewListRadioPlay.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);

                        if ((newState == 0) && (MainActivity_.mIsClickedItem)) {
                            if (!mIsRunningTimer) {
                                mIsRunningTimer = true;
                                mCountDownTimer = new CountDownTimer(3000, 1000) {

                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                    }

                                    @Override
                                    public void onFinish() {
                                        mIsRunningTimer = false;
                                        if (getActivity() instanceof MainActivity_) {
                                            ((MainActivity_) getActivity()).showMiniPlayer();
                                        }
                                    }
                                }.start();
                            }
                        }
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (MainActivity_.mIsClicked) {
                            if (dy > 0) {
                                if (getActivity() instanceof MainActivity_) {
                                    ((MainActivity_) getActivity()).hideMiniPlayer();
                                }
                            } else if (dy < 0) {
                                if (getActivity() instanceof MainActivity_) {
                                    ((MainActivity_) getActivity()).showMiniPlayer();
                                }
                            } else {
                                if (getActivity() instanceof MainActivity_) {
                                    ((MainActivity_) getActivity()).showMiniPlayer();
                                }
                            }

                        }
                    }
                });
            }
        }.start();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    ListRadioPlayAdapter adapter;

    public void notifyDatasetChange() {
        adapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        mRecyclerViewListRadioPlay.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewListRadioPlay.setLayoutManager(layoutManager);
        adapter = new ListRadioPlayAdapter(this, mTrackArgs, getActivity(), this, this);
        mRecyclerViewListRadioPlay.setAdapter(adapter);
    }

    private void configRecycleView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewListRadioPlay.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onClickRadioPlayAdapter(int position) {
        OnClickItemListRadioFragment.onClickListRadioFragment(position);
        mPosition = position;
        for (int i = 0; i < mTrackArgs.size(); i++) {
            mTrackArgs.get(i).setChoice(false);
        }
        mTrackArgs.get(position).setChoice(true);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onClickFeedBack() {

        Intent intent1 = new Intent(Intent.ACTION_VIEW);
        intent1.setData(Uri.parse("http://goo.gl/forms/lLPcJm1eO4"));
        startActivity(intent1);
    }

    @Override
    public void onClickShareFb() {

        getShareIntent();
    }


    public interface OnClickItemListRadioFragment {
        void onClickListRadioFragment(int position);
    }

    private void getShareIntent() {
        String urlToShare = "https://play.google.com/store/apps/details?id=com.tantv.vnradiotruyen";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
        boolean facebookAppFound = false;
        List<ResolveInfo> matches =
                getActivity().getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if
                    (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook")) {
                intent.setPackage(info.activityInfo.packageName);
                facebookAppFound = true;
                break;
            }
        }
        if (!facebookAppFound) {
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" +
                    urlToShare;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
        }

        startActivity(intent);
    }
}
