package com.example.lenovo.zhihudailyreport;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.logging.Level;

public class Comments extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments);
        Intent intent1 = getIntent();
        final String shortComment = intent1.getStringExtra("shortComment");
        final String longComment = intent1.getStringExtra("longComment");
        final String newsId = intent1.getStringExtra("newsId");
        final String userName = intent1.getStringExtra("userName");
        LinearLayout long_comment = (LinearLayout) findViewById(R.id.long_comment);
        final LinearLayout short_comment = (LinearLayout) findViewById(R.id.short_comment);
        long_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (longComment.equals("0")){
                    Toast.makeText(Comments.this, "无长评论", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(Comments.this,ShowLongComment.class);
                    intent.putExtra("userName",userName);
                    intent.putExtra("newsId",newsId);
                    intent.putExtra("shortComment",shortComment);
                    intent.putExtra("longComment",longComment);
                    startActivity(intent);
                }
                }
        });
        short_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shortComment.equals("0")){
                    Toast.makeText(Comments.this, "无短评论", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(Comments.this,ShowShortComment.class);
                    intent.putExtra("userName",userName);
                    intent.putExtra("newsId",newsId);
                    intent.putExtra("shortComment",shortComment);
                    intent.putExtra("longComment",longComment);
                    startActivity(intent);
                }
            }
        });
        showResponse(shortComment,longComment);
    }
    private void showResponse(String shortComment1,String longComment1) {
        final String shortComment;
        final String longComment;
        shortComment = shortComment1;
        longComment = longComment1;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tv_longComment = (TextView) findViewById(R.id.tv_longComment);
                TextView tv_shortComment = (TextView)findViewById(R.id.tv_shortComment);
                tv_longComment.setText(longComment);
                tv_shortComment.setText(shortComment);
            }
        });
    }
}
