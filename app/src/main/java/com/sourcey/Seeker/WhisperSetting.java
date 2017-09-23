package com.sourcey.Seeker;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

public class WhisperSetting extends AppCompatActivity {
    private ImageView picture;
    private int picdegree=0;
    private DisplayMetrics mPhone;
    private final int  MY_PERMISSIONS_REQUEST_READ_CONTACTS=1;
    private final static int PHOTO = 99 ;
    private final static int FRIEND = 123;
    private TextView showFriend;
    private Button rechoose;
    private String ID= Login.UID;
    private String account= Login.UAC;
    private String latitude=MainScreen.latitude;
    private String longitude=MainScreen.longitude;
    private String imagepath=WhisperPhoto.whisperimagepath;
    private String Content= WhisperPhoto.whispercontent;
    private String title=WhisperPhoto.whispertitle;
    private int serverResponseCode=0;
    private Button lastPage, check;
    private Button choosefriends;
    private Button calendar;
    private TextView showdate;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private boolean success=false;
    private boolean uploadsuccess=true;
    private String validtime;
    private String date;
    private ProgressDialog dialog=null;
    private String input="";
    public static List<String> friendaccountlist=new ArrayList<>();
    public static List<String> chosenfriendaccountlist=new ArrayList<>();
    public static List<String> friendlist=new ArrayList<>();
    public static List<String> chosenfriendlist=new ArrayList<>();
    public static List<Integer> checkedfriends=new ArrayList<>();
    public static boolean hasfriend=false;
    private Calendar now=Calendar.getInstance();
    private Calendar cdate=Calendar.getInstance();
    public static long dayDiff;



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whisper_setting);
        processViews();
        processControllers();
        showpic(imagepath);
        friendlist.clear();
        friendaccountlist.clear();
        chosenfriendaccountlist.clear();
        chosenfriendlist.clear();
        checkedfriends.clear();
        new SendPostRequest_getFriend().execute();
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
            imagepath = GetFilePathFromDevice.getPath(WhisperSetting.this,data.getData());
            showpic(imagepath);
        }
        if(requestCode == FRIEND )
        {
            String str="";
            for(String choosefriend:chosenfriendlist){
                str+=choosefriend+"\n";
            }
            showFriend.setText(str);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void processViews(){
        cdate.add(Calendar.DAY_OF_MONTH,-1);
        showFriend=(TextView) findViewById(R.id.whispershowfriend);
        picture=(ImageView) findViewById(R.id.whispershowphoto);
        mPhone = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mPhone);
        rechoose=(Button) findViewById(R.id.whisperrechoose);
        lastPage=(Button) findViewById(R.id.whisper_back2);
        check=(Button) findViewById(R.id.whisper_next2);
        choosefriends=(Button) findViewById(R.id.whisperchoosefriends);
        calendar=(Button) findViewById(R.id.whispercalendar);
        showdate=(TextView) findViewById(R.id.whisperdate);
    }
    private void processControllers(){
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                datePickerDialog=new DatePickerDialog(WhisperSetting.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        date=(year+"/"+(monthOfYear+1)+"/"+dayOfMonth);
                        showdate.setText(date);
                        cdate=new GregorianCalendar(year,monthOfYear,dayOfMonth);
                        String temp=year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                        validtime=temp;
                        timePickerDialog.show();
                    }
                },calendar.get(Calendar.YEAR)
                        ,calendar.get(Calendar.MONTH)
                        ,calendar.get(Calendar.DAY_OF_MONTH));
                timePickerDialog=new TimePickerDialog(WhisperSetting.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String m;
                        if (minute<10){
                            m="0"+String.valueOf(minute);
                        }
                        else{
                            m=String.valueOf(minute);
                        }
                        date+="\n"+((hourOfDay>12 ? hourOfDay-12 : hourOfDay)+":"+m+" "+(hourOfDay>12 ? "pm" : "am"));
                        showdate.setText(date);
                        String temp=hourOfDay+":"+m;
                        validtime=validtime+" "+temp;
                    }
                },calendar.get(Calendar.HOUR_OF_DAY)
                        ,calendar.get(Calendar.MINUTE)
                        ,false);
                datePickerDialog.show();
            }

        });
        choosefriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WhisperSetting.this,ChooseFriends.class);
                startActivityForResult(intent,FRIEND);
            }
        });
        lastPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long aDayInMilliSecond = 60 * 60 * 24 * 1000;
                dayDiff = (cdate.getTimeInMillis() - now.getTimeInMillis()) / aDayInMilliSecond;
                if (dayDiff >= 0) {
                    if (chosenfriendlist.size() > 0) {
                        boolean slast = false;
                        int n = chosenfriendlist.size();
                        dialog = ProgressDialog.show(WhisperSetting.this, "", "Uploading file...", true);
                        for (int i = 0; i < n; i++) {
                            final int index = i;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    uploadImage(imagepath, index);
                                }
                            }).start();
                            if (i == n - 1) {
                                slast = true;
                            }
                        }
                        final boolean last = slast;
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                while (!last && uploadsuccess) {
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (uploadsuccess) {
                                    uploadFile(imagepath);
                                } else {
                                    Toast.makeText(WhisperSetting.this, "上傳失敗...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).start();
                    } else {
                        Toast.makeText(WhisperSetting.this, "請選擇要傳送的對象喔", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(WhisperSetting.this,"請重新選擇日期",Toast.LENGTH_SHORT).show();
                }
            }
        });
        rechoose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (ContextCompat.checkSelfPermission(WhisperSetting.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                        &ContextCompat.checkSelfPermission(WhisperSetting.this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(WhisperSetting.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            &ActivityCompat.shouldShowRequestPermissionRationale(WhisperSetting.this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        new AlertDialog.Builder(WhisperSetting.this)
                                .setMessage("必須要有此權限才能開啟相簿喔!")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(WhisperSetting.this,
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
                        ActivityCompat.requestPermissions(WhisperSetting.this,
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
                URL url=new URL("http://134.208.97.233:80/ToFCardUpload.php");

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
                dos.write(title.getBytes());
                dos.writeBytes(lineEnd);
                /************************/
                dos.writeBytes(twoHyphens+boundary+lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"UID\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(ID);
                dos.writeBytes(lineEnd);
                /************************/
                dos.writeBytes(twoHyphens+boundary+lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"UAC\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(account);
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
                dos.writeBytes(twoHyphens+boundary+lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"validtime\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(validtime);
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
                    if(!input.equals("false")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            //    Toast.makeText(WhisperSetting.this,"上傳成功!可以到卡片庫查看",Toast.LENGTH_SHORT).show();
                                WhisperSetting.this.finish();
                                Intent intent=new Intent(WhisperSetting.this,WhisperSuccess.class);
                                startActivity(intent);

                            }
                        });}
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Toast.makeText(WhisperSetting.this,"上傳失敗，請確認網路狀態",Toast.LENGTH_SHORT).show();
                                WhisperSetting.this.finish();
                                Intent intent=new Intent(WhisperSetting.this,WhisperFailed.class);
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
                        Toast.makeText(WhisperSetting.this,"上傳失敗，請確認網路狀態",Toast.LENGTH_SHORT).show();
                        WhisperSetting.this.finish();
                        Intent intent=new Intent(WhisperSetting.this,WhisperFailed.class);
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
                        Toast.makeText(WhisperSetting.this,"上傳失敗，請確認網路狀態",Toast.LENGTH_SHORT).show();
                        WhisperSetting.this.finish();
                        Intent intent=new Intent(WhisperSetting.this,WhisperFailed.class);
                        startActivity(intent);

                    }
                });
                Log.e("server Exception", "Exception : "  + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;
        }
    }
    public int uploadImage(String sourceFileUri,int index){
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
            Log.e("uploadFile","Source File not exist:"+imagepath);
            return 0;
        }
        else{
            try {
                FileInputStream fileInputStream=new FileInputStream(sourceFile);
                URL url=new URL("http://134.208.97.233:80/FromFCardUpload.php");

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
                dos.write(title.getBytes());
                dos.writeBytes(lineEnd);
                /************************/
                dos.writeBytes(twoHyphens+boundary+lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"UAC\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(account);
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
                dos.writeBytes(twoHyphens+boundary+lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"validtime\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(validtime);
                dos.writeBytes(lineEnd);
                /************************/
                dos.writeBytes(twoHyphens+boundary+lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"friendac\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(chosenfriendaccountlist.get(index));
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
                    }
                    else{
                        uploadsuccess=false;
                    }
                }
                fileInputStream.close();
                dos.flush();
                dos.close();
            }catch (MalformedURLException ex){
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(WhisperSetting.this,"上傳失敗，請確認網路狀態",Toast.LENGTH_SHORT).show();
                        WhisperSetting.this.finish();
                        Intent intent=new Intent(WhisperSetting.this,WhisperFailed.class);
                        startActivity(intent);

                    }
                });
                Log.e("Upload file to server","error:"+ex.getMessage(),ex);
            }catch (Exception e){
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(WhisperSetting.this,"上傳失敗，請確認網路狀態",Toast.LENGTH_SHORT).show();
                        WhisperSetting.this.finish();
                        Intent intent=new Intent(WhisperSetting.this,WhisperFailed.class);
                        startActivity(intent);

                    }
                });
                Log.e("server Exception", "Exception : "  + e.getMessage(), e);
            }
            return serverResponseCode;
        }
    }
    public class SendPostRequest_getFriend extends AsyncTask<String,Void,String> {

        protected void onPreExecute(){}
        @Override
        protected void onPostExecute(String result){
            if(success){
                for(int i=0;i<friendlist.size();i++){
                    checkedfriends.add(0);
                }
                if(friendlist.size()>0) hasfriend=true;
            }
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                URL url=new URL("http://134.208.97.233:80/ChooseFriends.php");
                JSONObject postDataParams=new JSONObject();
                postDataParams.put("account",account);
                postDataParams.put("ID",ID);
                Log.e("params",postDataParams.toString());
                HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os=conn.getOutputStream();
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if(responseCode==HttpURLConnection.HTTP_OK){
                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb=new StringBuffer("");
                    String line="";

                    while((line=in.readLine())!=null){

                        sb.append(line);
                        break;
                    }
                    in.close();
                    String input; //php回傳值
                    input=sb.toString();
                    if(!input.equals("fail")) {
                        success=true;
                        String []temp;
                        temp=input.split("&&");
                        int n=0;
                        int len=temp.length;
                        for(int i=0;i<len/2;i++){
                            String isCheck=temp[n];
                            n++;
                            if(isCheck.equals("2")) {
                                friendaccountlist.add(temp[n]);
                                n++;
                                friendlist.add(temp[n]);
                                n++;
                            }
                            else{
                                n++;
                                n++;
                            }

                        }

                    }
                    return sb.toString();
                }
                else{
                    return new String("false:"+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception:"+e.getMessage());
            }

        }
    }
    public String getPostDataString(JSONObject params) throws Exception{
        StringBuilder result=new StringBuilder();
        boolean first=true;

        Iterator<String> itr=params.keys();

        while(itr.hasNext()){
            String key=itr.next();
            Object value=params.get(key);

            if(first)
                first=false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key,"UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(),"UTF-8"));
        }
        return  result.toString();
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
