package com.tantv.vnradiotruyen.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tantv.vnradiotruyen.App;
import com.tantv.vnradiotruyen.R;
import com.tantv.vnradiotruyen.adapter.ListSongAdapter;
import com.tantv.vnradiotruyen.adapter.PlayMusicFragmentAdapter;
import com.tantv.vnradiotruyen.fragment.ListSongFragment_;
import com.tantv.vnradiotruyen.fragment.MainControllerFragment_;
import com.tantv.vnradiotruyen.model.TrackAudio;
import com.tantv.vnradiotruyen.until.PlayerConstants;
import com.tantv.vnradiotruyen.until.UtilFunctions;
import com.squareup.otto.Produce;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import lombok.Getter;

/**
 * Copyright @2015
 * Created by tantv on 28/09/2015.
 */
@EActivity(R.layout.activity_play_music)
public class PlayMusicActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, ListSongAdapter.OnClickListSongAdapter {
    private int mProgress;
    private Intent mIntentPlayMusic;
    private String mNameSong;
    @ViewById(R.id.seekBarPlayMusic)
    SeekBar mSeekBarPlayMusic;

    @ViewById(R.id.tvTimeSong)
    TextView mTvTimeSong;
    @ViewById(R.id.tvSongName)
    TextView mTvSongName;
    @ViewById(R.id.tvTimer)
    TextView mTvTimer;

    @ViewById(R.id.imgPlayMusic)
    static ImageView mImgPlayMusic;
    @Getter
    ArrayList<TrackAudio> mTrackAudiosPlayMusic;
    static boolean mIsButtonPlayClicked;

