package com.example.lenovo.zhihudailyreport;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ShowNews extends AppCompatActivity {
    int long_comments ;
    int short_comments ;
    private MyDatabaseHelper dbHelper;

    private static final int COMPLETED = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String newsId = intent.getStringExtra("newsId");
        String userName = intent.getStringExtra("userName");
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://daily.zhihu.com/story/" + newsId);
        sendRequestWithHttpURLConnection();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        final MenuItem item = menu.findItem(R.id.comment);
        item.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(item);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int flag = 0;
        Intent intent = getIntent();
        final String newsId = intent.getStringExtra("newsId");
        final String userName = intent.getStringExtra("userName");
        String newsId0;
        dbHelper = new MyDatabaseHelper(this, "User.db", null, 3);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (item.getItemId()) {
            case R.id.like:
                if (userName == null){
                    Toast.makeText(ShowNews.this, "您还没有登录~请点击头像登录", Toast.LENGTH_SHORT).show();
                }
                else {
                    Cursor cursor = db.query("News", new String[]{"userName", "newsId"}, "userName=?", new String[]{userName}, null, null, null);
                    if (cursor.moveToFirst()) {
                        do {
                            newsId0 = cursor.getString(cursor.getColumnIndex("newsId"));
                            if (newsId.equals(newsId0)) {
                                flag = 1;
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    if (flag == 1){
                        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
                        Snackbar.make(linearLayout,"该新闻已收藏，确定要取消收藏吗？",Snackbar.LENGTH_SHORT)
                                .setAction("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                                        db.delete("News", "userName = ? and newsId= ?", new String[] {userName,newsId});
                                        Toast.makeText(ShowNews.this, "已取消", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .show();
                    }
                    else {
                        ContentValues values = new ContentValues();
                        values.put("userName", userName);
                        values.put("newsId",newsId);
                        db.insert("News", null, values);
                        values.clear();
                        Toast.makeText(ShowNews.this, "已收藏", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.comment:
                TextView tv_popular = (TextView) findViewById(R.id.tv_popular);
                TextView tv_comment = (TextView)findViewById(R.id.tv_comment);
                Intent intent1 = new Intent(ShowNews.this,Comments.class);
                intent1.putExtra("userName",userName);
                intent1.putExtra("newsId",newsId);
                intent1.putExtra("shortComment",String.valueOf(short_comments));
                intent1.putExtra("longComment",String.valueOf(long_comments));
                startActivity(intent1);
                break;
        }
        return true;
    }
    //联网内容
    private void sendRequestWithHttpURLConnection(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                Intent intent = getIntent();
                String newsId = intent.getStringExtra("newsId");
                try{
                    URL url = new URL("https://news-at.zhihu.com/api/4/story-extra/" + newsId);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine())!=null){
                        response.append(line);
                    }
                    Log.e("ShowNews", response.toString());
                    parseJSONWithJSONObject(response.toString());
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("ShowNews", e.getMessage());
                }finally {
                    if (reader != null){
                        try{
                            reader.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    if (connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
    //解析数据
    private void parseJSONWithJSONObject(String JsonData) {
        try    {
            JSONObject jsonObject = new JSONObject(JsonData);
            long_comments = jsonObject.getInt("long_comments");
            short_comments = jsonObject.getInt("short_comments");
            int comments = jsonObject.getInt("comments");
            int popularity = jsonObject.getInt("popularity");
            showResponse(comments,popularity);
        }   catch (JSONException e)    {
            e.printStackTrace();
            Log.e("MainActivity", e.getMessage());
        }
    }
    private void showResponse(int comments1,int popularity1) {
        final int comments;
        final int popularity;
        comments = comments1;
        popularity = popularity1;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tv_popular = (TextView) findViewById(R.id.tv_popular);
                TextView tv_comment = (TextView)findViewById(R.id.tv_comment);
                if(!String.valueOf(comments).equals("")) tv_comment.setText(String.valueOf(comments));
                if(!String.valueOf(popularity).equals("")) tv_popular.setText(String.valueOf(popularity));
            }
        });
    }

}