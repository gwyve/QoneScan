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


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class FileActivity extends AppCompatActivity {

    private EditText editText;
    private Button saveBtn;
    private File file;
    private List<String> curNums = new ArrayList<String>();

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
                    writeFile(editText.getText().toString(), file);
                    Toast.makeText(getApplicationContext(), "文件已保存", Toast.LENGTH_SHORT).show();
                    hasChanged = false;
                }else {
                    Toast.makeText(getApplicationContext(), "内容没有改变", Toast.LENGTH_SHORT).show();
                }
            }
        });

        file = new File(Environment.getExternalStorageDirectory()+File.separator+"Qone"+File.separator+fileName);
        if (!file.exists()){

        }else {
            String fileContent = readFile(file);
            String[] strings = fileContent.split("\r\n");
            for (String s:strings){
                if (!curNums.contains(s) && s.indexOf("\r\n")==-1 && s.indexOf(" ") ==-1 && s.length()> 0) {
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
        for (int i=0;i<curNums.size();i++){
            sb.append(curNums.get(i)+"\n");
        }

        editText.setText(sb.toString());
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
                Log.e("111","aaaaaa"+s);
                String[] tmp = s.toString().split("\n");
                newAdd = tmp[tmp.length-1];
            }

            @Override
            public void afterTextChanged(Editable s) {
                editText.removeTextChangedListener(this);
                if (curNums.contains(newAdd)){
                    editText.setText(beforeCS.toString());
                    createAlert(newAdd);
                }else {
                    curNums.add(newAdd);
                    hasChanged = true;
                }
                editText.setSelection(editText.getText().length());
                editText.addTextChangedListener(this);
            }
        });
    }

    private void createAlert(String s){
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
                        writeFile(editText.getText().toString(),file);
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
                        writeFile(editText.getText().toString(),file);
                        Toast.makeText(getApplicationContext(), "文件已保存", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .create().show();
    }


    private String readFile(File file){
        StringBuilder sb = new StringBuilder("");
        try {
            FileInputStream input = new FileInputStream(file);
            byte[] temp = new byte[1024];
            int len = 0;
            //读取文件内容:
            while ((len = input.read(temp)) > 0) {
                sb.append(new String(temp, 0, len));
            }
            //关闭输入流
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private void writeFile(String s,File file){
        String[] ss = s.split("\n");
        try {
            if ( file.exists())
                file.delete();
//            FileOutputStream fos = new FileOutputStream(file);
//            byte [] bytes = s.getBytes();
//            fos.write(bytes);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
            for(String sss:ss){
                bw.write(sss);
                bw.write("\r\n");
                bw.flush();
            }
            bw.close();
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