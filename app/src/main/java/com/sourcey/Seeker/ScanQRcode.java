package com.sourcey.Seeker;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

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

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanQRcode extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView mScannerView;
    private boolean isSuccess=false;
    private String ID= Login.UID;
    private String Account= Login.UAC;
    private String newfriendaccount="";
    private String NickName= Login.NICKNAME;
    private String Gender= Login.GENDER;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {

        dialog(rawResult.getText().toString());
        onBackPressed();

        // If you would like to resume scanning, call this method below:
        //mScannerView.resumeCameraPreview(this);
    }
    private void dialog(final String result){

        AlertDialog.Builder builder = new AlertDialog.Builder(getApplication()); //創建訊息方塊

        builder.setMessage("要添加"+result+"為好友嗎?");

        builder.setTitle("Friends");

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()  {

            @Override

            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }

        });

        builder.setPositiveButton("確認", new DialogInterface.OnClickListener()  {

            @Override

            public void onClick(DialogInterface dialog, int which) {
                newfriendaccount=result;
                if(!AddFriendByAccount.checkFriend(newfriendaccount,AddFriendByAccount.friendaccountlist))
                    new SendPostRequest().execute();
                else
                    Toast.makeText(getApplication(),newfriendaccount+"已經是你的好友囉",Toast.LENGTH_SHORT).show();
                dialog.dismiss();


            }

        });
        builder.create().show();
    }
    public class SendPostRequest extends AsyncTask<String,Void,String> {

        protected void onPreExecute(){}
        @Override
        protected void onPostExecute(String result){
            if(!isSuccess) {
                Toast.makeText(getApplication() ,"申請好友失敗...", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplication(), "申請成功，等待好友確認中...", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                URL url=new URL("http://134.208.97.233:80/AddFriend.php");


                JSONObject postDataParams=new JSONObject();
                postDataParams.put("ID",ID);
                postDataParams.put("account",Account);
                postDataParams.put("friendaccount",newfriendaccount);
                postDataParams.put("nickname",NickName);
                postDataParams.put("gender",Gender);


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
                        isSuccess=true;
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