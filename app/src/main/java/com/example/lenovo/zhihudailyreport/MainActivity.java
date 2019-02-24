package com.example.lenovo.zhihudailyreport;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.zhihudailyreport.Adapter.NewsAdapter;
import com.example.lenovo.zhihudailyreport.Bean.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;
    private SwipeRefreshLayout swipeRefresh;
    private DrawerLayout drawerLayout;
    private List<News>  NewsList =new ArrayList<>();
    Bitmap bitmap;
    String newsId;
    String title;
    String picURL;
    String userName;
    String picId = null;
    String signature;
    private Context context;

    public static final int CHOOSE_PHOTO = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        dbHelper = new MyDatabaseHelper(this, "User.db", null, 3);
        dbHelper.getWritableDatabase();
        //下拉刷新功能
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.color1);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshNews();
            }
        });
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //抽屉菜单
        drawerLayout = (DrawerLayout) findViewById(R.id.user_drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
        }
        //接收用户信息并判断
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        //滚动界面
        sendRequestWithHttpURLConnection();
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        if (userName != null){
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query("User", new String[]{"name","picId","signature"}, "name=?", new String[]{userName}, null, null,null);
            if(cursor.moveToFirst()) {
                do {
                    picId = cursor.getString(cursor.getColumnIndex("picId"));
                    signature = cursor.getString(cursor.getColumnIndex("signature"));
                } while(cursor.moveToNext());
            }
            cursor.close();
            CircleImageView circleImageView = (CircleImageView) headerView.findViewById(R.id.user_image);
            Bitmap bitmap = BitmapFactory.decodeFile(picId);
            circleImageView.setImageBitmap(bitmap);
            TextView tv_userName = (TextView) headerView.findViewById(R.id.tv_userName);
            TextView tv_signature = (TextView) headerView.findViewById(R.id.tv_signature);
            tv_signature.setText(signature);
            tv_userName.setText(userName);
        }
        else {
            TextView tv_userName = (TextView) headerView.findViewById(R.id.tv_userName);
            tv_userName.setText("未登录");
        }
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userName == null){
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                }
                else {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(MainActivity.this,new
                                String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE },1);
                    }else {
                        openAlbum();
                    }
                }
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
                switch (item.getItemId()) {
                    case R.id.information:
                        if(userName == null){
                            Toast.makeText(MainActivity.this, "您还没有登录~请点击头像登录", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent intent = new Intent(MainActivity.this, Information.class);
                            intent.putExtra("userName",userName);
                            startActivity(intent);
                        }
                        break;
                    case R.id.like:
                        if(userName == null){
                            Toast.makeText(MainActivity.this, "您还没有登录~请点击头像登录", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent intent = new Intent(MainActivity.this, Like_news.class);
                            intent.putExtra("userName",userName);
                            startActivity(intent);
                        }
                        break;
                    case R.id.column:
                        if(userName == null){
                            Toast.makeText(MainActivity.this, "您还没有登录~请点击头像登录", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent intent = new Intent(MainActivity.this, Like_column.class);
                            intent.putExtra("userName",userName);
                            startActivity(intent);
                        }
                        break;
                    case R.id.change_password:
                        if(userName == null){
                            Toast.makeText(MainActivity.this, "您还没有登录~请点击头像登录", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent intent = new Intent(MainActivity.this, ChangePassword.class);
                            intent.putExtra("userName",userName);
                            startActivity(intent);
                        }
                        break;
                    case R.id.out:
                        if(userName == null){
                            Toast.makeText(MainActivity.this, "您还没有登录~请点击头像登录", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            userName = null;
                            Toast.makeText(MainActivity.this, "已退出当前帐号", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            intent.putExtra("userName",userName);
                            startActivity(intent);
                        }
                        break;
                    case R.id.delete:
                        if(userName != null){
                            Snackbar.make(drawerLayout,"确定要注销吗",Snackbar.LENGTH_SHORT)
                                    .setAction("确定", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                                            db.delete("User", "name == ?", new String[] {userName});
                                            Toast.makeText(MainActivity.this, "已注销", Toast.LENGTH_SHORT).show();
                                            userName = null;
                                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                            intent.putExtra("userName",userName);
                                            startActivity(intent);
                                        }
                                    })
                                    .show();
                        }
                        break;
                    default:
                }
                return true;
            }
        });
    }
    //上传图片
    private void openAlbum (){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(this,"您已禁止此项权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK){
                    if(Build.VERSION.SDK_INT >= 19){
                        handleImageOnKitKat(data);
                    }else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }
        Intent intent = getIntent();
        String name = intent.getStringExtra("userName");
        displayImage(imagePath);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("picId",imagePath);
        db.update("User",values, "name = ?", new String[]{name});
        values.clear();
    }
    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        Intent intent = getIntent();
        String name = intent.getStringExtra("userName");
        String imagePath = getImagePath(uri,null);//保存imagePath
        displayImage(imagePath);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("picId",imagePath);
        db.update("User",values, "name = ?", new String[]{name});
        values.clear();
    }
    private  String getImagePath(Uri uri, String selection){
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void displayImage(String imagePath){
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        CircleImageView circleImageView = (CircleImageView) headerView.findViewById(R.id.user_image);
        if (imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            circleImageView.setImageBitmap(bitmap);
        }else {
            Toast.makeText(this,"获取图像失败",Toast.LENGTH_SHORT).show();
        }
    }
    //联网内容
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
            JSONArray jsonArray =  jsonObject.getJSONArray("recent");
            for (int i = 0; i < jsonArray.length(); i++)    {
               JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                title = jsonObject1.getString("title");
                picURL = jsonObject1.getString("thumbnail");
                newsId = jsonObject1.getString("news_id");
                bitmap = getHttpBitmap(picURL);
                News news = new News();
                news.setTitle(title);
                news.setBitmap(bitmap);
                news.setNewsId(newsId);
                NewsList.add(news);
            }
            Log.e("MainActivity", "ListSize="+NewsList.size());
            showResponse();
        }    catch (JSONException e)    {
            e.printStackTrace();
            Log.e("MainActivity", e.getMessage());
        }
    }
    //抽屉菜单
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_main,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.column:
                Intent intent = new Intent(MainActivity.this, ShowColumn.class);
                intent.putExtra("userName",userName);
                startActivity(intent);
            default:
        }
        return true;
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
    //监听back键
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }
}
