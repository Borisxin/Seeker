package com.sourcey.Seeker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.telecom.Call;
import android.util.Log;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class CallCamera extends AppCompatActivity {
    private final int  MY_PERMISSIONS_REQUEST_READ_CONTACTS_C=0;
    private Uri uri;
    private String UID= Login.UID;
    private String UAC= Login.UAC;
    private String fileName;
    private String realName;
    private Context context=this;
    private String imagepath;
    private String activityname;
    private String category;
    private ProgressDialog dialog=null;
    private String upLoadServerUri=null;
    private int serverResponseCode=0;
    private String input="fail";
    private final static int CAMERA = 66 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_camera);
        Intent intent = this.getIntent();
        activityname = intent.getStringExtra("activityname");
        category=intent.getStringExtra("category");
        Log.e("name",activityname);
        upLoadServerUri="http://134.208.97.233:80/activitycard.php";
        open();
    }
    private void open(){
        if (ContextCompat.checkSelfPermission(CallCamera.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(CallCamera.this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(CallCamera.this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    ||ActivityCompat.shouldShowRequestPermissionRationale(CallCamera.this,
                    android.Manifest.permission.CAMERA)) {
                new AlertDialog.Builder(CallCamera.this)
                        .setMessage("必須要有此權限才能開啟相機喔!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(CallCamera.this,
                                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA},
                                        MY_PERMISSIONS_REQUEST_READ_CONTACTS_C);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();

            } else {
                ActivityCompat.requestPermissions(CallCamera.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS_C);
            }
        }
        else {
            Intent intentCamera =
                    new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // 照片檔案名稱
            File pictureFile = configFileName("P", ".jpg");
            uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", pictureFile);
            // 設定檔案名稱
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            // 啟動相機元件
            startActivityForResult(intentCamera, CAMERA);
        }
    }
    private File configFileName(String prefix, String extension) {
        fileName = FileUtil.getUniqueFileName();
        realName=prefix + fileName + extension;
        return new File(FileUtil.getExternalStorageDir(FileUtil.APP_DIR),
                prefix + fileName + extension);
    }
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS_C: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intentCamera =
                            new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // 照片檔案名稱
                    File pictureFile = configFileName("P", ".jpg");
                    uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", pictureFile);
                    // 設定檔案名稱
                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    // 啟動相機元件
                    startActivityForResult(intentCamera, CAMERA);

                } else {
                }
                return;
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        if(requestCode==CAMERA) {
            File photo = FileUtil.getAlbumStorageDir();
            imagepath = photo.getAbsolutePath() + '/' + realName;
            dialog = ProgressDialog.show(CallCamera.this, "", "Uploading ...", true);
            if (imagepath.isEmpty()) {
                CallCamera.this.finish();
            }
            else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        uploadFile(imagepath);
                    }
                }).start();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public int uploadFile(String sourceFileUri){
        String fileName=sourceFileUri;

        HttpURLConnection conn=null;
        DataOutputStream dos=null;
        String lineEnd="\r\n";
        String twoHyphens="--";
        String boundary="*****";
        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize=1*1024*1024;
        File sourceFile=new File(sourceFileUri);

        if(!sourceFile.isFile()){
            dialog.dismiss();
            Log.e("uploadFile","Source File not exist:"+imagepath);
            CallCamera.this.finish();
            return 0;
        }
        else{
            try {
                FileInputStream fileInputStream=new FileInputStream(sourceFile);
                URL url=new URL(upLoadServerUri);

                conn=(HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos=new DataOutputStream(conn.getOutputStream());
                /************************/
                dos.writeBytes(twoHyphens+boundary+lineEnd);
                dos.writeBytes("Content-Disposition: form-data;name=\"uploaded_file\";filename=\""
                        +fileName+"\""+lineEnd);

                dos.writeBytes(lineEnd);

                //開始傳照片
                bytesAvailable=fileInputStream.available();
                bufferSize=Math.min(bytesAvailable,maxBufferSize);
                buffer=new byte[bufferSize];

                bytesRead=fileInputStream.read(buffer,0,bufferSize);
                while(bytesRead>0){
                    dos.write(buffer,0,bufferSize);
                    bytesAvailable=fileInputStream.available();
                    bufferSize=Math.min(bytesAvailable,maxBufferSize);
                    bytesRead=fileInputStream.read(buffer,0,bufferSize);
                }
                dos.writeBytes(lineEnd);
                /************************/
                dos.writeBytes(twoHyphens+boundary+lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"activityname\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.write(activityname.getBytes());
                dos.writeBytes(lineEnd);
                /************************/
                dos.writeBytes(twoHyphens+boundary+lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"category\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.write(category.getBytes());
                dos.writeBytes(lineEnd);
                /************************/
                dos.writeBytes(twoHyphens+boundary+lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"UID\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(UID);
                dos.writeBytes(lineEnd);
                /************************/
                dos.writeBytes(twoHyphens+boundary+lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"UAC\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(UAC);
                dos.writeBytes(lineEnd);
                /************************/
                dos.writeBytes(twoHyphens+boundary+twoHyphens+lineEnd);

                serverResponseCode=conn.getResponseCode();
                String serverResponseMessage=conn.getResponseMessage();

                Log.i("uploadFile","HTTP Response is:"+serverResponseMessage+":"+serverResponseCode);

                if(serverResponseCode==200){
                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb=new StringBuffer("");
                    String line="";

                    while((line=in.readLine())!=null){

                        sb.append(line);
                        break;
                    }
                    in.close();
                    //php回傳值
                    input=sb.toString();
                    if(!input.equals("fail")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Toast.makeText(CallCamera.this,"上傳成功! "+input,Toast.LENGTH_SHORT).show();
                                CallCamera.this.finish();
                            }
                        });}
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Toast.makeText(CallCamera.this,"上傳失敗!",Toast.LENGTH_SHORT).show();
                                CallCamera.this.finish();
                            }
                        });
                    }
                }
                fileInputStream.close();
                dos.flush();
                dos.close();
            }catch (MalformedURLException ex){
                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(CallCamera.this,"上傳失敗!",Toast.LENGTH_SHORT).show();
                        CallCamera.this.finish();
                    }
                });
                Log.e("Upload file to server","error:"+ex.getMessage(),ex);
            }catch (Exception e){
                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(CallCamera.this,"上傳失敗!",Toast.LENGTH_SHORT).show();
                        CallCamera.this.finish();
                    }
                });
                Log.e("server Exception", "Exception : "  + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;
        }
    }
}
