package com.sourcey.Seeker;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class JoinHistory extends AppCompatActivity {
    DisplayImageOptions options;
    public static ImageLoader imageLoader = ImageLoader.getInstance();
    private ListView listView;
    private boolean success=false;
    private boolean atleastone=false;
    private String account= Login.UAC;
    private String ID= Login.UID;
    private List<JoinHistoryItem> joinHistoryItems=new ArrayList<>();
    private String http ="http://134.208.97.233:80/Uploads/ActivityCard/";
    private ReleaseBitmap releaseBitmap=new ReleaseBitmap();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_history);
        success=false;
        atleastone=false;
        processViews();
        processControllers();
        new SendPostRequest().execute();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseBitmap.cleanBitmapList();
        listView.setAdapter(null);
    }
    private void processViews(){
        imageLoader.setDefaultLoadingListener(releaseBitmap);
        listView=(ListView) findViewById(R.id.joinlistview);
    }
    private void processControllers(){
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
                Toast.makeText(JoinHistory.this,"目前還沒有參加活動紀錄喔",Toast.LENGTH_SHORT).show();
            }
            else if(!success){
                Toast.makeText(JoinHistory.this,"網路異常，請稍後再試",Toast.LENGTH_SHORT).show();
            }
            else {
                JoinListAdapter adapter=new JoinListAdapter(JoinHistory.this,joinHistoryItems,imageLoader);
                listView.setAdapter(adapter);
            }
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                URL url=new URL("http://134.208.97.233:80/JoinHistory.php");
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
                    joinHistoryItems.clear();
                    if(!input.equals("fail") && !input.equals("zero")) {
                        String[] temp;
                        success = true;
                        atleastone=true;
                        temp = input.split("&&");
                        int n=0;
                        int len=temp.length;
                        len=len/6;
                        for (int i = 0; i <len; i++) {
                            String activityname;
                            String image;
                            String time;
                            String category;
                            String ID;
                            activityname=temp[n];
                            n++;
                            image=http+temp[n];
                            n++;
                            time=temp[n];
                            n++;
                            category=temp[n];
                            n++;
                            ID=temp[n];
                            n++;
                            if(temp[n].equals("0")){
                                JoinHistoryItem item=new JoinHistoryItem(image,activityname,time,ID,true,category);
                                joinHistoryItems.add(0,item);
                            }
                            else{
                                JoinHistoryItem item=new JoinHistoryItem(image,activityname,time,ID,false,category);
                                joinHistoryItems.add(item);
                            }
                            n++;
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
