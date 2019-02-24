package com.example.lenovo.zhihudailyreport;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Information extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information);
        final String name;
        String newName;
        String sex="";
        String job="";
        int age=0;
        String signature="";
        Intent intent = getIntent();
        name = intent.getStringExtra("userName");
        final EditText et_name = (EditText) findViewById(R.id.et_name);
        final EditText et_signature = (EditText) findViewById(R.id.et_signature);
        final EditText et_age = (EditText) findViewById(R.id.et_age);
        final EditText et_job = (EditText) findViewById(R.id.et_job);
        final EditText et_sex = (EditText) findViewById(R.id.et_sex);
        et_age.setFilters(new InputFilter[] { new InputFilter.LengthFilter(3) });
        et_signature.setFilters(new InputFilter[] { new InputFilter.LengthFilter(30) });
        et_signature.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        et_name.setFilters(new InputFilter[] { new InputFilter.LengthFilter(15) });
        et_job.setFilters(new InputFilter[] { new InputFilter.LengthFilter(15) });

        setEditTextInhibitInputSpace(et_name);

        Button reset = (Button) findViewById(R.id.reset);
        Button out = (Button) findViewById(R.id.out);
        dbHelper = new MyDatabaseHelper(this, "User.db", null, 3);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("User", new String[]{"name","sex","job","age","signature","picId"}, "name=?", new String[]{name}, null, null,null);
        if(cursor.moveToFirst()) {
            do {
                sex = cursor.getString(cursor.getColumnIndex("sex"));
                job = cursor.getString(cursor.getColumnIndex("job"));
                age = cursor.getInt(cursor.getColumnIndex("age"));
                signature = cursor.getString(cursor.getColumnIndex("signature"));
            } while(cursor.moveToNext());
        }
        cursor.close();
        et_name.setText(name);
        et_name.setSelection(name.length());
        et_age.setText(String.valueOf(age));
        et_age.setSelection(String.valueOf(age).length());
        et_job.setText(job);
        et_job.setSelection(job.length());
        et_sex.setText(sex);
        et_sex.setSelection(sex.length());
        et_signature.setText(signature);
        et_signature.setSelection(signature.length());
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Information.this, FragmentActivity.class);
                intent.putExtra("userName",name);
                startActivity(intent);
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldName;
                int i=1;
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                if (et_name.getText().toString().equals("")){
                    Toast.makeText(Information.this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
                }
                else {
                    Cursor cursor = db.query("User", new String[]{"name"}, null, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        do {
                            oldName = cursor.getString(cursor.getColumnIndex("name"));
                            if (et_name.getText().toString().equals(oldName)) {
                                i = 0;
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    if (et_name.getText().toString().equals(name)){
                        if (et_sex.getText().toString().equals("男")||et_sex.getText().toString().equals("女")||et_sex.getText().toString().equals("")){
                            values.put("name",et_name.getText().toString());
                            values.put("age",et_age.getText().toString());
                            values.put("job",et_job.getText().toString());
                            values.put("sex",et_sex.getText().toString());
                            values.put("signature",et_signature.getText().toString());
                            db.update("User",values, "name = ?", new String[]{name});
                            values.clear();
                            Toast.makeText(Information.this, "修改成功！", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Information.this, FragmentActivity.class);
                            intent.putExtra("userName",et_name.getText().toString());
                            startActivity(intent);
                        }
                        else
                            Toast.makeText(Information.this, "性别错误！", Toast.LENGTH_SHORT).show();
                    }
                    else if (i == 0){
                        Toast.makeText(Information.this, "该用户名已存在！", Toast.LENGTH_SHORT).show();
                    }
                    else if (et_sex.getText().toString().equals("男")||et_sex.getText().toString().equals("女")||et_sex.getText().toString().equals("")){
                        values.put("name",et_name.getText().toString());
                        values.put("age",et_age.getText().toString());
                        values.put("job",et_job.getText().toString());
                        values.put("sex",et_sex.getText().toString());
                        values.put("signature",et_signature.getText().toString());
                        db.update("User",values, "name = ?", new String[]{name});
                        values.clear();
                        Toast.makeText(Information.this, "修改成功！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Information.this, FragmentActivity.class);
                        intent.putExtra("userName",et_name.getText().toString());
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(Information.this, "性别错误！", Toast.LENGTH_SHORT).show();
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
