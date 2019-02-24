package com.example.lenovo.zhihudailyreport;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lenovo.zhihudailyreport.Adapter.MessageAdapter;
import com.example.lenovo.zhihudailyreport.Bean.Message;

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

public class ColumnNews extends AppCompatActivity {
    private List<Message> messageList =new ArrayList<>();
    Bitmap bitmap;
    String newsId;
    String title;
    String picURL;
    String userName;
    String columnId;
    String date;
    private Context context;
    private MyDatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.column_news);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        columnId = String.valueOf(intent.getIntExtra("columnId",1));
        sendRequestWithHttpURLConnection();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        MessageAdapter messageAdapter = new MessageAdapter(messageList,userName);
        recyclerView.setAdapter(messageAdapter);
    }
    private void sendRequestWithHttpURLConnection(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try{
                    URL url = new URL("https://news-at.zhihu.com/api/4/section/"+columnId);
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
        try    {
            JSONObject jsonObject = new JSONObject(JsonData);
            JSONArray jsonArray =  jsonObject.getJSONArray("stories");
            for (int i = 0; i < jsonArray.length(); i++)    {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                title = jsonObject1.getString("title");
                JSONArray jsonArray1 =  jsonObject1.getJSONArray("images");
                picURL = jsonArray1.getString(0);
                newsId = jsonObject1.getString("id");
                date = jsonObject1.getString("display_date");
                bitmap = getHttpBitmap(picURL);
                Message message = new Message();
                message.setTitle(title);
                message.setBitmap(bitmap);
                message.setNewsId(newsId);
                message.setDate(date);
                messageList.add(message);
                showResponse();
            }
        }    catch (JSONException e)    {
            e.printStackTrace();
        }
    }
    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(linearLayoutManager);
                MessageAdapter messageAdapter = new MessageAdapter(messageList,userName);
                recyclerView.setAdapter(messageAdapter);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int flag = 0;
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        columnId = String.valueOf(intent.getIntExtra("columnId",1));
        String columnId0;
        dbHelper = new MyDatabaseHelper(this, "User.db", null, 3);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (item.getItemId()) {
            case R.id.like:
                if (userName == null){
                    Toast.makeText(ColumnNews.this, "您还没有登录~请点击头像登录", Toast.LENGTH_SHORT).show();
                }
                else {
                    Cursor cursor = db.query("Colum", new String[]{"userName", "columnId"}, "userName=?", new String[]{userName}, null, null, null);
                    if (cursor.moveToFirst()) {
                        do {
                            columnId0 = cursor.getString(cursor.getColumnIndex("columnId"));
                            if (columnId.equals(columnId0)) {
                                flag = 1;
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    if (flag == 1){
                        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
                        Snackbar.make(linearLayout,"该栏目已收藏，确定要取消收藏吗？",Snackbar.LENGTH_SHORT)
                                .setAction("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                                        db.delete("Colum", "userName = ? and columnId= ?", new String[] {userName,columnId});
                                        Toast.makeText(ColumnNews.this, "已取消", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .show();
                    }
                    else {
                        ContentValues values = new ContentValues();
                        values.put("userName", userName);
                        values.put("columnId",columnId);
                        db.insert("Colum", null, values);
                        values.clear();
                        Toast.makeText(ColumnNews.this, "已收藏", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
        }
        return true;
    }
}
