package com.example.ztb.qonescan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class FileActivity extends AppCompatActivity {

    private EditText editText;
    private Button saveBtn;
    private File file;
    private List<String> curNums = new ArrayList<String>();
    private String sep = "\r\n";

    private boolean hasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        editText = (EditText)findViewById(R.id.fileActEditText);
        saveBtn = (Button)findViewById(R.id.fileActSaveBtn);

        Intent intent = getIntent();
        String fileName = intent.getStringExtra("fileName");
//        String fileName = intent.getExtras().getString("fileName");

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasChanged) {
                    writeFile( file);
                    Toast.makeText(getApplicationContext(), "文件已保存", Toast.LENGTH_SHORT).show();
                    hasChanged = false;
                }else {
                    Toast.makeText(getApplicationContext(), "内容没有改变", Toast.LENGTH_SHORT).show();
                }
            }
        });

        file = new File(Environment.getExternalStorageDirectory()+File.separator+"Qone"+File.separator+fileName);
        if (!file.exists()){
            file.mkdirs();
        }else {
            String fileContent = readFile(file);
            String[] strings = fileContent.split(sep);
            Log.e("111",fileContent);
            for (String s:strings){
                if (!curNums.contains(s) && s.indexOf(sep)==-1 && s.indexOf(" ") ==-1 && s.length()> 0) {
                    curNums.add(s);
                }
            }
        }
        initEditView();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK && hasChanged){
            createQuitAlert();
            return false;
        }
        return super.onKeyUp(keyCode, event);

    }




    private void initEditView(){
        StringBuilder sb = new StringBuilder("");
        for(int i=0;i<curNums.size();i++){
            Log.e("111","kkk  "+curNums.get(i));
            sb.append(curNums.get(i) + "\n");
        }
        if (sb.length()>1) {
            editText.setText(sb.toString().substring(0,sb.toString().length()));
        }
        editText.setInputType(InputType.TYPE_NULL);
        editText.setSingleLine(false);
        editText.setHorizontallyScrolling(false);

        editText.setSelection(editText.getText().length());

        editText.addTextChangedListener(new TextWatcher() {
            String beforeCS;
            String newAdd;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeCS = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (count >1){
                    String[] tmp = s.toString().split("\n");
                    newAdd = tmp[tmp.length-1];
                    newAdd = newAdd.replaceAll("\uFEFF","");
                }else {
                    newAdd = null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                editText.removeTextChangedListener(this);
                if (newAdd == null){

                }else {
                    if (curNums.contains(newAdd)){
                        editText.setText(beforeCS.toString());
                        createAlert(newAdd);
                    }else {
                        curNums.add(newAdd);
//                        strList.addLast(newAdd);
                        hasChanged = true;
                    }
                }
                editText.setSelection(editText.getText().length());
                editText.addTextChangedListener(this);
            }
        });
    }

    private void createAlert(String s){
        String editStr = editText.getText().toString();
        editStr = editStr.substring(0,editStr.length()-1);
        editText.setText(editStr);
        new AlertDialog.Builder(FileActivity.this)
                .setTitle("注意")
                .setMessage(s+"已存在，无法输入")
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create().show();
    }


    private void createSaveAlert(){
        new AlertDialog.Builder(FileActivity.this)
                .setTitle("保存")
                .setMessage("要保存吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        writeFile(file);
                        Toast.makeText(getApplicationContext(), "文件已保存", Toast.LENGTH_LONG).show();
                    }
                })
                .create().show();
    }

    private void createQuitAlert(){
        new AlertDialog.Builder(FileActivity.this)
                .setTitle("退出")
                .setMessage("要保存吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        writeFile(file);
                        Toast.makeText(getApplicationContext(), "文件已保存", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .create().show();
    }


    private String readFile(File file){
        String fileContent = "";
        try {
            if(file.isFile()&&file.exists()){
                InputStreamReader read = new InputStreamReader(new FileInputStream(file),"gbk");
                BufferedReader reader=new BufferedReader(read);
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContent += line;
                    fileContent += sep;
                    Log.e("111",line);
                }
                read.close();
            }
        } catch (Exception e) {
            System.out.println("读取文件内容操作出错");
            e.printStackTrace();
        }
        Log.e("111",fileContent);
        return fileContent;
    }

    private void writeFile(File file){

        try {
            if ( file.exists())
                file.delete();
            file.createNewFile();

            FileOutputStream outputStream = new FileOutputStream(file,true);
            for (int i = 0; i<curNums.size();i++){
                outputStream.write(curNums.get(i).trim().getBytes("gbk"));
                outputStream.write(sep.getBytes("gbk"));
                outputStream.flush();
            }
            outputStream.close();


//            BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
//            for(int i=0;i<curNums.size();i++){
//                bw.write(curNums.get(i));
//                bw.write(sep);
//                bw.flush();
//            }
//            bw.close();
//            MediaScannerConnection.scanFile(FileActivity.this,new String[]{file.getAbsolutePath()},null,null);
            notifySystemToScan(file);
//            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifySystemToScan(File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        FileActivity.this.getApplication().sendBroadcast(intent);
    }


}
