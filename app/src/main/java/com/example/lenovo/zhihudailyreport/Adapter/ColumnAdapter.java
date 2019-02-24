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

import com.example.lenovo.zhihudailyreport.Bean.Column;
import com.example.lenovo.zhihudailyreport.Bean.News;
import com.example.lenovo.zhihudailyreport.ColumnNews;
import com.example.lenovo.zhihudailyreport.R;
import com.example.lenovo.zhihudailyreport.ShowNews;

import java.util.List;

public class ColumnAdapter extends RecyclerView.Adapter<ColumnAdapter.ViewHolder>{
    private List<Column> list;
    private Context context;
    public String userName;

    public ColumnAdapter(List<Column> list,String userName1){
        userName = userName1;
        this.list = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView iv_column;
        TextView tv_title;
        TextView tv_content;
        View columnView;             //这个view指的是自身（这一整个item）

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            columnView = view;
            iv_column = (ImageView) view.findViewById(R.id.iv_column);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_content = (TextView) view.findViewById(R.id.tv_content);
        }
    }

    public ColumnAdapter(List<Column> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ColumnAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (context == null){
            context = viewGroup.getContext();
        }
        View view = LayoutInflater.from(context)
                .inflate(R.layout.column_item, viewGroup, false);
        final ColumnAdapter.ViewHolder holder = new ColumnAdapter.ViewHolder(view);
        holder.columnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Column column = list.get(position);
                View columnView = view;
                int columnId = column.getColumnId();
                Intent intent = new Intent();
                intent.setClass(columnView .getContext(),ColumnNews.class );
                intent.putExtra("columnId", columnId);
                intent.putExtra("userName", userName);
                columnView.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ColumnAdapter.ViewHolder viewHolder, int i) {
        Column column = list.get(i);
        viewHolder.tv_title.setText(column.getTitle());
        viewHolder.iv_column.setImageBitmap(column.getBitmap());
        viewHolder.tv_content.setText(column.getContent());
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
