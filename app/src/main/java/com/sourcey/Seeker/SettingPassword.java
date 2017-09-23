package com.sourcey.Seeker;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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


public class SettingPassword extends AppCompatActivity {
    private String account=Login.UAC;
    private String password;
    private boolean pwcheck=false;
    private Button ChangePassword;
    private EditText newpw,newcpw;
    private Button cancel,back;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_password);
        processViews();
        processControllers();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    private void processViews(){
        imageView=(ImageView) findViewById(R.id.settingpwimage);
        ChangePassword=(Button) findViewById(R.id.Settingpassword);
        newpw=(EditText) findViewById(R.id.settingnewpw);
        newcpw=(EditText) findViewById(R.id.settingnewcpw);
        cancel=(Button) findViewById(R.id.Settingpasswordcancel);
        back=(Button) findViewById(R.id.Settingpasswordback);
    }
    private void processControllers(){
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid=true;
                String cpassword = newpw.getText().toString();
                String reEnterPassword = newcpw.getText().toString();
                if (cpassword.isEmpty() || cpassword.length() < 6 || cpassword.length() > 20) {
                    newpw.setError("6~20位數的密碼");
                    valid = false;
                } else {
                    newpw.setError(null);
                }

                if (reEnterPassword.isEmpty() || reEnterPassword.length() < 6 || reEnterPassword.length() > 20 || !(reEnterPassword.equals(cpassword))) {
                    newcpw.setError("和先前的密碼不相符");
                    valid = false;
                } else {
                    newcpw.setError(null);
                }
                if(valid){
                    //改密碼
                    pwcheck=false;
                    password=newpw.getText().toString();
                    new SendPostRequest_pw().execute();
                }
            }
        });
    }
    public class SendPostRequest_pw extends AsyncTask<String,Void,String> {

        protected void onPreExecute(){}
        @Override
        protected void onPostExecute(String result){
            if(pwcheck) {
                imageView.setImageResource(R.drawable.final_changepasswordsucceed);
                newcpw.setText("");
                newpw.setText("");
            }
            else{
                Toast.makeText(getApplicationContext(), "網路異常，請稍後在試", Toast.LENGTH_LONG).show();
                imageView.setImageResource(R.drawable.final_changepasswordfailed);
            }
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                URL url=new URL("http://134.208.97.233:80/SettingPW.php");


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
                    String input=sb.toString();
                    if(!input.equals("fail")) {
                        pwcheck=true;
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
}
