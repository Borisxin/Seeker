package com.sourcey.Seeker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;


public class Login extends AppCompatActivity {
    private static final String TAG = "Login";
    private static final int REQUEST_SIGNUP = 0;

    private EditText accountText;
    private EditText passwordText;
    private Button loginButton;
    private TextView signupLink;
    private CheckBox checkBox;
    private String account;
    private String password;
    public static  String token=FirebaseInstanceId.getInstance().getToken();;
    public static String UID;
    public static String UAC;
    public static String NICKNAME;
    public static String GENDER;
    public static String nohttp_Profilepicture;
    public static String Profilepicture;
    private boolean success=false;
    private boolean token_success=false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FreeWifi.LoadWifi(Login.this); //匯入wifi資訊
        success=false;
        processViews();
        processControllers();
    }

    private void processViews(){
        String ac=getConfig("Config","Account","");
        String ps=getConfig("Config","Password","");
        String checked=getConfig("Config","checked","0");

        accountText=(EditText)findViewById(R.id.login_account);
        passwordText=(EditText)findViewById(R.id.login_password);
        accountText.setText(ac);
        passwordText.setText(ps);
        loginButton=(Button)findViewById(R.id.btn_login);
        signupLink=(TextView)findViewById(R.id.link_signup);
        checkBox=(CheckBox) findViewById(R.id.saveaccount);
        if(checked.equals("1")){
            checkBox.setChecked(true);
        }
        else{
            checkBox.setChecked(false);
        }
    }
    private void processControllers(){
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Signup.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }
    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(Login.this,
                R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("正在驗證您的帳號");
        progressDialog.show();

        account = accountText.getText().toString();
        password = passwordText.getText().toString();

        new SendPostRequest().execute();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if(!success) {
                            onLoginFailed();
                            progressDialog.dismiss();
                            return;
                        }
                        else{
                            onLoginSuccess();
                        }
                        progressDialog.dismiss();
                    }
                }, 1500);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        if(checkBox.isChecked()){
            setConfig("Config","Account",account);
            setConfig("Config","Password",password);
            setConfig("Config","checked","1");
        }
        else{
            ClearALL("Config");
        }
        loginButton.setEnabled(true);
        if(token !=null)
        new SendPostRequest_SendToken().execute();
        Intent intent=new Intent();
        intent.setClass(Login.this, MainScreen.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String account = accountText.getText().toString();
        String password = passwordText.getText().toString();

        if (account.isEmpty()) {
            accountText.setError("請輸入正確的帳號");
            valid = false;
        } else {
            accountText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 20) {
            passwordText.setError("請輸入6~20位數的密碼");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }
    public void setConfig(String name, String key,
                                 String value)
    {
        SharedPreferences settings =getSharedPreferences(name,0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putString(key, value);
        PE.commit();
    }

    //設定檔讀取
    public String getConfig( String name , String
            key , String def)
    {
        SharedPreferences settings =getSharedPreferences(name,0);
        return settings.getString(key, def);
    }
    public void ClearALL(String name ){
        SharedPreferences settings =getSharedPreferences(name,0);
        settings.edit().clear().commit();
    }
    public class SendPostRequest extends AsyncTask<String,Void,String> {

        protected void onPreExecute(){}
        @Override
        protected void onPostExecute(String result){
            if(!success) {
                Toast.makeText(getApplicationContext(),"登入失敗，請重新輸入帳號密碼", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "歡迎進入Seeker!", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                URL url=new URL("http://134.208.97.233:80/Login.php");


                JSONObject postDataParams=new JSONObject();

                postDataParams.put("account",account);
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
                    in.close();
                    String input; //php回傳值
                    input=sb.toString();
                    Log.e("登入input",input);
                    if(!input.equals("fail")) {
                        success=true;
                        String []temp;
                        temp=input.split("&&");
                        UID=temp[0].trim();
                        UAC=temp[1].trim();
                        NICKNAME=temp[2].trim();
                        GENDER=temp[3].trim();
                        if(temp[4].equals("no")){
                            nohttp_Profilepicture="";
                            Profilepicture="";
                        }
                        else {
                            nohttp_Profilepicture = temp[4].trim();
                            Profilepicture = "http://134.208.97.233:80/Uploads/Profilepicture/" + temp[4].trim();
                        }
                        Log.e("登入picture",Profilepicture);
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
    public class SendPostRequest_SendToken extends AsyncTask<String,Void,String> {

        protected void onPreExecute(){}
        @Override
        protected void onPostExecute(String result){

        }
        @Override
        protected String doInBackground(String... params) {
            try{
                URL url=new URL("http://134.208.97.233:80/UploadUserToken.php");


                JSONObject postDataParams=new JSONObject();

                postDataParams.put("UID",UID);
                postDataParams.put("token",token);

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
                        token_success=true;
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
}
