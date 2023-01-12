package com.example.messimusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity {
    ImageButton btnPause,btnNext,btnPrevious,btnVolum;
    TextView lblSong,player_Position,player_Duration;
    SeekBar songSeekBar,volumSeekBar;
    static MediaPlayer myMediaPlayer;
    int position;
    ArrayList<File> mySong;
    Thread updateSeekBar,updateSeekBar2;
    String sName;
    AudioManager audioManager;
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        btnPause=(ImageButton)findViewById(R.id.btnPause);
        btnNext=(ImageButton)findViewById(R.id.btnNext);
        btnPrevious=(ImageButton)findViewById(R.id.btnPrevious);
        lblSong =(TextView)findViewById(R.id.lblSong);
        songSeekBar=(SeekBar)findViewById(R.id.songSeekBar);
        volumSeekBar=(SeekBar)findViewById(R.id.volumSeekBar);
        btnVolum=(ImageButton)findViewById(R.id.btnVolum);
        player_Position=findViewById(R.id.player_Position);
        player_Duration=findViewById(R.id.player_Duration);
        getSupportActionBar().setTitle(" Now Playing");
        getSupportActionBar().setIcon(R.drawable.ic_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Audio Volum code--------------------------
        audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int maxVolum=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolum=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumSeekBar.setMax(maxVolum);
        volumSeekBar.setProgress(currentVolum);
        volumSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
                if(progress==0){
                    btnVolum.setImageResource(R.drawable.ic_volume_off);
                }else{
                    btnVolum.setImageResource(R.drawable.ic_volume_up);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,volumSeekBar.getProgress(),0);

            }
        });
        updateSeekBar2=new Thread(){
            @Override
            public void run() {
                int maxVolum=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int currentVolum=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                while (currentVolum!=maxVolum+1){
                    try{
                        sleep(500);
                        currentVolum=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        volumSeekBar.setProgress(currentVolum);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }

                }
            }
        };
        updateSeekBar2.start();
        //Audio Volum end-----------------------------
        updateSeekBar=new Thread(){
            @Override
            public void run() {
                int totalDuration=myMediaPlayer.getDuration();
                int currentPosition=0;
                while (currentPosition<totalDuration){
                    try{
                        sleep(500);
                        currentPosition=myMediaPlayer.getCurrentPosition();
                        songSeekBar.setProgress(currentPosition);

                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        if(myMediaPlayer!=null){
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }
        Intent i=getIntent();
        Bundle bundle=i.getExtras();
        mySong=(ArrayList)bundle.getParcelableArrayList("songs");
        sName=mySong.get(position).getName().toString();
        String songName=i.getStringExtra("songname");
        lblSong.setText(songName);
        lblSong.setSelected(true);
        position=bundle.getInt("pos",0);
        Uri u=Uri.parse(mySong.get(position).toString());
        myMediaPlayer=MediaPlayer.create(getApplicationContext(),u);
        myMediaPlayer.start();
        songSeekBar.setMax(myMediaPlayer.getDuration());
        updateSeekBar.start();
        //self work for timer-----------
        String sDuration=convertFormat(myMediaPlayer.getDuration());
        String sCurrent=convertFormat(myMediaPlayer.getCurrentPosition());
        player_Duration.setText(sDuration);
        player_Position.setText(sCurrent);
//        songSeekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.design_default_color_primary), PorterDuff.Mode.MULTIPLY);
//        songSeekBar.getThumb().setColorFilter(getResources().getColor(R.color.design_default_color_primary),PorterDuff.Mode.SRC_IN);
        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    myMediaPlayer.seekTo(progress);
                }
                player_Position.setText(convertFormat(myMediaPlayer.getCurrentPosition()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myMediaPlayer.seekTo(seekBar.getProgress());

            }
        });
        btnPause.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                songSeekBar.setMax(myMediaPlayer.getDuration());
                if(myMediaPlayer.isPlaying()){
                    //btnPause.setBackgroundResource(R.drawable.ic_play);
                    btnPause.setImageResource(R.drawable.ic_play);
                    myMediaPlayer.pause();
                }else{
                    //btnPause.setBackgroundResource(R.drawable.icon_pause);
                    btnPause.setImageResource(R.drawable.icon_pause);
                    myMediaPlayer.start();
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                myMediaPlayer.stop();
                myMediaPlayer.release();
                btnPause.setImageResource(R.drawable.ic_play);
                //self work on timer when btnNext press--------------------------
               // myMediaPlayer.seekTo(0);
                position=(position+1)%mySong.size();
                Uri u=Uri.parse(mySong.get(position).toString());
                myMediaPlayer=MediaPlayer.create(getApplicationContext(),u);
                sName=mySong.get(position).getName().toString();
                lblSong.setText(sName);
                btnPause.setImageResource(R.drawable.icon_pause);
                songSeekBar.setMax(myMediaPlayer.getDuration());
                myMediaPlayer.start();
                //self work on timer when btnNext press--------------------------
                String sDuration=convertFormat(myMediaPlayer.getDuration());
                String sCurrent=convertFormat(myMediaPlayer.getCurrentPosition());
                player_Duration.setText(sDuration);
                player_Position.setText(sCurrent);
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                myMediaPlayer.stop();
                myMediaPlayer.release();
                btnPause.setImageResource(R.drawable.ic_play);
                position=((position-1<0))?(mySong.size()-1):(position-1);
                Uri u=Uri.parse(mySong.get(position).toString());
                myMediaPlayer=MediaPlayer.create(getApplicationContext(),u);
                sName=mySong.get(position).getName().toString();
                lblSong.setText(sName);
                btnPause.setImageResource(R.drawable.icon_pause);
                songSeekBar.setMax(myMediaPlayer.getDuration());
                myMediaPlayer.start();
                //self work on timer when btnNext press--------------------------
               // myMediaPlayer.seekTo(0);
                String sDuration=convertFormat(myMediaPlayer.getDuration());
                String sCurrent=convertFormat(myMediaPlayer.getCurrentPosition());
                player_Duration.setText(sDuration);
                player_Position.setText(sCurrent);
            }
        });
        //self work when song song complete--------------------
        myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnPause.setImageResource(R.drawable.ic_play);
                myMediaPlayer.seekTo(0);
            }
        });
    }
    // self work for timer function--------------
    private String convertFormat(int duration) {
        return String.format("%02d:%02d"
                ,TimeUnit.MILLISECONDS.toMinutes(duration)
                ,TimeUnit.MILLISECONDS.toSeconds(duration)
                -TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }
}