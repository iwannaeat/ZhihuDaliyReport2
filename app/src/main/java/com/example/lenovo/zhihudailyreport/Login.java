package com.example.lenovo.zhihudailyreport;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    public String realName;
    public String realPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //设置密码为不可见
        final EditText et_password = (EditText) findViewById(R.id.et_password);
        et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        //登录功能
        final EditText et_name = (EditText)findViewById(R.id.et_name);
        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        String password = intent.getStringExtra("password");
        if (userName != null && password != null){
            et_name.setText(userName);
            et_name.setSelection(userName.length());
            et_password.setText(password);
            et_password.setSelection(password.length());
        }
        dbHelper = new MyDatabaseHelper(this, "User.db", null, 3);
        //跳转到注册界面
        Button et_register = (Button) findViewById(R.id.btn_register);
        et_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        Button btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 0;
                //查询数据
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor cursor = db.query("User", new String[]{"name", "password"}, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        realName = cursor.getString(cursor.getColumnIndex("name"));
                        realPassword = cursor.getString(cursor.getColumnIndex("password"));
                        if (et_name.getText().toString().equals(realName) && et_password.getText().toString().equals(realPassword)) {
                            i = 1;
                            String userName = et_name.getText().toString();
                            Intent intent = new Intent(Login.this, FragmentActivity.class);
                            intent.putExtra("userName", et_name.getText().toString());
                            startActivity(intent);
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
                if (i == 0) {
                    Toast.makeText(Login.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Login.this, FragmentActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }
}
