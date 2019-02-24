package com.example.lenovo.zhihudailyreport;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePassword extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    public String realName;
    public String realPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        dbHelper = new MyDatabaseHelper(this, "User.db", null, 3);
        final EditText et_oldPassword = (EditText) findViewById(R.id.et_oldPassword);
        final EditText et_newPassword = (EditText) findViewById(R.id.et_newPassword);
        final EditText et_repeatPassword = (EditText) findViewById(R.id.et_repeatPassword);
        et_oldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        et_newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        et_repeatPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        et_newPassword.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
        et_repeatPassword.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
        Button btn_reset = (Button) findViewById(R.id.btn_reset);
        Button btn_out = (Button) findViewById(R.id.btn_out);
        final String userName;
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        setEditTextInhibitInputSpace(et_newPassword);
        setEditTextInhibitInputSpace(et_oldPassword);
        setEditTextInhibitInputSpace(et_repeatPassword);
        btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangePassword.this, FragmentActivity.class);
                intent.putExtra("userName",userName);
                startActivity(intent);
            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 0;
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor cursor = db.query("User", new String[]{"name", "password"}, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        realName = cursor.getString(cursor.getColumnIndex("name"));
                        realPassword = cursor.getString(cursor.getColumnIndex("password"));
                        if (userName.equals(realName) && et_oldPassword.getText().toString().equals(realPassword)) {
                            i = 1;
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
                if (i == 0) Toast.makeText(ChangePassword.this, "密码错误", Toast.LENGTH_SHORT).show();
                else if (et_newPassword.getText().toString().equals("")||et_repeatPassword.getText().toString().equals("")){
                    Toast.makeText(ChangePassword.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (et_newPassword.getText().toString().equals(et_repeatPassword.getText().toString())){
                        ContentValues values = new ContentValues();
                        values.put("password",et_newPassword.getText().toString());
                        db.update("User",values, "name = ?", new String[]{userName});
                        values.clear();
                        Toast.makeText(ChangePassword.this, "修改成功！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChangePassword.this, FragmentActivity.class);
                        intent.putExtra("userName",userName);
                        startActivity(intent);
                    }
                    else Toast.makeText(ChangePassword.this, "输入的两次密码必须一致！", Toast.LENGTH_SHORT).show();
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
}
