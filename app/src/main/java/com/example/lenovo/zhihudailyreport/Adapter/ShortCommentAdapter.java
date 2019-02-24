package com.example.lenovo.zhihudailyreport.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.lenovo.zhihudailyreport.Bean.ShortComment;
import com.example.lenovo.zhihudailyreport.R;
import com.example.lenovo.zhihudailyreport.ShowShortComment;

import java.util.List;

public class ShortCommentAdapter extends RecyclerView.Adapter<ShortCommentAdapter.ViewHolder> {

    private List<ShortComment> list;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_userPic;
        TextView tv_commentator;
        TextView tv_content;
        TextView tv_popularity;
        TextView tv_time;
        View shortCommentView;             //这个view指的是自身（这一整个item）

        public ViewHolder(View view) {
            super(view);
            shortCommentView = view;
            iv_userPic = (ImageView) view.findViewById(R.id.iv_userPic);
            tv_commentator = (TextView) view.findViewById(R.id.tv_commentator);
            tv_content = (TextView) view.findViewById(R.id.tv_content);
            tv_popularity = (TextView) view.findViewById(R.id.tv_popularity);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
        }
    }

    public ShortCommentAdapter(List<ShortComment> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ShortCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.short_comment_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        //holder.phoneImage.setOnClickListener(new View.OnClickListener() {
           // @Override
           // public void onClick(View view) {
            //    int position = holder.getAdapterPosition();
             //   Phone phone = list.get(position);
             //   Toast.makeText(view.getContext(), phone.getName(), Toast.LENGTH_SHORT).show();
           // }
       // });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ShortCommentAdapter.ViewHolder viewHolder, int i) {
        ShortComment shortComment = list.get(i);
        viewHolder.iv_userPic.setImageBitmap(shortComment.getBitmap());
        viewHolder.tv_time.setText(String.valueOf(shortComment.getTime()));
        viewHolder.tv_popularity.setText(String.valueOf(shortComment.getPopularity()));
        viewHolder.tv_content.setText(shortComment.getContent());
        viewHolder.tv_commentator.setText(shortComment.getAuthor());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
