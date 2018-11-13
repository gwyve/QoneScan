package com.example.ztb.qonescan;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FolderActivity extends AppCompatActivity {


    private Button newFileBtn;
    private ListView listView;
    private File[] files;
    private EditText editText;
    File path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        newFileBtn = (Button) findViewById(R.id.folderActNewFileBtn);
        listView = (ListView) findViewById(R.id.folderActListView);

        File sdDir = Environment.getExternalStorageDirectory();
        path =  new File(sdDir+File.separator+"Qone");
        if(!path.exists()){
            path.mkdirs();
        }

        files = path.listFiles();

        newFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewFileAlert();
            }
        });

        listView.setAdapter(new MyAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("fileName",files[position].getName());
                intent.setClass(FolderActivity.this, FileActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        files = path.listFiles();

        listView.setAdapter(new MyAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("fileName",files[position].getName());
                intent.setClass(FolderActivity.this, FileActivity.class);
                startActivity(intent);
            }
        });



    }



    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return files.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(FolderActivity.this);
            tv.setTextSize(30);
            tv.setText(files[position].getName());
            return tv;
        }

    }

    private void createNewFileAlert(){

        final EditText et = new EditText(FolderActivity.this);
        editText = et;
//        et.setFocusable(true);
//        et.setFocusableInTouchMode(true);
//        et.requestFocus();
        FolderActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        new AlertDialog.Builder(this).setTitle("输入文件名")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(getApplicationContext(), "搜索内容不能为空！" + input, Toast.LENGTH_LONG).show();
                        }
                        else {
                            Intent intent = new Intent();
                            intent.putExtra("fileName", input+".txt");
                            intent.setClass(FolderActivity.this,FileActivity.class);
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    }
                })
                .show();

    }





}
