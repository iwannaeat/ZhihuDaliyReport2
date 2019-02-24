package com.example.lenovo.zhihudailyreport.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.lenovo.zhihudailyreport.Bean.News;
import com.example.lenovo.zhihudailyreport.R;
import com.example.lenovo.zhihudailyreport.ShowNews;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<News> list;
    private Context context;
    public String userName;

    public NewsAdapter(List<News> list,String userName1){
        userName = userName1;
        this.list = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView newsImage;
        TextView title;
        View newsView;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            newsView = view;
            newsImage = (ImageView) view.findViewById(R.id.news_pic);
            title = (TextView) view.findViewById(R.id.tv_title);
        }
    }

    public NewsAdapter(List<News> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (context == null){
            context = viewGroup.getContext();
        }
        View view = LayoutInflater.from(context)
                .inflate(R.layout.news_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.newsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               int position = holder.getAdapterPosition();
               News news = list.get(position);
               String newsId = news.getNewsId();
               Intent intent = new Intent();
               intent.setClass(view .getContext(),ShowNews.class );
               intent.putExtra("newsId", newsId);
               intent.putExtra("userName", userName);
               view.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder viewHolder, int i) {
        News news = list.get(i);
        viewHolder.title.setText(news.getTitle());
        viewHolder.newsImage.setImageBitmap(news.getBitmap());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
}
