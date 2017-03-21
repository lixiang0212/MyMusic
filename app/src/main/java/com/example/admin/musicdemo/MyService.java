package com.example.admin.musicdemo;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;


public class MyService extends Service{
    //多媒体音乐播放器
    private MediaPlayer mediaPlayer;
    //绑定式启动服务
    class MyBinder extends Binder{
        //播放--直接调用我们外面写好的方法
        public void played(String path){
            try {
                play(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //暂停
        public void pauseed(){
            pause();
        }
        //停止
        public void stoped(){
            stop();
        }
        //获取到当前音乐的播放时间
        public int getMusicTimes(){
            return getMusicTime();
        }
        //获取到当前音乐的播放进度
        public int getCurrentProgressed(){
            return getCurrentProgress();
        }
        //设置我们的播放进度
        public void setCurrentProgressed(int progress){
            setCurrentProgress(progress);
        }


    }
    //播放
    @SuppressLint("NewApi")
    public  void play(String path) throws IOException {
        if (mediaPlayer==null){
            Log.i("AAA","==null");
            //创建多媒体播放器
            mediaPlayer = new MediaPlayer();
            //设置播放类型
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //设置播放路径
            mediaPlayer.setDataSource(path);
            //进入准备状态
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    Log.i("AAA","start()");
                }
            });
        }else {
            Log.i("AAA","!=null");
            //获取到已经播放的进度
            int position = getCurrentProgress();
            mediaPlayer.seekTo(position);
            mediaPlayer.start();
        }
    }

    //暂停
    public  void pause(){
        if (mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }else if (mediaPlayer!=null&&!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }
    //停止播放
    public void stop(){
        if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }
    //获取歌曲的时间长度
    public int getMusicTime(){
        if (mediaPlayer!=null){
            return mediaPlayer.getDuration();
        }else {
            return 0;
        }
    }
    //获取到当前播放的进度
    private int getCurrentProgress() {
        if (mediaPlayer!=null){
            return mediaPlayer.getCurrentPosition();
        }else {
            return 0;
        }
    }
    //设置当前的播放进度
    public void setCurrentProgress(int progress){
        if (mediaPlayer!=null){
            mediaPlayer.seekTo(progress);
        }
    }
    //退出的时候 停止音乐的播放
    public void onDestroy() {
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
        super.onDestroy();
    }
        @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }
}
