package com.sourcey.Seeker;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.Iterator;


public class Signup extends AppCompatActivity {
    private static final String TAG = "Signup";

    private EditText accountText;
    private EditText nicknameText;
    private EditText passwordText;
    private EditText reEnterPasswordText;
    private Button signupButton;
    private Button loginLink;
    private Button cancel;
    private ImageButton changephoto;
    private String gender ;
    private RadioButton male,female;
    private RadioGroup group;
    private String account ;
    private String nickname;
    private String password;
    private String reEnterPassword;
    private String UID;
    private String UAC;
    ProgressDialog progressDialog;
    private boolean correct=false;
    private int serverResponseCode=0;

    //照片
    private final static int PHOTO = 99 ;
    private final int  MY_PERMISSIONS_REQUEST_READ_CONTACTS=1;
    private String imagepath="";
    private int picdegree=0;
    private DisplayMetrics mPhone;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        processViews();
        processControllers();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    private void processViews(){
        mPhone = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mPhone);
        changephoto=(ImageButton) findViewById(R.id.signup_change);
        male=(RadioButton) findViewById(R.id.signup_man);
        female=(RadioButton) findViewById(R.id.signup_female);
        group=(RadioGroup) findViewById(R.id.signup_group);
        accountText=(EditText)findViewById(R.id.signup_account);
        nicknameText=(EditText)findViewById(R.id.signup_nickname);
        passwordText=(EditText)findViewById(R.id.signup_password);
        reEnterPasswordText=(EditText)findViewById(R.id.signup_repassword);
        signupButton=(Button)findViewById(R.id.signup_yes);
        cancel=(Button) findViewById(R.id.signup_cancel);
        loginLink=(Button)findViewById(R.id.signup_back);
    }
    private void processControllers(){
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        group.setOnCheckedChangeListener(listener);
        changephoto.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (ContextCompat.checkSelfPermission(Signup.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                        &ContextCompat.checkSelfPermission(Signup.this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(Signup.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            &ActivityCompat.shouldShowRequestPermissionRationale(Signup.this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        new AlertDialog.Builder(Signup.this)
                                .setMessage("必須要有此權限才能開啟相簿喔!")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(Signup.this,
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
                        ActivityCompat.requestPermissions(Signup.this,
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
                case R.id.signup_man:
                    gender="男";
                    break;
                case R.id.signup_female:
                    gender="女";
                    break;
            }
        }
    };
    public void signup() {
       if (!validate()) {
            onSignupFailed();
            return;
        }
        signupButton.setEnabled(false);

        progressDialog = new ProgressDialog(Signup.this,
                R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        account=accountText.getText().toString();
        nickname=nicknameText.getText().toString();
        password = passwordText.getText().toString();
        reEnterPassword = reEnterPasswordText.getText().toString();
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


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if(correct) {
                            onSignupSuccess();
                        }
                        else{
                            accountused();
                            progressDialog.dismiss();
                            return;
                        }
                        progressDialog.dismiss();
                    }
                }, 1500);
    }
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, PHOTO);
                } else {
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        if (requestCode == PHOTO  && data != null)
        {
            imagepath = GetFilePathFromDevice.getPath(Signup.this,data.getData());
            showpic(imagepath);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    public void onSignupSuccess() {
        Toast.makeText(getApplicationContext(), "註冊成功，你可以登入囉!", Toast.LENGTH_LONG).show();
        signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent=new Intent();
        intent.setClass(Signup.this, Login.class);
        startActivity(intent);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "請重新輸入新的註冊資料", Toast.LENGTH_LONG).show();

        signupButton.setEnabled(true);
    }

    public void accountused(){
        Toast.makeText(getApplicationContext(), "此帳號已被註冊過" + "，請重新輸入註冊資料!", Toast.LENGTH_LONG).show();
        signupButton.setEnabled(true);
    }
    public boolean validate() {
        boolean valid = true;

        String account=accountText.getText().toString();
        String nickname=nicknameText.getText().toString();
        String password = passwordText.getText().toString();
        String reEnterPassword = reEnterPasswordText.getText().toString();


        if (account.isEmpty()) {
            accountText.setError("輸入一個新的帳號");
            valid = false;
        } else {
            accountText.setError(null);
        }


        if (nickname.isEmpty() ) {
            nicknameText.setError("輸入你的綽號");
            valid = false;
        } else {
            nicknameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 20) {
            passwordText.setError("6~20位數的密碼");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 6 || reEnterPassword.length() > 20 || !(reEnterPassword.equals(password))) {
            reEnterPasswordText.setError("和先前的密碼不相符");
            valid = false;
        } else {
            reEnterPasswordText.setError(null);
        }

        return valid;
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
            progressDialog.dismiss();
            Log.e("uploadFile","Source File not exist:"+imagepath);
            Signup.this.finish();
            return 0;
        }
        else{
            try {
                FileInputStream fileInputStream=new FileInputStream(sourceFile);
                URL url=new URL("http://134.208.97.233:80/Register2.php");

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
                dos.writeBytes("Content-Disposition: form-data; name=\"account\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(account);
                dos.writeBytes(lineEnd);
                /************************/
                dos.writeBytes(twoHyphens+boundary+lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"gender\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.write(gender.getBytes());
                dos.writeBytes(lineEnd);
                /************************/
                dos.writeBytes(twoHyphens+boundary+lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"nickname\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.write(nickname.getBytes());
                dos.writeBytes(lineEnd);
                /************************/
                dos.writeBytes(twoHyphens+boundary+lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"password\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(password);
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
                    if(!input.equals("fail")) {
                        correct=true;
                    }
                }
                fileInputStream.close();
                dos.flush();
                dos.close();
            }catch (MalformedURLException ex){
                progressDialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Signup.this, "異常發生，請稍後再試", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server","error:"+ex.getMessage(),ex);
            }catch (Exception e){
                progressDialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Signup.this, "異常發生，請稍後再試", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("server Exception", "Exception : "  + e.getMessage(), e);
            }
            progressDialog.dismiss();
            return serverResponseCode;
        }
    }
    public class SendPostRequest extends AsyncTask<String,Void,String> {

        protected void onPreExecute(){}
        @Override
        protected void onPostExecute(String result){
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                URL url=new URL("http://134.208.97.233:80/Register.php");


                JSONObject postDataParams=new JSONObject();
                postDataParams.put("gender",gender);
                postDataParams.put("account",account);
                postDataParams.put("nickname",nickname);
                postDataParams.put("password",password);


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
                    if(!input.equals("fail")) {
                        correct=true;
                        String []ss;
                        ss=input.split("-");
                        UID=ss[0];
                        UID=UID.replace("\"","");
                        UAC=ss[1];
                        UAC=UAC.replace("\"","");
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
            changephoto.setImageBitmap(mScaleBitmap);
        }
        else changephoto.setImageBitmap(bitmap);
    }
}