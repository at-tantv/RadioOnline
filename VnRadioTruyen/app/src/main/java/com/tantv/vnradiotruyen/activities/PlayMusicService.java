package com.tantv.vnradiotruyen.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.tantv.vnradiotruyen.R;
import com.tantv.vnradiotruyen.model.BusHolder;
import com.tantv.vnradiotruyen.model.TrackAudio;
import com.tantv.vnradiotruyen.until.PlayerConstants;
import com.tantv.vnradiotruyen.until.UtilFunctions;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import lombok.Getter;

/**
 * Copyright @2015
 * Created by tantv on 28/09/2015.
 */
public class PlayMusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {
    public static final String NOTIFY_PREVIOUS = "com.tantv.vnradiotruyen.activities.previous";
    public static final String NOTIFY_DELETE = "com.tantv.vnradiotruyen.activities.delete";
    public static final String NOTIFY_PAUSE = "com.tantv.vnradiotruyen.activities.pause";
    public static final String NOTIFY_NEXT = "com.tantv.vnradiotruyen.activities.next";
    private static boolean currentVersionSupportBigNotification = false;
    private Notification mNotification;
    //private static boolean currentVersionSupportLockScreenControls = false;
    private Timer timer;
    private MediaPlayer player;
    private MainTask mainTask;
    private boolean isCheckTimer;
    @Getter
    private int timerMusic = 0;
    private ArrayList<TrackAudio> mTrackAudios;
    @Getter
    private int mBufferLoading = 0;
    private int mPosition;
    private CountDownTimer mOldCountDownTimer;
    private MainActivity.IncomingCall incomingCall;

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        incomingCall = new MainActivity.IncomingCall();
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PHONE_STATE");

