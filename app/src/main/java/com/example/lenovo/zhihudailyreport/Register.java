package com.example.lenovo.zhihudailyreport;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        dbHelper = new MyDatabaseHelper(this, "User.db", null, 3);
        dbHelper.getWritableDatabase();

        Button btn_register = (Button) findViewById(R.id.btn_register);
        Button btn_out = (Button) findViewById(R.id.btn_out);
        //退出到登录界面
        btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, FragmentActivity.class);
                startActivity(intent);
            }
        });
        //输入密码，同时密码不可见
        final EditText newPassword = (EditText) findViewById(R.id.newPassword);
        newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        final EditText repeatPassword=(EditText) findViewById(R.id.repeatPassword) ;
        repeatPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        final EditText name = (EditText) findViewById(R.id.name);
        name.setFilters(new InputFilter[] { new InputFilter.LengthFilter(15) });
        newPassword.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
        repeatPassword.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
        setEditTextInhibitInputSpace(name);
        setEditTextInhibitInputSpace(newPassword);
        //注册按钮
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldName;
                int i=1;
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                if (name.getText().toString().equals("")||newPassword.getText().toString().equals("")){
                    Toast.makeText(Register.this, "用户名或密码不能为空！", Toast.LENGTH_SHORT).show();
                    i =0;
                }
                else if (repeatPassword.getText().toString().equals("")){
                    Toast.makeText(Register.this, "请再次输入密码！", Toast.LENGTH_SHORT).show();
                    i =0;
                }
                else {
                    if (newPassword.getText().toString().equals(repeatPassword.getText().toString())) {
                        Cursor cursor = db.query("User", new String[]{"name"}, null, null, null, null, null);
                        if (cursor.moveToFirst()) {
                            do {
                                oldName = cursor.getString(cursor.getColumnIndex("name"));
                                if (name.getText().toString().equals(oldName)) {
                                    i = 0;
                                    Toast.makeText(Register.this, "用户名不能重复！", Toast.LENGTH_SHORT).show();
                                }
                            } while (cursor.moveToNext());
                        }
                        cursor.close();
                    }
                    else {
                        Toast.makeText(Register.this, "输入的两次密码必须一致！", Toast.LENGTH_SHORT).show();
                        i=2;
                    }
                    //添加数据
                    if (i==1){
                        ContentValues values = new ContentValues();
                        values.put("password", newPassword.getText().toString());
                        values.put("name",name.getText().toString());
                        values.put("job","");
                        values.put("sex","");
                        values.put("age","");
                        values.put("signature","");
                        values.put("picId","");
                        db.insert("User", null, values);
                        values.clear();
                        //界面跳转
                        Intent intent = new Intent(Register.this,Login.class);
                        intent.putExtra("userName",name.getText().toString());
                        intent.putExtra("password",newPassword.getText().toString());
                        startActivity(intent);
                        Toast.makeText(Register.this, "注册成功！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    public static void setEditTextInhibitInputSpace(EditText editText){
        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(source.equals(" "))
                    return "";
                else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
            return true;
        }
        return false;
    }
}
