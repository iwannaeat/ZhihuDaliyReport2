package com.example.lenovo.zhihudailyreport.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.zhihudailyreport.Bean.Message;
import com.example.lenovo.zhihudailyreport.Bean.News;
import com.example.lenovo.zhihudailyreport.R;
import com.example.lenovo.zhihudailyreport.ShowNews;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    private List<Message> list;
    private Context context;
    public String userName;

    public MessageAdapter(List<Message> list,String userName1){
        userName = userName1;
        this.list = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView messageImage;
        TextView title;
        View messageView;
        TextView date;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            messageView = view;
            messageImage = (ImageView) view.findViewById(R.id.news_pic);
            title = (TextView) view.findViewById(R.id.tv_title);
            date = (TextView) view.findViewById(R.id.tv_date);
        }
    }

    public MessageAdapter(List<Message> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (context == null){
            context = viewGroup.getContext();
        }
        View view = LayoutInflater.from(context)
                .inflate(R.layout.message_item, viewGroup, false);
        final MessageAdapter.ViewHolder holder = new MessageAdapter.ViewHolder(view);
        holder.messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Message message = list.get(position);
                View messageView = view;
                String newsId = message.getNewsId();
                Intent intent = new Intent();
                intent.setClass(messageView .getContext(),ShowNews.class );
                intent.putExtra("newsId", newsId);
                intent.putExtra("userName", userName);
                messageView.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder viewHolder, int i) {
        Message message = list.get(i);
        viewHolder.title.setText(message.getTitle());
        viewHolder.date.setText(message.getDate());
        viewHolder.messageImage.setImageBitmap(message.getBitmap());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
