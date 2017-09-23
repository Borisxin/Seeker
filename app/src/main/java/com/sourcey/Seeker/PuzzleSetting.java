package com.sourcey.Seeker;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PuzzleSetting extends AppCompatActivity {
    private ImageView picture;
    private int picdegree=0;
    private DisplayMetrics mPhone;
    private final int  MY_PERMISSIONS_REQUEST_READ_CONTACTS=1;
    private final static int PHOTO = 99 ;
    private Button rechoose;
    private Button check,lastPage;
    private String UID= Login.UID;
    private String UAC= Login.UAC;
    private String latitude=MainScreen.latitude;
    private String longitude=MainScreen.longitude;
    private String imagepath=PuzzlePhoto.puzzleimagepath;
    private String Content=PuzzlePhoto.puzzlecontent;
    private String Title=PuzzlePhoto.puzzletitle;
    private ProgressDialog dialog=null;
    private String upLoadServerUri=null;
    private int serverResponseCode=0;
    private String input="fail";
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_setting);
        processViews();
        processControllers();
        showpic(imagepath);
        upLoadServerUri="http://134.208.97.233:80/MyCardUpload.php";
        registerReceiver(close_myself, new IntentFilter("CloseActivities"));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(close_myself);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        if (requestCode == PHOTO  && data != null)
        {
            imagepath = GetFilePathFromDevice.getPath(PuzzleSetting.this,data.getData());
            showpic(imagepath);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void processViews(){
        check=(Button) findViewById(R.id.puzzle_next2);
        lastPage=(Button) findViewById(R.id.puzzle_back2);
        picture=(ImageView) findViewById(R.id.puzzleshowphoto);
        mPhone = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mPhone);
        rechoose=(Button) findViewById(R.id.puzzlerechoose);
    }
    private void processControllers(){
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(PuzzleSetting.this, "", "Uploading file...", true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        uploadFile(imagepath);
                    }
                }).start();
            }
        });
        lastPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        rechoose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (ContextCompat.checkSelfPermission(PuzzleSetting.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                        &ContextCompat.checkSelfPermission(PuzzleSetting.this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(PuzzleSetting.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            &ActivityCompat.shouldShowRequestPermissionRationale(PuzzleSetting.this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        new AlertDialog.Builder(PuzzleSetting.this)
                                .setMessage("必須要有此權限才能開啟相簿喔!")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(PuzzleSetting.this,
                                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
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
                        ActivityCompat.requestPermissions(PuzzleSetting.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    }
                }
                else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, PHOTO);
                }
            }
        });
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
                dos.writeBytes("Content-Disposition: form-data; name=\"text\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.write(Content.getBytes());
                dos.writeBytes(lineEnd);
                /************************/
                dos.writeBytes(twoHyphens+boundary+lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"title\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.write(Title.getBytes());
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
                dos.writeBytes(twoHyphens+boundary+lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"latitude\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(latitude);
                dos.writeBytes(lineEnd);
                /************************/
                dos.writeBytes(twoHyphens+boundary+lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"longitude\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(longitude);
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
                              //  Toast.makeText(PuzzleSetting.this,"上傳成功!可以到卡片庫查看",Toast.LENGTH_SHORT).show();
                                PuzzleSetting.this.finish();
                                Intent intent=new Intent(PuzzleSetting.this,PuzzleSuccess.class);
                                startActivity(intent);

                            }
                        });}
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Toast.makeText(PuzzleSetting.this,"上傳失敗，請確認網路狀態",Toast.LENGTH_SHORT).show();
                                PuzzleSetting.this.finish();
                                Intent intent=new Intent(PuzzleSetting.this,PuzzleFailed.class);
                                startActivity(intent);

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
                        Toast.makeText(PuzzleSetting.this,"上傳失敗，請確認網路狀態",Toast.LENGTH_SHORT).show();
                        PuzzleSetting.this.finish();
                        Intent intent=new Intent(PuzzleSetting.this,PuzzleFailed.class);
                        startActivity(intent);

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
                        Toast.makeText(PuzzleSetting.this,"上傳失敗，請確認網路狀態",Toast.LENGTH_SHORT).show();
                        PuzzleSetting.this.finish();
                        Intent intent=new Intent(PuzzleSetting.this,PuzzleFailed.class);
                        startActivity(intent);

                    }
                });
                Log.e("server Exception", "Exception : "  + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;
        }
    }


    private void showpic(String path){
        Uri uri=Uri.fromFile(new File(path));
        ContentResolver cr = this.getContentResolver();
        try {
            picdegree=getBitmapDegree(path);
            Bitmap bitmap = null;

            bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));

            if(picdegree!=0){
                bitmap=rotateBitmapByDegree(bitmap,picdegree);
            }
            //判斷照片為橫向或者為直向，並進入ScalePic判斷圖片是否要進行縮放
            if(bitmap.getWidth()>bitmap.getHeight())ScalePic(bitmap,
                    mPhone.heightPixels);
            else ScalePic(bitmap,mPhone.widthPixels);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 從指定路徑下讀取圖片，並獲取其EXIF資訊
            ExifInterface exifInterface = new ExifInterface(path);
            // 獲取圖片的旋轉資訊
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根據旋轉角度，生成旋轉矩陣
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 將原始圖片按照旋轉矩陣進行旋轉，並得到新的圖片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    private void ScalePic(Bitmap bitmap, int phone)
    {
        //縮放比例預設為1
        float mScale = 1 ;

        //如果圖片寬度大於手機寬度則進行縮放，否則直接將圖片放入ImageView內
        if(bitmap.getWidth() > phone )
        {
            //判斷縮放比例
            mScale = (float)phone/(float)bitmap.getWidth();

            Matrix mMat = new Matrix() ;
            mMat.setScale(mScale, mScale);

            Bitmap mScaleBitmap = Bitmap.createBitmap(bitmap,
                    0,
                    0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    mMat,
                    false);
            picture.setImageBitmap(mScaleBitmap);
        }
        else picture.setImageBitmap(bitmap);
    }
    private final BroadcastReceiver close_myself = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
}