    @AfterViews
    void afterView() {

        mTrackAudiosPlayMusic = new ArrayList<>();
        mSeekBarPlayMusic.setOnSeekBarChangeListener(this);
        getSupportActionBar().hide();
        mTvTimeSong.setText("--:--");
        mTvTimer.setText("--:--");
        mTvSongName.setText("");
        mIntentPlayMusic = new Intent();
        mIntentPlayMusic.setAction("com.tantv.vnradiotruyen.activities");
        mImgPlayMusic.setSelected(true);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mIsButtonPlayClicked = extras.getBoolean("EXTRA_BUTTON_PLAY");
        }
        if (!mIsButtonPlayClicked) {
            mImgPlayMusic.setSelected(false);
        } else {
            mImgPlayMusic.setSelected(true);
        }
    }


    @ViewById(R.id.viewPagerFragmentController)
    ViewPager mViewPagerFragmentController;

    ArrayList<Fragment> mFragments;

    @ViewById(R.id.viewPageChange1)
    View mViewPageChange1;

    @ViewById(R.id.viewPageChange2)
    View mViewPageChange2;


    void initViewPager() {
        mFragments = new ArrayList<>();
        mFragments.add(new ListSongFragment_());
        mFragments.add(new MainControllerFragment_());
        PlayMusicFragmentAdapter playMusicFragmentAdapter = new PlayMusicFragmentAdapter(getSupportFragmentManager(), mFragments);
        mViewPagerFragmentController.setAdapter(playMusicFragmentAdapter);
        mViewPagerFragmentController.setCurrentItem(1);
        mViewPagerFragmentController.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mViewPageChange2.setBackgroundColor(Color.BLACK);
                    mViewPageChange1.setBackgroundColor(Color.parseColor("#6b0b56"));
                } else {
                    mViewPageChange1.setBackgroundColor(Color.BLACK);
                    mViewPageChange2.setBackgroundColor(Color.parseColor("#6b0b56"));

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    void setTitleSong(String title) {
        mTvSongName.setSelected(true);
        mTvSongName.setText(title);
    }

    public static class NotifiBroad extends BroadcastReceiver {
        public NotifiBroad() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(PlayMusicService.NOTIFY_PAUSE)) {
                if (mImgPlayMusic != null) {
                    mIsButtonPlayClicked = !mIsButtonPlayClicked;
                    if (!mIsButtonPlayClicked) {
                        mImgPlayMusic.setSelected(false);
                    } else {
                        mImgPlayMusic.setSelected(true);
                    }
                }
            }
        }
    }


    @Override
    protected void onResume() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            boolean data1 = extras.getBoolean("data 1");
            if (data1) {

                if (App.mIsCheckActivityRunning == 2) {
                    finish();
                    Intent intent = new Intent(getApplicationContext(), MainActivity_.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
            }
        }
        try {
            PlayerConstants.PROGRESSBAR_HANDLER = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    PlayMusicService.SendDataFromService sendDataFromService = (PlayMusicService.SendDataFromService) msg.obj;
                    Integer i[] = sendDataFromService.a;
                    if (mTrackAudiosPlayMusic.size() == 0) {
                        mTrackAudiosPlayMusic.removeAll(mTrackAudiosPlayMusic);
                        mTrackAudiosPlayMusic.addAll(sendDataFromService.mTrackAudios);
                        initViewPager();
                    }
                    if (mNameSong == null) {
                        mNameSong = sendDataFromService.mTrackAudios.get(i[3]).getTitle();
                        setTitleSong(mNameSong + "");
                        mPosition = i[6];
                        /*//playing
                        if (i[5] == 1) {
                            mImgPlayMusic.setSelected(true);
                        } else {
                            mImgPlayMusic.setSelected(false);
                        }*/
                    }
                    if (mIsTouchProgressChange) {
                        mSeekBarPlayMusic.setProgress(i[1]);
                    }
                    mSeekBarPlayMusic.setSecondaryProgress(i[2]);
                    mTvTimeSong.setText(UtilFunctions.getDuration(i[4]) + "");
                    mTvTimer.setText(UtilFunctions.getDuration(i[0]) + "");

                }

            };
        } catch (Exception e) {
        }
        try {
            PlayerConstants.SEND_TITLE_SONG = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    String s[] = (String[]) msg.obj;
                    setTitleSong(s[0]);
                    mImgPlayMusic.setSelected(true);
                    int pos = Integer.parseInt(s[1]);
                    for (int i = 0; i < mTrackAudiosPlayMusic.size(); i++) {
                        mTrackAudiosPlayMusic.get(i).setChoice(false);
                    }
                    mTrackAudiosPlayMusic.get(pos).setChoice(true);
                    if (mFragments.get(0) instanceof ListSongFragment_) {
                        ((ListSongFragment_) mFragments.get(0)).notifyDataSetChange();
                    }

                }
            };
        } catch (Exception e) {
        }
        try {
            PlayerConstants.SEND_CALL_PHONE = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    String s[] = (String[]) msg.obj;
                    if ("1".equals(s[0])) {
                        mIsButtonPlayClicked = true;
                        mImgPlayMusic.setSelected(false);
                        mIntentPlayMusic.putExtra("playMusicActivity", 11);
                        sendBroadcast(mIntentPlayMusic);
                    }

                }
            };
        } catch (Exception e) {
        }

        /*try {
            PlayerConstants.SEND_TIMER = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    String s = (String) msg.obj;
                    int time = 0;
                    try {
                        time = Integer.parseInt(s) / 1000;
                    } catch (Exception e) {
                    }
                    if (time == 3) {
                        mImgPlayMusic.setSelected(false);
                        return;
                    }
                }
            };
        } catch (Exception e) {
        }*/
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // BusHolder.get().post(sendData());
        mIntentPlayMusic.putExtra("playMusicActivity", 15);
        mIntentPlayMusic.putExtra("playMusicActivityMPosition", mPosition);
        sendBroadcast(mIntentPlayMusic);
    }


    @Click(R.id.imgPlayMusic)
    void playClicked() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        mIsButtonPlayClicked = !mIsButtonPlayClicked;
        if (!mIsButtonPlayClicked) {
            mImgPlayMusic.setSelected(false);
        } else {
            mImgPlayMusic.setSelected(true);
        }
        mIntentPlayMusic.putExtra("playMusicActivity", 11);
        sendBroadcast(mIntentPlayMusic);
    }

    @Click(R.id.imgPreviousPlayMusic)
    void previousMusicClicked() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        mIntentPlayMusic.putExtra("playMusicActivity", 12);
        sendBroadcast(mIntentPlayMusic);
    }

    private long mLastClickTime = 0;

    @Click(R.id.imgNextPlayMusic)
    void nextMusicClicked() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        mIntentPlayMusic.putExtra("playMusicActivity", 13);
        sendBroadcast(mIntentPlayMusic);

    }

    boolean mIsTouchProgressChange = true;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mProgress = progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mIsTouchProgressChange = false;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mIntentPlayMusic.putExtra("playMusicActivity", 14);
        mIntentPlayMusic.putExtra("progressSeekbar", mProgress);
        sendBroadcast(mIntentPlayMusic);

        mIsTouchProgressChange = true;
    }

    @Click(R.id.imgPlayMusicBack)
    void buttonBackClicked() {
        finish();
    }

    private int mPosition;

    @Override
    public void onClickListSongAdapter(int position) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        position += 1;
        mPosition = position;

        mIntentPlayMusic.putExtra("positionMusicActivity", position);
        mIntentPlayMusic.putExtra("playMusicActivity", 16);
        sendBroadcast(mIntentPlayMusic);
    }

    @Produce
    public ArrayList<TrackAudio> sendData() {
        return mTrackAudiosPlayMusic;
    }

    @Override
    protected void onDestroy() {
        // BusHolder.get().post(sendData());
        super.onDestroy();
    }
}
