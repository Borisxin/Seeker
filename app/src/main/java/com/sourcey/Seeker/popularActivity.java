package com.sourcey.Seeker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

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

public class popularActivity extends AppCompatActivity {
    DisplayImageOptions options;
    public static ImageLoader imageLoader = ImageLoader.getInstance();
    private ListView activitylistview;
    private String latitude=MainScreen.latitude;
    private String longitude=MainScreen.longitude;
    private ActivityListAdapter adapter=null;
    private Button history;
    private Button back;
    private boolean success=false;
    private boolean atleastone=false;
    private List<ActivityItem> ActivityList=new ArrayList<>();
    private String http ="http://134.208.97.233:80/Activity_Upload_Files/";
    private ReleaseBitmap releaseBitmap=new ReleaseBitmap();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular);
        processView();
        processControllers();
        new SendPostRequest().execute();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseBitmap.cleanBitmapList();
        activitylistview.setAdapter(null);
    }
    public void onBackPressed() {
        super.onBackPressed();
    }
    private void processView(){
        imageLoader.setDefaultLoadingListener(releaseBitmap);
        back=(Button) findViewById(R.id.activity_back);
        activitylistview=(ListView) findViewById(R.id.activitylist);
        history=(Button) findViewById(R.id.activityhistory);
    }
    private void processControllers(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(popularActivity.this,JoinHistory.class);
                startActivity(intent);
            }
        });
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
    }
    public class SendPostRequest extends AsyncTask<String,Void,String> {

        protected void onPreExecute(){}
        @Override
        protected void onPostExecute(String result){
            if(!atleastone && success) {
                Toast.makeText(popularActivity.this,"附近目前沒有活動呦",Toast.LENGTH_SHORT).show();
            }
            else if(!success){
                Toast.makeText(popularActivity.this,"網路異常，請稍後再試",Toast.LENGTH_SHORT).show();
            }
            else {
                adapter=new ActivityListAdapter(popularActivity.this,ActivityList,latitude,longitude,imageLoader);
                activitylistview.setAdapter(adapter);
            }
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                URL url=new URL("http://134.208.97.233:80/Activity.php");


                JSONObject postDataParams=new JSONObject();

                postDataParams.put("latitude",latitude);
                postDataParams.put("longitude",longitude);


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
                    ActivityList.clear();
                    if(!input.equals("fail") && !input.equals("zero")) {
                        String[] temp;
                        success = true;
                        atleastone=true;
                        temp = input.split("&&");
                        int n=0;
                        int len=temp.length;
                        len=len/12;
                        for (int i = 0; i <len; i++) {

                            Double Latitude=Double.parseDouble(temp[n]);
                            n++;
                            Double Longitude=Double.parseDouble(temp[n]);
                            n++;
                            String Picture=http+temp[n];
                            n++;
                            String Activity_Name=temp[n];
                            n++;
                            String Nature=temp[n];
                            n++;
                            String Content=temp[n];
                            n++;
                            String StartDate=temp[n];
                            n++;
                            String FinishDate=temp[n];
                            n++;
                            String Address=temp[n];
                            n++;
                            String Phone=temp[n];
                            n++;
                            String Store_Name=temp[n];
                            n++;
                            String distance=temp[n];
                            n++;
                            ActivityItem activityItem=new ActivityItem(Picture,Latitude,Longitude,Activity_Name,Nature,Content,StartDate,FinishDate,Address,Phone,Store_Name,distance);
                            if(distance.equals("200m")) {
                                ActivityList.add(0,activityItem);
                            }
                            else{
                                ActivityList.add(activityItem);
                            }
                        }
                    }
                    else if(input.equals("zero")){
                        success=true;
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
