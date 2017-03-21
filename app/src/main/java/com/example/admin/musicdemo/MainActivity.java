package com.example.admin.musicdemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ListView listView;
    private SeekBar seekBar;
    private TextView tv_time,tv_name,tv_singer;
    private Button btn_up,btn_play,btn_next;
    private MyAdapter adapter;
    private MyService.MyBinder myBinder;
    private Intent intent;
    private ExecutorService service;//线程池
    private MyConn conn;
    private boolean flag=true;//用来判断是播放还是暂停
    private int id = 0;//id是我们当前要播放的歌曲
    private List<MusicInfo> data;//
    //在主线程中更新和显示进度条
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==100){
                seekBar.setProgress(msg.arg1);
                tv_time.setText(Utils.getTime(msg.arg1));
            }
        }
    };
    //绑定式启动服务
    private class MyConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder services) {
            myBinder= (MyService.MyBinder)services;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    //通过findViewById找到控件
    private void initView() {
        listView = (ListView) findViewById(R.id.listView);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        tv_name = (TextView) findViewById(R.id.musicName);
        tv_time = (TextView) findViewById(R.id.musicTime);
        tv_singer = (TextView) findViewById(R.id.musicSinger);
        btn_up = (Button) findViewById(R.id.musicUp);
        btn_play = (Button) findViewById(R.id.musicPlay);
        btn_next = (Button) findViewById(R.id.musicNext);
        data=new ArrayList<>();
        //设置按钮监听
        btn_up.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        service = Executors.newSingleThreadExecutor();//创建单线程池
        conn = new MyConn();//服务连接
        intent = new Intent(this,MyService.class);
        //绑定服务
        bindService(intent,conn,BIND_AUTO_CREATE);
        //
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //进度条改变的时候
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //当我们的歌曲播放完毕的时候
                if (progress==seekBar.getMax()){
                    if (id+1>=data.size()){
                        id=-1;
                    }
                    myBinder.stoped();
                    myBinder.played(data.get(++id).getMusicUrl());
                    tv_singer.setText(data.get(id).getMusicSinger());
                    tv_name.setText(data.get(id).getMusicName());
                    btn_play.setText("暂停");
                    initSeekBar();
                    setSeekBarProgress();
                    flag=false;
                }
            }
            //当我们开始拖动进度条的时候
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //暂停
                myBinder.pauseed();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myBinder.setCurrentProgressed(seekBar.getProgress());
                myBinder.played(data.get(id).getMusicUrl());
                tv_singer.setText(data.get(id).getMusicSinger());
                tv_name.setText(data.get(id).getMusicName());
                btn_play.setText("暂停");
                initSeekBar();
                setSeekBarProgress();
                flag=false;
            }
        });
    }

    public void onClick(View v){
        //通过点击不同按钮，做出不同的点击时间
        switch (v.getId()){
            case R.id.musicUp://点击上一曲
                if(id-1<0){
                    id=data.size();
                }
                myBinder.stoped();
                myBinder.played(data.get(--id).getMusicUrl());
                tv_singer.setText(data.get(id).getMusicSinger());
                tv_name.setText(data.get(id).getMusicName());
                btn_play.setText("暂停");
                initSeekBar();
                setSeekBarProgress();
                flag=false;
                break;
            case R.id.musicPlay://点击暂停或者播放
                if (flag){
                    myBinder.played(data.get(id).getMusicUrl());
                    tv_singer.setText(data.get(id).getMusicSinger());
                    tv_name.setText(data.get(id).getMusicName());
                    btn_play.setText("暂停");
                    initSeekBar();
                    setSeekBarProgress();
                    flag=!flag;
                }else {
                    myBinder.pauseed();
                    flag=!flag;
                    btn_play.setText("播放");
                }
                break;
            case R.id.musicNext://点击下一曲
                if(id>=data.size()-1){
                    id=-1;
                }
                myBinder.stoped();
                myBinder.played(data.get(++id).getMusicUrl());
                tv_singer.setText(data.get(id).getMusicSinger());
                tv_name.setText(data.get(id).getMusicName());
                btn_play.setText("暂停");
                initSeekBar();
                setSeekBarProgress();
                flag=false;
                break;
        }
    }
    //初始化seekBar的长度
    private void initSeekBar(){
        int width = myBinder.getMusicTimes();
        seekBar.setMax(width);
    }
    //动态更新seekBar的长度
    private void setSeekBarProgress(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true){
                    int position = myBinder.getCurrentProgressed();
                    Message message = new Message();
                    message.what = 100;
                    message.arg1 = position;
                    handler.sendMessage(message);
                }
            }
        };
        //提交线程
        service.submit(runnable);
    }

    //从我们的手机上面获取到音乐文件
    private List<MusicInfo> getMusic(){
        //定义一个存放音乐文件的集合
        final List<MusicInfo> info = new ArrayList<>();
        //新开一根线程来获取我们的音乐文件
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("AAA","getMusic");
                //把手机上面获取到的音乐文件 存放在游标cursor中 然后遍历
                Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null,null,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
                //如果我们获取到的音频文件不为空，就把他存放在集合里面
                if (cursor!=null&& cursor.getCount()>0){
                    while (cursor.moveToNext()){
                        //获取到我们的歌曲名
                        String musicName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                        //获取到我们的歌手的名字
                        String musicSinger = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        //获取到我们的歌曲的播放路径
                        String musicUrl = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        //获取到我们的歌曲的大小
                        long musicSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                        //获取到我们的歌曲的时间长短
                        long musicTime = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        //把我们获取到的音乐文件的数据添加到我们的集合中
                        info.add(new MusicInfo(musicName,musicSinger,musicUrl,musicSize,musicTime));
                    }
                    cursor.close();
                }else {
                    //如果我们获取到的数据为空或者失败，就添加我们的模拟数据s
                    for (int i=0;i<10;i++){
                        info.add(new MusicInfo("failed","failed","failed",123,123));
                    }
                }
            }
        }).start();
        //返回存放音乐的集合
        return info;
    }
    //从手机上获取到音乐文件并且显示到ListView上面
    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                data = getMusic();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new MyAdapter(MainActivity.this,data);
                        adapter.setMyInterface(new MyInterface() {
                            @Override
                            public void OnClick(int position) {
                                id=position;
                                myBinder.stoped();
                                myBinder.played(data.get(id).getMusicUrl());
                                tv_singer.setText(data.get(id).getMusicSinger());
                                tv_name.setText(data.get(id).getMusicName());
                                btn_play.setText("暂停");
                                initSeekBar();
                                setSeekBarProgress();
                                flag=false;
                            }
                        });
                        listView.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }
    //服务解绑
    @Override
    protected void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }
}
