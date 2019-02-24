package com.example.lenovo.zhihudailyreport;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String CREATE_USER = "create table User ("
            + "id integer primary key autoincrement, "
            + "name text, "
            + "sex text,"
            + "job text,"
            + "age integer,"
            + "signature text,"
            + "picId text,"
            + "password text)";
    private static final String CREATE_NEWS= "create table News ("
            + "id integer primary key autoincrement, "
            + "newsId text,"
            + "userName text)";
    private static final String CREATE_COLUM= "create table Colum ("
            + "id integer primary key autoincrement, "
            + "columnId integer,"
            + "userName text)";
    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_NEWS);
        db.execSQL(CREATE_COLUM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists User");
        db.execSQL("drop table if exists News");
        db.execSQL("drop table if exists Colum");
        onCreate(db);
    }
}