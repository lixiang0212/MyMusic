package com.example.admin.musicdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;


public class MyAdapter extends BaseAdapter{
    private Context context;//上下文对象
    private LayoutInflater inflater;//布局解析器
    private List<MusicInfo> datas;//数据源--数据是MainActivity传过来的
    private MyInterface myInterface;

    public void setMyInterface(MyInterface myInterface) {
        this.myInterface = myInterface;
    }

    //构造方法
    public MyAdapter(Context context,List<MusicInfo> datas){
        this.context=context;
        this.datas=datas;
        inflater=LayoutInflater.from(context);
    }

    //ListView一共有多少项
    @Override
    public int getCount() {
        return datas.size();
    }
    //返回第positon项的数据
    @Override
    public MusicInfo getItem(int position) {
        return datas.get(position);
    }
    //返回第positon项的数据的id
    @Override
    public long getItemId(int position) {
        return position;
    }
    //设置视图
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        //如果是第一次加载，就把布局文件解析过来
        if (convertView==null){
            holder = new ViewHolder();
            //解析布局文件
            convertView = inflater.inflate(R.layout.list_item,null);
            //通过我们解析的布局文件再找到相应的控件
            holder.tv_name = (TextView) convertView.findViewById(R.id.name);
            holder.tv_singer = (TextView) convertView.findViewById(R.id.singer);
            holder.tv_size = (TextView) convertView.findViewById(R.id.size);
            holder.tv_time = (TextView) convertView.findViewById(R.id.time);
            holder.relativeLayout= (RelativeLayout) convertView.findViewById(R.id.relativeLayout);
            //把我门解析好的布局文件保存起来
            convertView.setTag(holder);
        }else {
            //如果是第二次或者是第N次,直接取出前面我们已经解析好的布局文件
            holder = (ViewHolder) convertView.getTag();
        }
        //获取到第postion行的数据并且展示在相关的控件上
        MusicInfo info = datas.get(position);
        holder.tv_name.setText(info.getMusicName());
        holder.tv_singer.setText(info.getMusicSinger());
        holder.tv_size.setText(Utils.getSize(info.getMusicSize()));
        holder.tv_time.setText(Utils.getTime(info.getMusicTime()));
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myInterface.OnClick(position);
            }
        });
        //返回我们已经设计好的试图
        return convertView;
    }
    //ViewHoider重复利用，节约内存
    private class ViewHolder{
        private TextView tv_name,tv_singer,tv_size,tv_time;
        private RelativeLayout relativeLayout;
    }

}
