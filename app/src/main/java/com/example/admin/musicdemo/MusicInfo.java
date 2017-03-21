package com.example.admin.musicdemo;

public class MusicInfo {
    private String musicName; //歌曲名
    private String musicSinger;//歌手
    private String musicUrl;//歌曲播放路径
    private long musicSize;//歌曲大小
    private long musicTime;//歌曲时间长短

    //构造方法
    public MusicInfo(String musicName, String musicSinger, String musicUrl, long musicSize, long musicTime) {
        this.musicName = musicName;
        this.musicSinger = musicSinger;
        this.musicUrl = musicUrl;
        this.musicSize = musicSize;
        this.musicTime = musicTime;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicSinger() {
        return musicSinger;
    }

    public void setMusicSinger(String musicSinger) {
        this.musicSinger = musicSinger;
    }

    public String getMusicUrl() {
        return musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public long getMusicSize() {
        return musicSize;
    }

    public void setMusicSize(long musicSize) {
        this.musicSize = musicSize;
    }

    public long getMusicTime() {
        return musicTime;
    }

    public void setMusicTime(long musicTime) {
        this.musicTime = musicTime;
    }
}
