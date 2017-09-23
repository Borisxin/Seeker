package com.sourcey.Seeker;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.squareup.leakcanary.RefWatcher;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FriendScreen extends AppCompatActivity {
    DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();
    public static List<Friend> friends=new ArrayList<>();
    private ListView friendlist;
    private EditText searchfriendname;
    private static FriendLIstAdapter adapter;
    private Button backmain,addfriend;
    private String account= Login.UAC;
    private String ID= Login.UID;
    private boolean success=false;
    private String deleteaccount="";
    int adapter_position;
    boolean delete_success=false;
    private ReleaseBitmap releaseBitmap=new ReleaseBitmap();
    public boolean onKeyDown(int keyCode,KeyEvent event){

        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){   //確定按下退出鍵and防止重複按下退出鍵
            FriendScreen.this.finish();
        }
        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_screen);
        friends.clear();
        processViews();
        processControllers();
        new SendPostRequest().execute();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseBitmap.cleanBitmapList();
    }
    private void processViews(){
        imageLoader.setDefaultLoadingListener(releaseBitmap);
        addfriend=(Button) findViewById(R.id.addfriend_btn);
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
        searchfriendname=(EditText) findViewById(R.id.searchfriendname) ;
        friendlist=(ListView) findViewById(R.id.friendlist);
        friendlist.setTextFilterEnabled(true);
        backmain=(Button) findViewById(R.id.backmain);
        searchfriendname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                FriendScreen.this.adapter.getFilter().filter(s);
            }
        });
        friendlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                delete_success=false;
                adapter_position=position;
                ListView listView=(ListView) parent;
                Friend f=(Friend) listView.getItemAtPosition(position);
                deleteaccount=f.getAccount();
                dialog();
                return true;
            }
        });
    }
    private void processControllers(){
        backmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendScreen.this.finish();
            }
        });
        addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FriendScreen.this,AddFriendScreen.class);
                startActivity(intent);
                FriendScreen.this.finish();
            }
        });
    }
    public static void AdapterChange(){
        adapter.notifyDataSetChanged();
    }
    private void dialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(FriendScreen.this); //創建訊息方塊

        builder.setMessage("確定要刪除好友？");

        builder.setTitle("警告");

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()  {

            @Override

            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }

        });

        builder.setPositiveButton("確認", new DialogInterface.OnClickListener()  {

            @Override

            public void onClick(DialogInterface dialog, int which) {
                new SendPostRequest_DELETE().execute();
                dialog.dismiss();


            }

        });



        builder.create().show();

    }
    public class SendPostRequest extends AsyncTask<String,Void,String> {

        protected void onPreExecute(){}
        @Override
        protected void onPostExecute(String result){
            if(!success) {
                Toast.makeText(getApplicationContext(),"查無好友資料...", Toast.LENGTH_SHORT).show();
            }
            else {
                adapter=new FriendLIstAdapter(FriendScreen.this,friends,imageLoader);
                friendlist.setAdapter(adapter);
            }
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                URL url=new URL("http://134.208.97.233:80/Friends.php");


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
                        len=len/5;
                        for(int i=0;i<len;i++){
                            String account=temp[n];
                            n++;
                            String nickname=temp[n];
                            n++;
                            String gender=temp[n];
                            n++;
                            String photo="http://134.208.97.233:80/Uploads/Profilepicture/"+temp[n];
                            n++;
                            String ischeck=temp[n];
                            n++;
                            friends.add(new Friend(account,nickname,gender,ischeck,photo));
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
    public class SendPostRequest_DELETE extends AsyncTask<String,Void,String> {

        protected void onPreExecute(){}
        @Override
        protected void onPostExecute(String result){
            if(!delete_success) {
                Toast.makeText(FriendScreen.this,"刪除好友失敗，請稍後再試...", Toast.LENGTH_SHORT).show();
            }
            else {
                FriendLIstAdapter.friends.remove(adapter_position);
                FriendScreen.AdapterChange();
            }
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                URL url=new URL("http://134.208.97.233:80/FriendDelete.php");


                JSONObject postDataParams=new JSONObject();

                postDataParams.put("account",account);
                postDataParams.put("ID",ID);
                postDataParams.put("friendaccount",deleteaccount);


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
                       delete_success=true;
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
