package com.geekband.musicplayersuper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    private TextView btn_pre, btn_play_pause, btn_next, songID_text;
    private int i = 0;
    public static String MAIN_ACTIVITY_UPDATE_UI = "main_activity_update_ui";
    public static String KEY_MAIN_ACTIVITY_UI_BTN = "main_activity_ui_btn_key";
    public static String KEY_MAIN_ACTIVITY_UI_TEXT = "main_activity_ui_text_key";
    public static final int  VAL_UPDATE_UI_PLAY = 1,VAL_UPDATE_UI_PAUSE =2;

    BroadcastReceiver activityReceiver = new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String action = intent.getAction();
            if (MAIN_ACTIVITY_UPDATE_UI.equals(action)) {
                int play_pause =  intent.getIntExtra(KEY_MAIN_ACTIVITY_UI_BTN, -1);
                int text = intent.getIntExtra(KEY_MAIN_ACTIVITY_UI_TEXT, -1);
                switch (play_pause) {
                    case VAL_UPDATE_UI_PLAY:
                        btn_play_pause.setBackgroundResource(R.drawable.music_pause);
                        songID_text.setText("song_0"+text);
                        break;
                    case VAL_UPDATE_UI_PAUSE:
                        btn_play_pause.setBackgroundResource(R.drawable.music_play);
                        songID_text.setText("song_0"+text);
                        break;
                    default:
                        break;
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MAIN_ACTIVITY_UPDATE_UI);
        registerReceiver(activityReceiver, intentFilter);

        initCtrl();
    }

    private void initCtrl() {
        btn_pre = (TextView) findViewById(R.id.prev_song);
        btn_play_pause = (TextView) findViewById(R.id.play_pause);
        btn_next = (TextView) findViewById(R.id.next_song);
        songID_text = (TextView)findViewById(R.id.song_name);

        btn_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postAction(MainActivity.this, MusicPlayerService.ACTION_PRE);
            }
        });

        btn_play_pause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                i++;
                if (i == 1) {
                    Intent startIntent = new Intent(MainActivity.this,MusicPlayerService.class);
                    startService(startIntent);
                }
                postAction(MainActivity.this, MusicPlayerService.ACTION_PLAY_PAUSE);
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postAction(MainActivity.this, MusicPlayerService.ACTION_NEXT);
            }
        });
    }

    private void postAction(Context context, int usr_action) {
        Intent actionIntent = new Intent(MusicPlayerService.ACTION);
        actionIntent.putExtra(MusicPlayerService.KEY_USR_ACTION, usr_action);
        context.sendBroadcast(actionIntent);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Intent startIntent = new Intent(MainActivity.this,MusicPlayerService.class);
        stopService(startIntent);
        super.onDestroy();
    }

}

