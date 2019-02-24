package com.example.lenovo.zhihudailyreport;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;

import com.example.lenovo.zhihudailyreport.Adapter.LongCommentAdapter;
import com.example.lenovo.zhihudailyreport.Bean.LongComment;

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

public class ShowLongComment extends AppCompatActivity {
    Bitmap bitmap;
    String author;
    String content;
    String avatar;
    int time;
    int likes;
    private List<LongComment> longCommentList =new ArrayList<>();

    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.short_comment);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        final String newsId = intent.getStringExtra("newsId");
        final String userName = intent.getStringExtra("userName");
        final String shortComment = intent.getStringExtra("shortComment");
        final String longComment = intent.getStringExtra("longComment");
        sendRequestWithHttpURLConnection();
    }
    //联网内容
    private void sendRequestWithHttpURLConnection(){
        new Thread(new Runnable() {
            Intent intent = getIntent();
            final String newsId = intent.getStringExtra("newsId");
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try{
                    URL url = new URL("https://news-at.zhihu.com/api/4/story/" + newsId + "/long-comments");
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
                    Log.e("ShortComment", response.toString());
                    parseJSONWithJSONObject(response.toString());
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("ShortComment", e.getMessage());
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
        try    {
            JSONObject jsonObject = new JSONObject(JsonData);
            JSONArray jsonArray =  jsonObject.getJSONArray("comments");
            for (int i = 0; i < jsonArray.length(); i++)    {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                author = jsonObject1.getString("author");
                avatar = jsonObject1.getString("avatar");
                time = jsonObject1.getInt("time");
                likes = jsonObject1.getInt("likes");
                content = jsonObject1.getString("content");
                bitmap = getHttpBitmap(avatar);
                LongComment longComment = new LongComment();
                longComment.setBitmap(bitmap);
                longComment.setAuthor(author);
                longComment.setContent(content);
                longComment.setPopularity(likes);
                longComment.setTime(time);
                longCommentList.add(longComment);
            }
            Log.e("ShortComment", "ListSize="+longCommentList.size());
            showResponse();
        }    catch (JSONException e)    {
            e.printStackTrace();
            Log.e("ShortComment", e.getMessage());
        }
    }
    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(linearLayoutManager);
                LongCommentAdapter longCommentAdapter = new LongCommentAdapter(longCommentList);
                recyclerView.setAdapter(longCommentAdapter);
            }
        });
    }
}
