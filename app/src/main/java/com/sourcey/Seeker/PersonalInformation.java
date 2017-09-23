package com.sourcey.Seeker;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

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
import java.util.Iterator;


public class PersonalInformation extends AppCompatActivity {
    DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();
    TextView account;
    EditText nickname;
    RadioButton male,female;
    RadioGroup group;
    ImageButton profilephoto;
    Button back;
    String accounttext=Login.UAC;
    String nicknametext=Login.NICKNAME;
    String gender=Login.GENDER;
    String photo=Login.Profilepicture;
    String newnickname;
    String newgender;
    Boolean success=false;
    private int serverResponseCode=0;
    private final static int PHOTO = 99 ;
    private final int  MY_PERMISSIONS_REQUEST_READ_CONTACTS=1;
    private String imagepath="";
    private int picdegree=0;
    private DisplayMetrics mPhone;
    private ReleaseBitmap releaseBitmap=new ReleaseBitmap();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
        processViews();
        processControllers();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseBitmap.cleanBitmapList();
    }
    private void processViews(){
        imageLoader.setDefaultLoadingListener(releaseBitmap);
        back=(Button) findViewById(R.id.personal_back);
        mPhone = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mPhone);
        options= new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.final_ninja)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .considerExifParams(false)
                .imageScaleType(ImageScaleType.EXACTLY)
                .resetViewBeforeLoading(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoaderConfiguration config=new ImageLoaderConfiguration.Builder(this).threadPoolSize(3).defaultDisplayImageOptions(options).build();
        imageLoader.init(config);
        account=(TextView) findViewById(R.id.profile_account);
        nickname=(EditText) findViewById(R.id.profile_nickname);
        male=(RadioButton) findViewById(R.id.profile_male);
        female=(RadioButton) findViewById(R.id.profile_female);
        group=(RadioGroup) findViewById(R.id.profile_group);
        profilephoto=(ImageButton) findViewById(R.id.Profilepicture);
    }
    private void processControllers(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });
        Log.e("個人picture",photo);
        if(photo.startsWith("http")) {
            imageLoader.displayImage(photo, profilephoto);
        }
        else{
            showpic(photo);
        }
        account.setText(accounttext);
        nickname.setText(nicknametext);
        group.setOnCheckedChangeListener(listener);
        if(gender.equals("男")){
            male.setChecked(true);
        }
        else{
            gender="女";
            female.setChecked(true);
        }
        profilephoto.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (ContextCompat.checkSelfPermission(PersonalInformation.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                        &ContextCompat.checkSelfPermission(PersonalInformation.this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(PersonalInformation.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            &ActivityCompat.shouldShowRequestPermissionRationale(PersonalInformation.this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        new AlertDialog.Builder(PersonalInformation.this)
                                .setMessage("必須要有此權限才能開啟相簿喔!")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(PersonalInformation.this,
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
                        ActivityCompat.requestPermissions(PersonalInformation.this,
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
    private RadioGroup.OnCheckedChangeListener listener=new RadioGroup.OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId){
                case R.id.profile_male:
                    gender="男";
                    break;
                case R.id.profile_female:
                    gender="女";
                    break;
            }
        }
    };

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
            Log.e("uploadFile","Source File not exist:"+imagepath);
            PersonalInformation.this.finish();
            return 0;
        }
        else{
            try {
                FileInputStream fileInputStream=new FileInputStream(sourceFile);
                URL url=new URL("http://134.208.97.233:80/ChangeProfile2.php");

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
                dos.writeBytes("Content-Disposition: form-data; name=\"gender\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.write(newgender.getBytes());
                dos.writeBytes(lineEnd);
                /************************/
                dos.writeBytes(twoHyphens+boundary+lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"account\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(accounttext);
                dos.writeBytes(lineEnd);
                /************************/
                dos.writeBytes(twoHyphens+boundary+lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"nickname\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.write(newnickname.getBytes());
                dos.writeBytes(lineEnd);
                /************************/
                dos.writeBytes(twoHyphens+boundary+twoHyphens+lineEnd);

                serverResponseCode=conn.getResponseCode();
                String serverResponseMessage=conn.getResponseMessage();

                Log.e("uploadFile","HTTP Response is:"+serverResponseMessage+":"+serverResponseCode);

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
                    String input=sb.toString();
                    if(!input.equals("fail")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                success=true;
                                Toast.makeText(PersonalInformation.this,"成功修改個人資料!",Toast.LENGTH_SHORT).show();
                                Login.GENDER=newgender;
                                Login.NICKNAME=newnickname;
                                Login.Profilepicture=imagepath;
                            }
                        });}
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PersonalInformation.this,"修改失敗，請稍後再試",Toast.LENGTH_SHORT).show();
                            }
                        });}
                }
                fileInputStream.close();
                dos.flush();
                dos.close();
            }catch (MalformedURLException ex){
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PersonalInformation.this,"修改失敗，請稍後再試",Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server","error:"+ex.getMessage(),ex);
            }catch (Exception e){
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PersonalInformation.this,"修改失敗，請稍後再試",Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("server Exception", "Exception : "  + e.getMessage(), e);
            }
            return serverResponseCode;
        }
    }
    public class SendPostRequest extends AsyncTask<String,Void,String> {

        protected void onPreExecute(){}
        @Override
        protected void onPostExecute(String result){
            if(success){
                Toast.makeText(PersonalInformation.this,"成功修改個人資料!",Toast.LENGTH_SHORT).show();
                Login.GENDER=newgender;
                Login.NICKNAME=newnickname;
                Login.Profilepicture=imagepath;
            }
            else{
                Toast.makeText(PersonalInformation.this,"修改失敗，請稍後再試",Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                URL url=new URL("http://134.208.97.233:80/ChangeProfile.php");


                JSONObject postDataParams=new JSONObject();
                postDataParams.put("gender",newgender);
                postDataParams.put("nickname",newnickname);
                postDataParams.put("account",accounttext);

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
                    String input=sb.toString();
                    Log.e("回傳per",input);
                    if(!input.equals("fail")) {
                        success=true;
                    }

                    in.close();
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

    private void dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInformation.this); //創建訊息方塊

        builder.setMessage("確定要更改？");

        builder.setTitle("返回");

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()  {

            @Override

            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }

        });

        builder.setPositiveButton("確認", new DialogInterface.OnClickListener()  {

            @Override

            public void onClick(DialogInterface dialog, int which) {
                newnickname=nickname.getText().toString();
                newgender=gender;
                if(imagepath.isEmpty()) {
                    new SendPostRequest().execute();
                }
                else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            uploadFile(imagepath);
                        }
                    }).start();
                }
                dialog.dismiss();
                PersonalInformation.this.finish();

            }

        });



        builder.create().show();

    }
    public boolean onKeyDown(int keyCode,KeyEvent event){

        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){   //確定按下退出鍵and防止重複按下退出鍵

            dialog();

        }

        return false;

    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        if (requestCode == PHOTO  && data != null)
        {
            imagepath = GetFilePathFromDevice.getPath(PersonalInformation.this,data.getData());
            showpic(imagepath);
        }

        super.onActivityResult(requestCode, resultCode, data);
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
            profilephoto.setImageBitmap(mScaleBitmap);
        }
        else profilephoto.setImageBitmap(bitmap);
    }
}
