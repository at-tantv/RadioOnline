package com.tantv.vnradiotruyen.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tantv.vnradiotruyen.R;
import com.tantv.vnradiotruyen.activities.MainActivity_;
import com.tantv.vnradiotruyen.adapter.TruyenDemKhuyaAdapter;
import com.tantv.vnradiotruyen.model.ApplicationData;
import com.tantv.vnradiotruyen.model.ListRadioTruyen;
import com.tantv.vnradiotruyen.model.TrackAudio;
import com.tantv.vnradiotruyen.model.Truyen;
import com.tantv.vnradiotruyen.network.core.ApiClient;
import com.tantv.vnradiotruyen.network.core.Callback;
import com.tantv.vnradiotruyen.until.PlayerConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;

/**
 * Created by tantv on 24/09/2015.
 */
@EFragment(R.layout.fragment_blog_radio)
public class TruyenDemKhuyaFragment extends Fragment implements TruyenDemKhuyaAdapter.OnClickTruyenDemKhuyaAdapter {
    public static String ID = "59839543";
    public static String Client_ID = "96101e1b44233132493f54d2a2e26550";
    public static String Client_Secret = "cf258a243df77cbf045f1998c0573843";
    private ArrayList<Truyen> mTruyens;
    private ArrayList<ListRadioTruyen> mListRadioTruyens;
    private ArrayList<ArrayList<TrackAudio>> mTrackAudios;
    private OnClickTruyenDemKhuya onClickTruyenDemKhuya;
    @ViewById(R.id.recyclerViewListBlogRadio)
    RecyclerView mRecyclerViewListBlogRadio;
    private TruyenDemKhuyaAdapter adapter;
    DisplayMetrics displaymetrics;
    @ViewById(R.id.llViewSignalNoconnect)
    RelativeLayout mLlViewSignalNoconnect;


    @Click(R.id.btnRetry)
    void retryClick() {
        if (getActivity() instanceof MainActivity_) {
            ((MainActivity_) getActivity()).retryClick();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTruyens = new ArrayList<>();
        mTrackAudios = new ArrayList<>();
        mListRadioTruyens = new ArrayList<>();
    }

    boolean mIsRunningTimer;
    CountDownTimer mCountDownTimer;
    private ProgressDialog mProgressDialog;

    public void showWhenNoInternet() {
        mLlViewSignalNoconnect.setVisibility(View.VISIBLE);
    }

    public void hideWhenNoInternet() {
        mLlViewSignalNoconnect.setVisibility(View.GONE);
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @AfterViews
    void afterView() {
        if (!isNetworkConnected()) {
            showWhenNoInternet();
        } else {
            Log.d("Info: ", "AfterView");
            displaymetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            if (getActivity() instanceof MainActivity_) {
                onClickTruyenDemKhuya = ((MainActivity_) getActivity()).getOnClickTruyenDemKhuya();
            }
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Loading.....");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            new CountDownTimer(500, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    mProgressDialog.hide();

                    if (mTrackAudios.size() > 0) {
                        adapter.notifyDataSetChanged();
                        Log.d("Info: ", "Size >0");
                    } else {
                        if (getActivity() instanceof MainActivity_) {
                            mTruyens = ((MainActivity_) getActivity()).getMTruyens();
                            mListRadioTruyens = ((MainActivity_) getActivity()).getMListRadioTruyens();
                            mTrackAudios = ((MainActivity_) getActivity()).getMTrackAudios();
                        }
                    }
                    configRecycleView();
                    initRecyclerView();
                }
            }.start();

            mRecyclerViewListBlogRadio.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
    }


    private void initRecyclerView() {
        mRecyclerViewListBlogRadio.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewListBlogRadio.setLayoutManager(layoutManager);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        adapter = new TruyenDemKhuyaAdapter(width, height, this, mTruyens, getActivity());
        mRecyclerViewListBlogRadio.setAdapter(adapter);
    }

    private void configRecycleView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewListBlogRadio.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onClickAdapter(int position) {
        ArrayList<TrackAudio> mTracks = new ArrayList<>();
        String tenTruyen = mListRadioTruyens.get(position).getTitle().substring(5, mListRadioTruyens.get(position).getTitle().length());
        TrackAudio trackAudio = new TrackAudio(0, "", tenTruyen + "", mListRadioTruyens.get(position).getArtworkUrl(), mListRadioTruyens.get(position).getTrackCount(), 0, 0, false);
        mTracks.add(trackAudio);
        for (int i = 0; i < mTrackAudios.get(position).size(); i++) {
            mTracks.add(mTrackAudios.get(position).get(i));
        }
        onClickTruyenDemKhuya.onClickTruyenDemKhuya(position, mTracks);

        if (getActivity() instanceof MainActivity_) {
            ((MainActivity_) getActivity()).commitFragmentPlayList(mTracks, position);
        }
    }

    public interface OnClickTruyenDemKhuya {
        void onClickTruyenDemKhuya(int pos, ArrayList<TrackAudio> mTracks);
    }
}
