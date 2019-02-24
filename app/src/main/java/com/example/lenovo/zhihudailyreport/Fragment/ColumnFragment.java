package com.example.lenovo.zhihudailyreport.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lenovo.zhihudailyreport.Adapter.ColumnAdapter;
import com.example.lenovo.zhihudailyreport.Bean.Column;
import com.example.lenovo.zhihudailyreport.R;

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

public class ColumnFragment extends Fragment {
    View view;
    SwipeRefreshLayout swipeRefresh;
    private List<Column> columnList =new ArrayList<>();
    Bitmap bitmap;
    int columnId;
    String title;
    String picURL;
    String userName;
    String picId = null;
    String content;
    String ip;
    private Context context;
    String tab;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_column,container,false);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //下拉刷新功能
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.color1);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshNews();
            }
        });

        //接收数据
        Bundle bundle = getArguments();
        if(bundle != null) userName = bundle.getString("userName");

        sendRequestWithHttpURLConnection();

    }

    //联网内容
    private void sendRequestWithHttpURLConnection(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try{
                    URL url = new URL("https://news-at.zhihu.com/api/4/sections");
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
                    Log.e("MainActivity", response.toString());
                    parseJSONWithJSONObject(response.toString());
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("MainActivity", e.getMessage());
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
            JSONArray jsonArray =  jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++)    {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                title = jsonObject1.getString("name");
                picURL = jsonObject1.getString("thumbnail");
                columnId = jsonObject1.getInt("id");
                content = jsonObject1.getString("description");
                bitmap = getHttpBitmap(picURL);
                Column column = new Column();
                column.setTitle(title);
                column.setBitmap(bitmap);
                column.setColumnId(columnId);
                column.setContent(content);
                columnList.add(column);
            }
            Log.e("MainActivity", "ListSize="+columnList.size());
            showResponse();
        }    catch (JSONException e)    {
            e.printStackTrace();
            Log.e("MainActivity", e.getMessage());
        }
    }

    private List<Column> getColumn(){
        Column column = new Column();
        column.setTitle(title);
        column.setBitmap(bitmap);
        column.setColumnId(columnId);
        column.setContent(content);
        columnList.add(column);
        return columnList;
    }

    private void showResponse() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
                ColumnAdapter adapter = new ColumnAdapter(getColumn(),userName);
                adapter.setHasStableIds(true);
                recyclerView.setAdapter(adapter);
            }
        });
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        columnList.clear();
                        sendRequestWithHttpURLConnection();
                        ColumnAdapter columnAdapter = new ColumnAdapter(getColumn(),userName);
                        columnAdapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
}