        try {
            registerReceiver(incomingCall,intentFilter);
            PlayerConstants.SETUP_TIMER = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    String s = (String) msg.obj;

                    if (mOldCountDownTimer != null) {
                        mOldCountDownTimer.cancel();
                    }
                    int time = Integer.parseInt(s);
                    String noti = time / 60 + "giờ " + (time % 60) + "phút ";


                    if (time != 0) {
                        Toast.makeText(getApplicationContext(), "Tắt nhạc sau " + noti, Toast.LENGTH_SHORT).show();
                        mOldCountDownTimer = new CountDownTimer(time * 60000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                try {
                                    PlayerConstants.SEND_TIMER.sendMessage(PlayerConstants.SEND_TIMER.obtainMessage(0, millisUntilFinished + ""));
                                } catch (Exception e) {
                                }
                            }

                            @Override
                            public void onFinish() {
                                PlayerConstants.SEND_TIMER.sendMessage(PlayerConstants.SEND_TIMER.obtainMessage(0, 3 + ""));
                                player.pause();
                                try {
                                    PlayerConstants.SEND_TIMER.sendMessage(PlayerConstants.SEND_TIMER.obtainMessage(0, 3 + ""));
                                } catch (Exception e) {
                                }
                            }
                        }.start();
                    } else {
                        Toast.makeText(getApplicationContext(), "Đã hủy chức năng hẹn giờ !", Toast.LENGTH_SHORT).show();
                        try {
                            PlayerConstants.SEND_TIMER.sendMessage(PlayerConstants.SEND_TIMER.obtainMessage(0, 0 + ""));
                        } catch (Exception e) {
                        }
                    }

                }
            };
        } catch (Exception e) {
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        BusHolder.get().register(this);
        mTrackAudios = new ArrayList<>();
        timer = new Timer();
        mainTask = new MainTask();
        currentVersionSupportBigNotification = UtilFunctions.currentVersionSupportBigNotification();
        //currentVersionSupportLockScreenControls = UtilFunctions.currentVersionSupportLockScreenControls();
        if (player == null) player = new MediaPlayer();
        initMusicPlayer();
        super.onCreate();
    }

    /**
     * Send message from timer
     *
     * @author jonty.ankit
     */
    private class MainTask extends TimerTask {
        public void run() {
            handler.sendEmptyMessage(0);
            if (!isCheckTimer) {
                timer.cancel();
                isCheckTimer = false;
            }
        }
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (mBufferLoading > 0) {
                checkAutoPlay = false;
            }
            if (player != null) {
                Integer i[] = new Integer[7];
                int progress = 0;
                try {
                    progress = (player.getCurrentPosition() * 100) / mTrackAudios.get(mPosition).getDuration();
                } catch (Exception e) {

                }
                try {
                    i[0] = player.getCurrentPosition();
                } catch (Exception e) {
                    i[0] = 0;
                }

                i[1] = progress;
                i[2] = mBufferLoading;
                i[3] = mPosition;
                i[4] = mTrackAudios.get(mPosition).getDuration();
                if (player.isPlaying()) {
                    i[5] = 1;
                } else {
                    i[5] = 0;
                }
                i[6] = mPosition;
                String title;
                try {
                    title = mTrackAudios.get(mPosition).getTitle();
                } catch (Exception e) {
                    title = "";
                }
                SendDataFromService sendDataFromService = new SendDataFromService(i, title, mTrackAudios);
                try {

                    PlayerConstants.PROGRESSBAR_HANDLER.sendMessage(PlayerConstants.PROGRESSBAR_HANDLER.obtainMessage(0, sendDataFromService));
                } catch (Exception e) {
                }
            }
        }
    };

    public class SendDataFromService {
        Integer[] a;
        String title;
        ArrayList<TrackAudio> mTrackAudios;

        public SendDataFromService(Integer[] a, String title, ArrayList<TrackAudio> mTrackAudios) {
            this.a = a;
            this.title = title;
            this.mTrackAudios = mTrackAudios;
        }
    }

    boolean checkAutoPlay;
    boolean ktCheckNotify;
    CountDownTimer newCountDownTimerAutoNext;

    void onAutoPlay() {
        if (newCountDownTimerAutoNext != null) {
            newCountDownTimerAutoNext.cancel();
        }
        checkAutoPlay = true;
        newCountDownTimerAutoNext = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("count", millisUntilFinished / 1000 + "");
                if (mBufferLoading < 1) {
                    try {
                        PlayerConstants.SEND_TIMER_LEFT.sendMessage(PlayerConstants.SEND_TIMER_LEFT.obtainMessage(0, millisUntilFinished + ""));
                    } catch (Exception e) {
                    }
                } else
                    try {
                        PlayerConstants.SEND_TIMER_LEFT.sendMessage(PlayerConstants.SEND_TIMER_LEFT.obtainMessage(0, 0 + ""));
                    } catch (Exception e) {
                    }


            }

            @Override
            public void onFinish() {
                if (checkAutoPlay && !mIsPauseOrPlay) {
                    Toast.makeText(getApplicationContext(), "Tự động chuyển bài, do chờ quá lâu !", Toast.LENGTH_LONG).show();
                    nextMusic();
                } else {
                    try {
                        PlayerConstants.SEND_TIMER_LEFT.sendMessage(PlayerConstants.SEND_TIMER_LEFT.obtainMessage(0, 0 + ""));
                    } catch (Exception e) {
                    }
                }
            }
        }.start();
    }

    public void playSong(int id) {
        onAutoPlay();
        if (!ktCheckNotify) newNotification();
        ktCheckNotify = true;
        mBufferLoading = 0;
        mIsPauseOrPlay = false;
        setTitleSong();
        setSelectorIsPlaying();
        player.reset();
        try {
            String urlPlay = "https://api.soundcloud.com/tracks/" + id + "/stream?client_id="+"e534ffbc2d474446c8538d23b1c7605c";
            player.setDataSource(urlPlay);
            Log.d("play1132","url: "+urlPlay);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.setOnPreparedListener(this);
        player.prepareAsync();
        isCheckTimer = true;
        mainTask.cancel();
        try {
            String s[] = new String[2];
            String title = mTrackAudios.get(mPosition).getTitle();
            s[0] = title;
            s[1] = mPosition + "";
            PlayerConstants.SEND_TITLE_SONG.sendMessage(PlayerConstants.SEND_TITLE_SONG.obtainMessage(0, s));
        } catch (Exception e) {
        }
        mainTask = new MainTask();
        timer.scheduleAtFixedRate(mainTask, 0, 100);

    }


    public void initMusicPlayer() {

        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setOnBufferingUpdateListener(this);

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        mBufferLoading = percent;
    }

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 100: {
                    player.start();
                    break;
                }
                case 101: {
                    mPosition = msg.arg1;
                    playSong(mTrackAudios.get(msg.arg1).getId());
                    break;
                }
                case 102: {
                    nextMusic();
                    break;
                }
                case 103: {
                    previousMusic();
                    break;
                }
                case 104: {
                    mIsPauseOrPlay = !mIsPauseOrPlay;
                    /*if(!mIsPauseOrPlay){
                        onAutoPlay();
                    } else try {
                        PlayerConstants.SEND_TIMER.sendMessage(PlayerConstants.SEND_TIMER.obtainMessage(0, 0 + ""));
                    } catch (Exception e) {
                    }*/
                    int pause = msg.arg1;
                    if (pause == 1) {
                        if (mBufferLoading > 0) {
                            player.pause();
                        }
                        setSelectorIsPause();
                    } else {
                        player.start();
                        setSelectorIsPlaying();
                    }
                    break;
                }
                case 105: {
                    int mili = (msg.arg1 * player.getDuration() / 100);
                    player.seekTo(mili);
                    break;
                }

                case 106: {
                    try {
                        String s[] = new String[1];
                        String title = mTrackAudios.get(mPosition).getTitle();
                        s[0] = title;
                        PlayerConstants.SEND_TITLE_SONG.sendMessage(PlayerConstants.SEND_TITLE_SONG.obtainMessage(0, s));
                    } catch (Exception e) {
                    }
                    break;
                }
                case 107: {
                    try {
                        String s[] = new String[2];
                        String title = mTrackAudios.get(mPosition).getTitle();
                        s[0] = title;
                        if (player.isPlaying()) s[1] = "1";
                        else s[1] = "0";
                        PlayerConstants.SEND_TITLE_SONG_AND_IS_PLAYING.sendMessage(PlayerConstants.SEND_TITLE_SONG.obtainMessage(0, s));
                    } catch (Exception e) {
                    }
                    break;
                }
                case 108: {
                    try {
                        String s[] = new String[1];
                        s[0] = "1";
                        PlayerConstants.SEND_CALL_PHONE.sendMessage(PlayerConstants.SEND_CALL_PHONE.obtainMessage(0, s));
                    } catch (Exception e) {
                    }
                    setSelectorIsPause();
                    player.pause();
                    break;
                }
            }

        }
    }


    IncomingHandler incomingHandler = new IncomingHandler();
    final Messenger mMessenger = new Messenger(incomingHandler);


    @Subscribe
    public void getDataOtto(ArrayList<TrackAudio> list) {
        if(list!=null && list.size()>0) {
            mTrackAudios = list;
            Log.d("tan1111111", ": " + mTrackAudios.size());
            Log.d("tan1111111", ": " + mTrackAudios.size());
        }
    }


    @Override
    public void onDestroy() {
        unregisterReceiver(incomingCall);
        BusHolder.get().unregister(this);
        if (player != null) {
            player.stop();
            player.release();
        }
        super.onDestroy();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d("tan121", "Hết bài 1");
        if (mBufferLoading > 0) {
            Log.d("tan121", "Hết bài 2");
            nextMusic();

        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    boolean mIsPauseOrPlay;

    @Override
    public void onPrepared(MediaPlayer mp) {

        mp.start();
        if (mIsPauseOrPlay)
            mp.pause();

    }


    public boolean previousMusic() {
        if (mPosition > 1) {
            mPosition--;
            playSong(mTrackAudios.get(mPosition).getId());
            return true;
        }
        return false;

    }

    public boolean nextMusic() {
        if (mPosition < mTrackAudios.size() - 1) {
            mPosition++;
            playSong(mTrackAudios.get(mPosition).getId());

            return true;
        }
        return false;
    }

    /**
     * Notification
     * Custom Bignotification is available from API 16
     */
    @SuppressLint("NewApi")
    private void newNotification() {
        RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.custom_notification);
        RemoteViews expandedView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.big_notification);
        mNotification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_music)
                .setContentTitle("N/A").build();

        setListeners(simpleContentView);
        setListeners(expandedView);
        mNotification.contentView = simpleContentView;
        if (currentVersionSupportBigNotification) {
            mNotification.bigContentView = expandedView;
        }

        try {
            Bitmap albumArt = BitmapFactory.decodeResource(getResources(), R.drawable.default_album_art2);
            mNotification.contentView.setImageViewBitmap(R.id.imageViewAlbumArt, albumArt);
            if (currentVersionSupportBigNotification) {
                mNotification.bigContentView.setImageViewBitmap(R.id.imageViewAlbumArt, albumArt);
            } else {
                mNotification.contentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.default_album_art2);
                if (currentVersionSupportBigNotification) {

                    mNotification.bigContentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.default_album_art2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mNotification.contentView.setTextViewText(R.id.textSongName, "N/A");
        mNotification.contentView.setTextViewText(R.id.textAlbumName, "N/A");
        if (currentVersionSupportBigNotification) {
            mNotification.bigContentView.setTextViewText(R.id.textSongName, "N/A");
            mNotification.bigContentView.setTextViewText(R.id.textAlbumName, "N/A");
        }
        Intent intent = new Intent(this, PlayMusicActivity_.class);
        intent.putExtra("data 1", true);
        intent.setAction("myString1" + 1);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mNotification.contentIntent = pendingIntent;
        mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        startForeground(1111, mNotification);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void setSelectorIsPause() {
        mNotification.contentView.setImageViewResource(R.id.btnPause, R.drawable.ic_play_arrow_white_36dp);
        if (currentVersionSupportBigNotification) {
            mNotification.bigContentView.setImageViewResource(R.id.btnPause, R.drawable.ic_play_arrow_white_36dp);
        }
        startForeground(1111, mNotification);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void setTitleSong() {
        mNotification.contentView.setTextViewText(R.id.textAlbumName, "N/A");
        if (currentVersionSupportBigNotification) {
            mNotification.bigContentView.setTextViewText(R.id.textSongName, "" + mTrackAudios.get(mPosition).getTitle());
            mNotification.bigContentView.setTextViewText(R.id.textAlbumName, "N/A");
        }
        mNotification.contentView.setTextViewText(R.id.textSongName, "" + mTrackAudios.get(mPosition).getTitle());
        mNotification.contentView.setTextViewText(R.id.textAlbumName, "N/A");
        startForeground(1111, mNotification);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void setSelectorIsPlaying() {
        mNotification.contentView.setImageViewResource(R.id.btnPause, R.drawable.ic_pause_white_36dp);
        if (currentVersionSupportBigNotification) {
            mNotification.bigContentView.setImageViewResource(R.id.btnPause, R.drawable.ic_pause_white_36dp);
        }
        startForeground(1111, mNotification);
    }

    /**
     * Notification click listeners
     *
     * @param view
     */
    public void setListeners(RemoteViews view) {
        Intent previous = new Intent(NOTIFY_PREVIOUS);
        Intent delete = new Intent(NOTIFY_DELETE);
        Intent pause = new Intent(NOTIFY_PAUSE);
        Intent next = new Intent(NOTIFY_NEXT);

        PendingIntent pPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPrevious, pPrevious);

        PendingIntent pDelete = PendingIntent.getBroadcast(getApplicationContext(), 0, delete, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnDelete, pDelete);

        PendingIntent pPause = PendingIntent.getBroadcast(getApplicationContext(), 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPause, pPause);

        PendingIntent pNext = PendingIntent.getBroadcast(getApplicationContext(), 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnNext, pNext);


    }

}