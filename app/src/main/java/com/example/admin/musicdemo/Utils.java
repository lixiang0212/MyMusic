package com.example.admin.musicdemo;

/**
 * Created by 123 on 2017/1/4.
 */
public class Utils {

    //歌曲大小转换
    public static String getSize(long size){
        float a = size;
        if (a<1024){
            return a+"b";
        }else if (a<1024*1024){
            String b=a/1024+"";
            String c=b.substring(0,3);
            return c+"kb";
        }else {
            String b = a/(1024*1024)+"";
            String c = b.substring(0,3);
            return c+"M";
        }
    }
    //歌曲时间转换
    public static String getTime(long size){
        int a=(int) (size/1000);
        if (a<60){
            return "0:"+a;
        }else {
            int b=a/60;
            int c=a%60;
            return b+":"+c;
        }
    }
}
