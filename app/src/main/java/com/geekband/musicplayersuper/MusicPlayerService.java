package com.geekband.musicplayersuper;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.IBinder;

/**
 * Created by Administrator on 2016/6/1.
 */
public class MusicPlayerService extends Service{
    private MediaPlayer mPlayer;
    private int mIndex = 0;// 默认从第一首开始播放
    private int[] mArrayList = new int[4];
    public static String ACTION = "to_service";
    public static String KEY_USR_ACTION = "key_usr_action";
    public static final int ACTION_PRE = 0, ACTION_PLAY_PAUSE = 1, ACTION_NEXT = 2;
    private boolean mPlayState = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action  = intent.getAction();
            if (ACTION.equals(action)) {
                int widget_action = intent.getIntExtra(KEY_USR_ACTION, -1);

                switch (widget_action) {
                    case ACTION_PRE:
                        playPrev(context);
                        break;
                    case ACTION_PLAY_PAUSE:
                        if (mPlayState) {
                            pause(context);
                        }else{
                            play(context);
                        }
                        break;
                    case ACTION_NEXT:
                        playNext(context);
                        break;
                    default:
                        break;
                }
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(serviceReceiver, intentFilter);

        initList();

        mediaPlayerStart();
    }

    private void mediaPlayerStart(){
        mPlayer = new MediaPlayer();
        mPlayer = MediaPlayer.create(getApplicationContext(), mArrayList[mIndex]);
        mPlayer.start();
        mPlayState = true;
        postState(getApplicationContext(), MusicPlayerWidget.VAL_UPDATE_UI_PLAY,mIndex);
    }

    private void initList() {
        mArrayList[0] = R.raw.song_00;
        mArrayList[1] = R.raw.song_01;
        mArrayList[2] = R.raw.song_02;
        mArrayList[3] = R.raw.song_03;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

//     播放下一首
    public void playNext(Context context) {
        if (++mIndex > 3) {
            mIndex = 0;
        }
        mPlayState = true;
        playSong(context, mArrayList[mIndex]);
        postState(context, MusicPlayerWidget.VAL_UPDATE_UI_PLAY,mIndex);
    }

//      播放上一首
    public void playPrev(Context context) {
        if (--mIndex < 0) {
            mIndex = 3;
        }
        mPlayState = true;
        playSong(context, mArrayList[mIndex]);
        postState(context, MusicPlayerWidget.VAL_UPDATE_UI_PLAY,mIndex);
    }

//     继续播放
    public void play(Context context) {
        mPlayState = true;
        mPlayer.start();
        postState(context, MusicPlayerWidget.VAL_UPDATE_UI_PLAY,mIndex);
    }

//     暂停播放
    public void pause(Context context) {
        mPlayState = false;
        mPlayer.pause();
        postState(context, MusicPlayerWidget.VAL_UPDATE_UI_PAUSE,mIndex);
    }

//     播放指定的歌曲
    private void playSong(Context context, int resid) {
        AssetFileDescriptor afd = context.getResources().openRawResourceFd(
                mArrayList[mIndex]);
        try {
            mPlayer.reset();
            mPlayer.setDataSource(afd.getFileDescriptor(),
                    afd.getStartOffset(), afd.getDeclaredLength());
            mPlayer.prepare();
            mPlayer.start();
            afd.close();
        } catch (Exception e) {
            e.getMessage();
        }

    }

    private void postState(Context context, int state,int songid) {
        Intent actionIntent = new Intent(MusicPlayerWidget.MAIN_ACTIVITY_UPDATE_UI);
        actionIntent.putExtra(MusicPlayerWidget.KEY_MAIN_ACTIVITY_UI_BTN,state);
        actionIntent.putExtra(MusicPlayerWidget.KEY_MAIN_ACTIVITY_UI_TEXT, songid);
        context.sendBroadcast(actionIntent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.stop();
    }

}


