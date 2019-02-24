package com.example.lenovo.zhihudailyreport;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;

import com.example.lenovo.zhihudailyreport.Adapter.NewsAdapter;
import com.example.lenovo.zhihudailyreport.Bean.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Like_news extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    private SwipeRefreshLayout swipeRefresh;
    private List<News> NewsList =new ArrayList<>();
    Bitmap bitmap;
    String newsId;
    String title;
    String picURL;
    String userName;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.like_news);
        context = this;
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        dbHelper = new MyDatabaseHelper(this, "User.db", null, 3);
        //下拉刷新功能
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.color1);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshNews();
            }
        });
        sendRequestWithHttpURLConnection();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        NewsAdapter newsAdapter = new NewsAdapter(NewsList,userName);
        recyclerView.setAdapter(newsAdapter);
    }
    private void sendRequestWithHttpURLConnection(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try{
                    URL url = new URL("https://news-at.zhihu.com/api/4/news/hot");
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
                    Log.d("MainActivity", response.toString());
                    parseJSONWithJSONObject(response.toString());
                }catch (Exception e){
                    e.printStackTrace();
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
    public static Bitmap getHttpBitmap(String url){
        URL myFileURL;
        Bitmap bitmap=null;
        try{
            myFileURL = new URL(url);
            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
            conn.setConnectTimeout(6000);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }
    //解析数据
    private void parseJSONWithJSONObject(String JsonData) {
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        String newsId0;
        int flag = 0;
        try    {
            JSONObject jsonObject = new JSONObject(JsonData);
            JSONArray jsonArray =  jsonObject.getJSONArray("recent");
            for (int i = 0; i < jsonArray.length(); i++)    {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                title = jsonObject1.getString("title");
                picURL = jsonObject1.getString("thumbnail");
                newsId = jsonObject1.getString("news_id");
                bitmap = getHttpBitmap(picURL);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
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
                    News news = new News();
                    news.setTitle(title);
                    news.setBitmap(bitmap);
                    news.setNewsId(newsId);
                    NewsList.add(news);
                    flag = 0;
                }
                showResponse();
            }
        }    catch (JSONException e)    {
            e.printStackTrace();
        }
    }
    //下拉刷新
    private void refreshNews(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(2000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        NewsList.clear();
                        sendRequestWithHttpURLConnection();
                        NewsAdapter newsAdapter = new NewsAdapter(NewsList,userName);
                        newsAdapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(linearLayoutManager);
                NewsAdapter newsAdapter = new NewsAdapter(NewsList,userName);
                recyclerView.setAdapter(newsAdapter);
            }
        });
    }
}
