package com.tantv.vnradiotruyen.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tantv.vnradiotruyen.App;
import com.tantv.vnradiotruyen.R;
import com.tantv.vnradiotruyen.adapter.MenuAdapter;
import com.tantv.vnradiotruyen.fragment.ListRadioPlayFragment;
import com.tantv.vnradiotruyen.fragment.ListRadioPlayFragment_;
import com.tantv.vnradiotruyen.fragment.TruyenDemKhuyaFragment;
import com.tantv.vnradiotruyen.fragment.TruyenDemKhuyaFragment_;
import com.tantv.vnradiotruyen.model.ApplicationData;
import com.tantv.vnradiotruyen.model.BusHolder;
import com.tantv.vnradiotruyen.model.ListRadioTruyen;
import com.tantv.vnradiotruyen.model.MenuObject;
import com.tantv.vnradiotruyen.model.TrackAudio;
import com.tantv.vnradiotruyen.model.Truyen;
import com.tantv.vnradiotruyen.network.core.ApiClient;
import com.tantv.vnradiotruyen.network.core.Callback;
import com.tantv.vnradiotruyen.until.PlayerConstants;
import com.tantv.vnradiotruyen.until.UtilFunctions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.squareup.otto.Produce;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import retrofit.RetrofitError;

@EActivity(R.layout.activity_main_copy)
public class MainActivity extends AppCompatActivity implements MenuAdapter.OnclickMenuAdapter,
        ListRadioPlayFragment.OnClickItemListRadioFragment, TruyenDemKhuyaFragment.OnClickTruyenDemKhuya {
    private ArrayList<MenuObject> mMenuObjects;
    private FragmentManager fragmentManager;
    private static Message msg;
    private static Messenger mService = null;
    private static Context mContext;
    private ArrayList<TrackAudio> mDataTrackAudio;
    private ArrayList<TrackAudio> mTempDataTrackAudio;
    private String mNameSong;
    @ViewById(R.id.tvTitleSong)
    TextView mTvTitleSong;
    @ViewById(R.id.tvTitle)
    TextView mTvTitle;
    @ViewById(R.id.imgSetupTimer)
    ImageView mImgSetupTimer;

    @ViewById(R.id.tvTimerSetUp)
    TextView mTvTimerSetUp;

    @ViewById(R.id.progressBarTimer)
    ProgressBar mProgressBarTimer;
    @Getter
    TruyenDemKhuyaFragment.OnClickTruyenDemKhuya onClickTruyenDemKhuya;
    private Intent playIntent;
    @Getter
    private ListRadioPlayFragment.OnClickItemListRadioFragment onClickItemListRadioFragment;

    @ViewById(R.id.recyclerViewMenu)
    RecyclerView mRecyclerViewMenu;

    @Getter
    int mPositionMenuClicked = 1;

    @ViewById(R.id.myDrawerLayout)
    DrawerLayout mDrawerLayout;

    @ViewById(R.id.flListPlay)
    FrameLayout mFlListPlay;

    @ViewById(R.id.llMenuController)
    RelativeLayout mLlMenuController;

    @ViewById(R.id.tvLoadingData)
    TextView mTvLoadingData;

    @ViewById(R.id.tvTimerSetUpLeft)
    TextView mTvTimerSetUpLeft;

    @SuppressLint("HandlerLeak")
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

        }
    }

    private static ServiceConnection
            musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("anhtt", "Service Disconnect");
        }
    };
    private int mSeekBarTimer;

    @Click(R.id.imgSetupTimer)
    void timerClicked() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_seebar_dialog, (ViewGroup) findViewById(R.id.rlSeebar));
        final TextView mTvTimerAfterMinutes = (TextView) layout.findViewById(R.id.tvTimerAfterMinutes);
        Button btnOK = (Button) layout.findViewById(R.id.btnOKSeebar);

        Button btnCancel = (Button) layout.findViewById(R.id.btnCancelSeekbar);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(layout);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlayerConstants.SETUP_TIMER.sendMessage(PlayerConstants.SETUP_TIMER.obtainMessage(0, mSeekBarTimer + ""));
                } catch (Exception e) {
                }
                alertDialog.hide();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.hide();
            }
        });
        SeekBar sb = (SeekBar) layout.findViewById(R.id.seekBarSetupTimer);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSeekBarTimer = progress;
                if (progress != 0) {
                    String noti = progress / 60 + "giờ " + progress % 60 + "phút ";
                    mTvTimerAfterMinutes.setText("Tắt nhạc sau " + noti);
                } else {
                    mTvTimerAfterMinutes.setText("Hủy hẹn giờ");
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    IncomingHandler incomingHandler = new IncomingHandler();
    final Messenger mMessenger = new Messenger(incomingHandler);
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.tantv.vnradiotruyen.activities")) {
                int play = intent.getIntExtra("playMusicActivity", 0);
                switch (play) {
                    case 11: {
                        playClicked();
                        break;
                    }
                    case 12: {
                        previousClicked();
                        break;
                    }
                    case 13: {
                        nextClicked();
                        break;
                    }
                    case 14: {
                        int progress = intent.getIntExtra("progressSeekbar", 0);
                        try {
                            msg = Message.obtain(null, 105, progress, 0);
                            mService.send(msg);
                        } catch (RemoteException e) {
                            Log.d("tantv2", "loi dinh menh" + e.toString());
                        }
                        break;
                    }
                    case 15: {
                        try {
                            msg = Message.obtain(null, 107);
                            mService.send(msg);
                        } catch (RemoteException e) {
                            Log.d("tantv2", "loi dinh menh" + e.toString());
                        }
                        break;
                    }
                    case 16: {
                        int position = intent.getIntExtra("positionMusicActivity", 0);
                        msg = Message.obtain(null, 101, position, 0);
                        try {
                            mService.send(msg);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                    }


                }
            }
        }
    };

    // private InterstitialAd interstitial;

    @ViewById(R.id.rrViewAd)
    RelativeLayout mRlViewAd;
    AdView adView;

    public void loadAd() {

        adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        AdRequest adRequest = new AdRequest.Builder()

                // Add a test device to show Test Ads
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("134A1CA55349046A9E073083486C581E")
                .build();
        adView.setAdUnitId("ca-app-pub-5675731296915316/9105427983");
        RelativeLayout.LayoutParams labelLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        labelLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mRlViewAd.addView(adView, labelLayoutParams);
        mRlViewAd.setVisibility(View.GONE);

        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                mRlViewAd.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mRlViewAd.setVisibility(View.VISIBLE);
            }
        });

    }


    private static Fragment fragmentListRadioPlay;

    @AfterViews
    void afterView() {
        hideMiniPlayer();
        initDB();
        configRecycleView();
        getDataRecyclerMenu();
        initRecyclerView();
        mTempDataTrackAudio = new ArrayList<>();
        mDataTrackAudio = new ArrayList<>();
        BusHolder.get().register(this);
        getSupportActionBar().hide();
        fragmentManager = getFragmentManager();
        onClickItemListRadioFragment = this;
        onClickTruyenDemKhuya = this;
        boolean isServiceRunning = UtilFunctions.isServiceRunning(PlayMusicService.class.getName(), getApplicationContext());
        playIntent = new Intent(this, PlayMusicService.class);
        bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        if (!isServiceRunning) {
            Log.d("tanv1235", "service shutdown");
            startService(playIntent);
        }
        mContext = this;
        getDataApi();
        App.mIsCheckActivityRunning = 1;
    }

    public void retryClick() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                fragmentManager.popBackStack();
            }
        }
        getDataApi();
        TruyenDemKhuyaFragment_ truyenDemKhuyaFragment_ = new TruyenDemKhuyaFragment_();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.flListPlay, truyenDemKhuyaFragment_, null);
        fragmentTransaction.commit();
    }

    private void initRecyclerView() {
        MenuAdapter menuAdapter = new MenuAdapter(mMenuObjects, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerViewMenu.setHasFixedSize(true);
        mRecyclerViewMenu.setLayoutManager(layoutManager);
        mRecyclerViewMenu.setAdapter(menuAdapter);
    }

    public void getDataRecyclerMenu() {
        mMenuObjects = new ArrayList<>();
        mMenuObjects = ApplicationData.addData();
    }

    private void configRecycleView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewMenu.setLayoutManager(linearLayoutManager);
    }


    @Override
    public void onClickMenu(int position) {
        mPositionMenuClicked = position;
        onClickGetData(position);
        initFragment(position);
    }

    @Click(R.id.rlClickedMiniMusic)
    void clickedMiniPlayMusic() {
        Intent intent = new Intent(getBaseContext(), PlayMusicActivity_.class);
        intent.putExtra("EXTRA_BUTTON_PLAY", mIsButtonPlayClicked);
        startActivity(intent);

    }

    @ViewById(R.id.imgMainPlayMusic)
    static ImageView mImgMainPlayMusic;

    private static boolean mIsButtonPlayClicked = true;


    //Play
    @Click(R.id.imgMainPlayMusic)
    static void playClicked() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        //False
        mIsButtonPlayClicked = !mIsButtonPlayClicked;
        //Stop
        if (!mIsButtonPlayClicked) {
            mImgMainPlayMusic.setSelected(false);
            try {
                msg = Message.obtain(null, 104, 1, 0);
                mService.send(msg);
            } catch (RemoteException e) {
                Log.d("tantv2", "loi dinh menh" + e.toString());
            }
            //Play
        } else {

            mImgMainPlayMusic.setSelected(true);
            try {
                msg = Message.obtain(null, 104, 0, 0);
                mService.send(msg);
            } catch (RemoteException e) {
                Log.d("tantv2", "loi dinh menh" + e.toString());
            }
        }
    }

    private static long mLastClickTime = 0;

    @Click(R.id.imgMainPrevious)
    static void previousClicked() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        if (fragmentListRadioPlay instanceof ListRadioPlayFragment_) {
            ((ListRadioPlayFragment_) fragmentListRadioPlay).notifyDatasetChange();
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        try {
            msg = Message.obtain(null, 103);
            mService.send(msg);

        } catch (RemoteException e) {
            Log.d("tantv2", "loi dinh menh" + e.toString());

        }
    }


    @Click(R.id.imgMainNext)
    static void nextClicked() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }

        mLastClickTime = SystemClock.elapsedRealtime();
        try {
            msg = Message.obtain(null, 102);
            mService.send(msg);

        } catch (RemoteException e) {
            Log.d("tantv2", "loi dinh menh" + e.toString());

        }
    }


    public static class IncomingCall extends BroadcastReceiver {
        public IncomingCall() {
        }

        public void onReceive(Context context, Intent intent) {

            try {
                TelephonyManager tmgr = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                MyPhoneStateListener PhoneListener = new MyPhoneStateListener();
                tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

            } catch (Exception e) {
                Log.e("Phone Receive Error", " " + e);
            }

        }

        private static class MyPhoneStateListener extends PhoneStateListener {

            public void onCallStateChanged(int state, String incomingNumber) {


                if (state == 1) {
                    mIsButtonPlayClicked = true;
                    mImgMainPlayMusic.setSelected(false);
                    try {
                        msg = Message.obtain(null, 108);
                        mService.send(msg);

                    } catch (RemoteException e) {
                        Log.d("tantv2", "loi dinh menh" + e.toString());

                    }

                }
            }
        }
    }


    public static class NotifiBroad extends BroadcastReceiver {
        public NotifiBroad() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(PlayMusicService.NOTIFY_PAUSE)) {
                playClicked();

            } else if (intent.getAction().equals(PlayMusicService.NOTIFY_NEXT)) {
                nextClicked();
            } else if (intent.getAction().equals(PlayMusicService.NOTIFY_DELETE)) {
                Intent myService = new Intent(context, PlayMusicService.class);
                context.stopService(myService);
                ((Activity) mContext).finish();
                System.exit(0);

            } else if (intent.getAction().equals(PlayMusicService.NOTIFY_PREVIOUS)) {
                previousClicked();
            }
        }

    }


    @Click(R.id.imgMainReplay)
    void replayClicked() {
    }

    public void showMiniPlayer() {
        if (mLlMenuController.getVisibility() == View.GONE) {
            mLlMenuController.setVisibility(View.VISIBLE);        // Prepare the View for the animation
            mLlMenuController.startAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.slid_up));
        }
    }

    public void hideMiniPlayer() {
        if (mLlMenuController.getVisibility() == View.VISIBLE) {
            mLlMenuController.startAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.slid_down));
            mLlMenuController.setVisibility(View.GONE);
        }
    }


    private void setTitleSong(String title) {
        mTvTitleSong.setSelected(true);
        mTvTitleSong.setText(title);
    }


    public void commitFragmentPlayList(ArrayList<TrackAudio> trackAudios, int position) {
        fragmentListRadioPlay = ListRadioPlayFragment_.builder().mTrackArgs(trackAudios).positionArgs(position).build();
        commitFragment(fragmentListRadioPlay);
        mDrawerLayout.closeDrawer(mRecyclerViewMenu);
    }

    public static boolean mIsClickedItem;

    @Override
    public void onClickListRadioFragment(int position) {

        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        mIsClickedItem = true;
        mIsButtonPlayClicked = true;
        mIsFirstRun = true;
        mImgMainPlayMusic.setSelected(true);
        mDataTrackAudio.removeAll(mDataTrackAudio);
        mDataTrackAudio.addAll(mTempDataTrackAudio);
        BusHolder.get().post(sendData());
        showMiniPlayer();
        mImgSetupTimer.setVisibility(View.VISIBLE);

        try {
            msg = Message.obtain(null, 101, position, 0);
            mService.send(msg);
        } catch (RemoteException e) {
            Log.d("tantv2", "loi dinh menh" + e.toString());
        }


    }


    @Produce
    public ArrayList<TrackAudio> sendData() {
        return mDataTrackAudio;
    }


    //Click item list play
    @Override
    public void onClickTruyenDemKhuya(int pos, ArrayList<TrackAudio> mTracks) {
        Log.d("tantv123", "onClickTruyenDemKhuya");
        mTempDataTrackAudio.removeAll(mTempDataTrackAudio);
        mTempDataTrackAudio.addAll(mTracks);
    }

    private void commitFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.flListPlay, fragment, null);
        fragmentTransaction.commit();
    }

    private boolean doubleBackToExitPressedOnce;

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mRecyclerViewMenu)) {
            mDrawerLayout.closeDrawer(mRecyclerViewMenu);
        } else if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Nhấn thêm một lần nữa để thoát!", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.mIsCheckActivityRunning = 2;
        // Logs 'app deactivate' App EventonPa.
        // AppEventsLogger.deactivateApp(this);
    }

    boolean mIsFirstRun;
    public static boolean mIsClicked;

    @Override
    protected void onResume() {
        App.mIsCheckActivityRunning = 3;

        this.registerReceiver(myBroadcastReceiver, new IntentFilter("com.tantv.vnradiotruyen.activities"));
        boolean isServiceRunning = UtilFunctions.isServiceRunning(PlayMusicService.class.getName(), getApplicationContext());
        if (!isServiceRunning) {
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
        try {
            PlayerConstants.PROGRESSBAR_HANDLER = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    PlayMusicService.SendDataFromService sendDataFromService = (PlayMusicService.SendDataFromService) msg.obj;
                    Integer i[] = sendDataFromService.a;
                    if (mNameSong == null) {
                        mNameSong = sendDataFromService.mTrackAudios.get(i[3]).getTitle();
                        setTitleSong(mNameSong + "");
                    }
                    mProgressBarTimer.setProgress(i[1]);
                    mProgressBarTimer.setSecondaryProgress(i[2]);
                    mTvLoadingData.setText("Đang tải dữ liệu: " + i[2] + "%");
                    if (i[2] == 100) {
                        mTvLoadingData.setText("Tải dữ liệu thành công !");
                    }
                    if (!mIsFirstRun) {
                        if (i[5] == 1)
                            mImgMainPlayMusic.setSelected(true);
                        else {
                            mImgMainPlayMusic.setSelected(false);
                        }
                        mIsFirstRun = true;
                        showMiniPlayer();

                    }
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
                    mImgMainPlayMusic.setSelected(true);
                    mIsClicked = true;
                    int pos = Integer.parseInt(s[1]);
                    if (fragmentListRadioPlay instanceof ListRadioPlayFragment_) {
                        ((ListRadioPlayFragment_) fragmentListRadioPlay).onClickRadioPlayAdapter(pos);
                    }

                }
            };
        } catch (Exception e) {
        }

        try {
            PlayerConstants.SEND_TITLE_SONG_AND_IS_PLAYING = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    String s[] = (String[]) msg.obj;
                    setTitleSong(s[0]);
                    if ("1".equals(s[1])) {
                        mImgMainPlayMusic.setSelected(true);
                    } else {
                        mImgMainPlayMusic.setSelected(false);
                    }
                }

            };
        } catch (Exception e) {
        }
        try {
            PlayerConstants.IS_PLAYING = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    int i = (Integer) msg.obj;
                    if (i == 1) {
                        mImgMainPlayMusic.setSelected(false);
                    } else {
                        mImgMainPlayMusic.setSelected(true);
                    }
                }
            };
        } catch (Exception e) {
        }
        try {
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
                        mImgMainPlayMusic.setSelected(false);
                        mTvTimerSetUp.setVisibility(View.GONE);
                        return;
                    }
                    if (time == 0) {
                        mTvTimerSetUp.setVisibility(View.GONE);
                    } else {
                        mTvTimerSetUpLeft.setVisibility(View.GONE);
                        mTvTimerSetUp.setVisibility(View.VISIBLE);
                        mImgSetupTimer.setVisibility(View.VISIBLE);
                        if ((time / 60) % 60 < 10) {
                            mTvTimerSetUp.setText("0" + time / (60 * 60) + ":0" + (time / 60) % 60 + ":" + time % 60);
                        } else {
                            mTvTimerSetUp.setText("0" + time / (60 * 60) + ":" + (time / 60) % 60 + ":" + time % 60);
                        }
                    }
                }
            };
        } catch (Exception e) {
        }
        try {
            PlayerConstants.SEND_TIMER_LEFT = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    String s = (String) msg.obj;
                    int time = 0;
                    try {
                        time = Integer.parseInt(s) / 1000;
                    } catch (Exception e) {
                    }
                    if (time == 0) {
                        mTvTimerSetUpLeft.setVisibility(View.GONE);
                    } else {
                        if (mTvTimerSetUp.getVisibility() == View.GONE) {
                            mTvTimerSetUpLeft.setVisibility(View.VISIBLE);
                            mImgSetupTimer.setVisibility(View.VISIBLE);
                            if ((time / 60) % 60 < 10) {
                                mTvTimerSetUpLeft.setText("0" + time / (60 * 60) + ":0" + (time / 60) % 60 + ":" + time % 60);
                            } else {
                                mTvTimerSetUpLeft.setText("0" + time / (60 * 60) + ":" + (time / 60) % 60 + ":" + time % 60);
                            }
                        }
                    }
                }
            };
        } catch (Exception e) {
        }
        super.onResume();

    }

    private void setMainTitle(String name) {
        mTvTitle.setText(name + "");
    }

    private void initFragment(int fragmentID) {
        switch (fragmentID) {
            //Blog radio
            case 1:
                setMainTitle("BLOG RADIO");
                break;
            //Sach noi
            case 3:
                setMainTitle("SÁCH NÓI");
                break;
            //Truyen dem khuya
            case 5:
                setMainTitle("TRUYỆN ĐÊM KHUYA");
                break;
            //Truyen ma kinh di
            case 6:
                setMainTitle("TRUYỆN MA - KINH DỊ");
                break;
            //Da su kiem hiep
            case 7:
                setMainTitle("DÃ SỬ - KIẾM HIỆP");
                break;
            //Co tich thieu nhi
            case 8:
                setMainTitle("CỔ TÍCH - THIẾU NHI");
                break;
            //Tinh yeu
            case 9:
                setMainTitle("TÌNH YÊU");
                break;
            //khac
            case 10: {
                setMainTitle("KHÁC");
                break;
            }
            //Danh gia
            case 12: {
                Uri uri = Uri.parse("market://details?id=" + "com.tantv.vnradiotruyen");
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + "com.tantv.vnradiotruyen")));
                }
                break;
            }
            case 13: {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:HTTDev Inc"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
                }
                break;
            }
            //Gop y
            case 14: {
                // setMainTitle("GÓP Ý");
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"htt.devvn@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "[VN RADIO ONLINE]");
                i.putExtra(Intent.EXTRA_TEXT, "Ý kiến của bạn: ");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this, "ứng dụng mail chưa được cài vào máy !", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            //Gioi thieu
            case 15: {
                //setMainTitle("GIỚI THIỆU");
                Intent i = new Intent(this, AboutActitivy_.class);
                startActivity(i);

                break;
            }
        }
        switch (fragmentID) {
            //Blog radio
            case 1:
                //Sach noi
            case 3:
                //Truyen dem khuya
            case 5:
                //Truyen ma kinh di
            case 6:
                //Da su kiem hiep
            case 7:
                //Co tich thieu nhi
            case 8:
                //Tinh yeu
            case 9:
                //khac
            case 10: {
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                        fragmentManager.popBackStack();
                    }
                }
                Fragment fragment = new TruyenDemKhuyaFragment_();
                commitFragment(fragment);
                mDrawerLayout.closeDrawer(mRecyclerViewMenu);
                break;
            }
            //Danh gia
            case 12: {

                break;
            }
            //Ung dung khac
            case 13: {
                break;
            }
            //Gop y
            case 14: {
                break;
            }
            //Gioi thieu
            case 15: {
                break;
            }
        }
    }


    @Click(R.id.imgMainMenu)
    void clickMenuLeft() {
        if (mDrawerLayout.isDrawerOpen(mRecyclerViewMenu)) {
            mDrawerLayout.closeDrawer(mRecyclerViewMenu);
        } else {
            mDrawerLayout.openDrawer(mRecyclerViewMenu);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(myBroadcastReceiver);
        BusHolder.get().unregister(this);
        boolean isServiceRunning = UtilFunctions.isServiceRunning(PlayMusicService.class.getName(), getApplicationContext());
        if (isServiceRunning) {
            unbindService(musicConnection);
        }


    }


    private ProgressDialog mProgressDialog;

    private void onClickGetData(int pos) {
        mListRadioTruyens.removeAll(mListRadioTruyens);
        mTruyens.removeAll(mTruyens);
        mTrackAudios.removeAll(mTrackAudios);
        for (int i = 0; i < mListRadioTruyensAPI.size(); i++) {
            String tenTruyenFull = mListRadioTruyensAPI.get(i).getTitle();
            String msTruyen = tenTruyenFull.substring(0, 5);
            String tenTruyen = tenTruyenFull.substring(5, tenTruyenFull.length());
            String author = mListRadioTruyensAPI.get(i).getGenre() + "";

            switch (pos) {
                case 1: {
                    if (ApplicationData.BLOG_RADIO.equals(msTruyen)) {
                        getDataFromPosition(mListRadioTruyensAPI, tenTruyen, author, i);
                    }
                    break;
                }

                case 3: {
                    if (ApplicationData.SACH_NOI.equals(msTruyen)) {
                        getDataFromPosition(mListRadioTruyensAPI, tenTruyen, author, i);
                    }
                    break;
                }

                case 5: {
                    if (ApplicationData.TRUYEN_DEM_KHUYA.equals(msTruyen)) {
                        getDataFromPosition(mListRadioTruyensAPI, tenTruyen, author, i);
                    }
                    break;
                }
                case 6: {
                    if (ApplicationData.TRUYEN_MA_KINH_DI.equals(msTruyen)) {
                        getDataFromPosition(mListRadioTruyensAPI, tenTruyen, author, i);
                    }
                    break;
                }
                case 7: {
                    if (ApplicationData.DASU_KIEMHIEP.equals(msTruyen)) {
                        getDataFromPosition(mListRadioTruyensAPI, tenTruyen, author, i);
                    }
                    break;
                }

                case 8: {
                    if (ApplicationData.COTICH_THIEUNHI.equals(msTruyen)) {
                        getDataFromPosition(mListRadioTruyensAPI, tenTruyen, author, i);
                    }
                    break;
                }

                case 9: {
                    if (ApplicationData.TINHYEU.equals(msTruyen)) {
                        getDataFromPosition(mListRadioTruyensAPI, tenTruyen, author, i);
                    }
                    break;
                }

                case 10: {
                    if (ApplicationData.KHAC.equals(msTruyen)) {
                        getDataFromPosition(mListRadioTruyensAPI, tenTruyen, author, i);
                    }
                    break;
                }
            }

        }
    }

    private void getDataApi() {

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading.....");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        ApiClient.call().getPlaylistSoundClound(new Callback<List<ListRadioTruyen>>() {
            @Override
            public void success(List<ListRadioTruyen> listRadioTruyens) {
                // mLlViewSignalNoconnect.setVisibility(View.GONE);
                loadAd();
                mListRadioTruyensAPI.removeAll(mListRadioTruyensAPI);
                mListRadioTruyensAPI.addAll(listRadioTruyens);
                mProgressDialog.hide();
                onClickGetData(1);
                Log.d("tantv123", "Success" + listRadioTruyens.size());
                TruyenDemKhuyaFragment_ truyenDemKhuyaFragment_ = new TruyenDemKhuyaFragment_();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.add(R.id.flListPlay, truyenDemKhuyaFragment_, null);
                fragmentTransaction.commit();

            }

            @Override
            public void failure(RetrofitError error, Error myError) {
                mProgressDialog.hide();
                loadAd();
                Log.d("tantv123", "Loi: " + error);
                //mLlViewSignalNoconnect.setVisibility(View.VISIBLE);
                TruyenDemKhuyaFragment_ truyenDemKhuyaFragment_ = new TruyenDemKhuyaFragment_();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.add(R.id.flListPlay, truyenDemKhuyaFragment_, null);
                fragmentTransaction.commit();

            }
        });

    }


    void initDB() {
        mTruyens = new ArrayList<>();
        mTrackAudios = new ArrayList<>();
        mListRadioTruyens = new ArrayList<>();
        mListRadioTruyensAPI = new ArrayList<>();
    }

    private List<ListRadioTruyen> mListRadioTruyensAPI;
    @Getter
    private ArrayList<ArrayList<TrackAudio>> mTrackAudios;
    @Getter
    private ArrayList<Truyen> mTruyens;
    @Getter
    private ArrayList<ListRadioTruyen> mListRadioTruyens;

    private void getDataFromPosition(List<ListRadioTruyen> listRadioTruyens, String tenTruyen, String author, int i) {
        //Cái cần là cái mlis và mtruyen mtrack nữa đm

        ArrayList<TrackAudio> mTracks = new ArrayList<>();
        mListRadioTruyens.add(listRadioTruyens.get(i));
        TrackAudio trackAudio;
        Truyen truyen = new Truyen(tenTruyen, author, listRadioTruyens.get(i).getArtworkUrl() + "");
        mTruyens.add(truyen);
        for (int j = 0; j < listRadioTruyens.get(i).getTrackCount(); j++) {
            int id = listRadioTruyens.get(i).getTracks().get(j).getId();
            String title = listRadioTruyens.get(i).getTracks().get(j).getTitle();
            String urlImage = listRadioTruyens.get(i).getTracks().get(j).getArtworkUrl();
            int duration = listRadioTruyens.get(i).getTracks().get(j).getDuration();
            trackAudio = new TrackAudio(id, title, listRadioTruyens.get(i).getTitle(), urlImage, listRadioTruyens.get(i).getTrackCount(), 1, duration, false);
            mTracks.add(trackAudio);
        }
        mTrackAudios.add(mTracks);
    }
}
