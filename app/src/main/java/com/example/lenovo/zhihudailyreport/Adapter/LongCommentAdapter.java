package com.example.lenovo.zhihudailyreport.Adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.zhihudailyreport.Bean.LongComment;
import com.example.lenovo.zhihudailyreport.Bean.News;
import com.example.lenovo.zhihudailyreport.R;

import java.util.List;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import com.example.lenovo.zhihudailyreport.R;
import com.example.lenovo.zhihudailyreport.ShowNews;
import com.example.lenovo.zhihudailyreport.ShowShortComment;

import java.util.List;

public class LongCommentAdapter extends RecyclerView.Adapter<LongCommentAdapter.ViewHolder> {

    private List<LongComment> list;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_userPic;
        TextView tv_commentator;
        TextView tv_content;
        TextView tv_popularity;
        TextView tv_time;
        View longCommentView;             //这个view指的是自身（这一整个item）
        public ViewHolder(View view) {
            super(view);
            longCommentView = view;
            iv_userPic = (ImageView) view.findViewById(R.id.iv_userPic);
            tv_commentator = (TextView) view.findViewById(R.id.tv_commentator);
            tv_content = (TextView) view.findViewById(R.id.tv_content);
            tv_popularity = (TextView) view.findViewById(R.id.tv_popularity);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
        }
    }
    public LongCommentAdapter(List<LongComment> list) {
        this.list = list;
    }
    @NonNull
    @Override
    public LongCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.long_comment_item, viewGroup, false);
        final LongCommentAdapter.ViewHolder holder = new LongCommentAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LongCommentAdapter.ViewHolder viewHolder, int i) {
        LongComment longComment = list.get(i);
        viewHolder.iv_userPic.setImageBitmap(longComment.getBitmap());
        viewHolder.tv_time.setText(String.valueOf(longComment.getTime()));
        viewHolder.tv_popularity.setText(String.valueOf(longComment.getPopularity()));
        viewHolder.tv_content.setText(longComment.getContent());
        viewHolder.tv_commentator.setText(longComment.getAuthor());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

