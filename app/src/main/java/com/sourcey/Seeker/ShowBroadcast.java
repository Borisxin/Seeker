package com.sourcey.Seeker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
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

public class ShowBroadcast extends AppCompatActivity  {
    Button play,all,food,young,secret;
    LinearLayout layout1,layout2,layout3,layout4;
    Button history,nevigate,add;
    Button last,next,back;
    DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();
    ViewPager viewPager;
    BroadcastAdapter adapter;
    List<BroadcastCard> BroadcastItems=new ArrayList<>();
    List<BroadcastCard> selecteditems=new ArrayList<>();
    List<HistoryItem> historyItemList=new ArrayList<>();
    double latitude;
    double longitude;
    public static double cardlat;
    public static double cardlng;
    public String lat;
    public String lng;
    private boolean success=false;
    private boolean atleastone=false;
    private int mCurrentViewID=0;
    private int PAGER_NUM=0;
    private boolean success_history=false;
    private boolean atleastone_history=false;
    private ReleaseBitmap releaseBitmap=new ReleaseBitmap();
    private GPSTracker gpsTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_broadcast);
        processViews();
        processControllers();
        success=false;
        atleastone=false;
        success_history=false;
        atleastone_history=false;
        gpsTracker=new GPSTracker(ShowBroadcast.this);
        Location location=gpsTracker.getLocation();
        latitude=location.getLatitude();
        longitude=location.getLongitude();
        lat=String.valueOf(location.getLatitude());
        lng=String.valueOf(location.getLongitude());
        new SearchBroadcast().execute();
        new SearchHistory().execute();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseBitmap.cleanBitmapList();
        gpsTracker.stopUsingGPS();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    private void processViews(){
        options= new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.final_ninja)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .considerExifParams(false)
                .imageScaleType(ImageScaleType.EXACTLY)
                .resetViewBeforeLoading(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).threadPoolSize(3).defaultDisplayImageOptions(options).build();
        imageLoader.init(config);
        imageLoader.setDefaultLoadingListener(releaseBitmap);
        viewPager=(ViewPager) findViewById(R.id.broadcastviewpager);
        play=(Button) findViewById(R.id.broadcast_play_btn);
        all=(Button) findViewById(R.id.broadcast_all_btn);
        food=(Button) findViewById(R.id.broadcast_food_btn);
        young=(Button) findViewById(R.id.broadcast_young_btn);
        secret=(Button) findViewById(R.id.broadcast_secret_btn);
        layout1=(LinearLayout) findViewById(R.id.broadcastlayout1);
        layout2=(LinearLayout) findViewById(R.id.broadcastlayout2);
        layout3=(LinearLayout) findViewById(R.id.broadcastlayout3);
        layout4=(LinearLayout) findViewById(R.id.broadcastlayout4);
        history=(Button) findViewById(R.id.broadcast_intro_btn);
        nevigate=(Button) findViewById(R.id.broadcast_nevigate_btn);
        add=(Button) findViewById(R.id.broadcast_add_btn);
        last=(Button) findViewById(R.id.broadcast_last_btn);
        next=(Button) findViewById(R.id.broadcast_next_btn);
        back=(Button) findViewById(R.id.broadcast_back_btn);
    }
    private void processControllers(){
        viewPager.addOnPageChangeListener(mOnPageChangeListener);
        viewPager.setOffscreenPageLimit(2);
        last.setOnClickListener(mOnClickListener);
        next.setOnClickListener(mOnClickListener);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ShowBroadcast.this,BroadcastPhoto.class);
                startActivity(intent);
            }
        });
        nevigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guide();
            }
        });
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1.setBackgroundResource(R.mipmap.final_allback);
                layout2.setBackgroundResource(R.mipmap.final_allback);
                layout3.setBackgroundResource(R.mipmap.final_allback);
                layout4.setBackgroundResource(R.mipmap.final_allback);
                adapter=new BroadcastAdapter(ShowBroadcast.this,BroadcastItems,lat,lng,imageLoader);
                PAGER_NUM=BroadcastItems.size();
                viewPager.setAdapter(adapter);
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecteditems.clear();
                for(BroadcastCard broadcastCard:BroadcastItems){
                    if(broadcastCard.getCategory().equals("好玩不騙")){
                        selecteditems.add(broadcastCard);
                    }
                }
                layout1.setBackgroundResource(R.mipmap.final_broadcast_fun_background);
                layout2.setBackgroundResource(R.mipmap.final_broadcast_fun_background);
                layout3.setBackgroundResource(R.mipmap.final_broadcast_fun_background);
                layout4.setBackgroundResource(R.mipmap.final_broadcast_fun_background);
                BroadcastAdapter adapter=new BroadcastAdapter(ShowBroadcast.this,selecteditems,lat,lng,imageLoader);
                PAGER_NUM=selecteditems.size();
                viewPager.setAdapter(adapter);
            }
        });
        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecteditems.clear();
                for(BroadcastCard broadcastCard:BroadcastItems){
                    if(broadcastCard.getCategory().equals("饕客推薦")){
                        selecteditems.add(broadcastCard);
                    }
                }
                layout1.setBackgroundResource(R.mipmap.final_broadcast_gourmet_background);
                layout2.setBackgroundResource(R.mipmap.final_broadcast_gourmet_background);
                layout3.setBackgroundResource(R.mipmap.final_broadcast_gourmet_background);
                layout4.setBackgroundResource(R.mipmap.final_broadcast_gourmet_background);
                BroadcastAdapter adapter=new BroadcastAdapter(ShowBroadcast.this,selecteditems,lat,lng,imageLoader);
                PAGER_NUM=selecteditems.size();
                viewPager.setAdapter(adapter);
            }
        });
        young.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecteditems.clear();
                for(BroadcastCard broadcastCard:BroadcastItems){
                    if(broadcastCard.getCategory().equals("文青天地")){
                        selecteditems.add(broadcastCard);
                    }
                }
                layout1.setBackgroundResource(R.mipmap.final_broadcast_hipster_background);
                layout2.setBackgroundResource(R.mipmap.final_broadcast_hipster_background);
                layout3.setBackgroundResource(R.mipmap.final_broadcast_hipster_background);
                layout4.setBackgroundResource(R.mipmap.final_broadcast_hipster_background);
                BroadcastAdapter adapter=new BroadcastAdapter(ShowBroadcast.this,selecteditems,lat,lng,imageLoader);
                PAGER_NUM=selecteditems.size();
                viewPager.setAdapter(adapter);
            }
        });
        secret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecteditems.clear();
                for(BroadcastCard broadcastCard:BroadcastItems){
                    if(broadcastCard.getCategory().equals("私房景點")){
                        selecteditems.add(broadcastCard);
                    }
                }
                layout1.setBackgroundResource(R.mipmap.final_broadcast_hotspot_background);
                layout2.setBackgroundResource(R.mipmap.final_broadcast_hotspot_background);
                layout3.setBackgroundResource(R.mipmap.final_broadcast_hotspot_background);
                layout4.setBackgroundResource(R.mipmap.final_broadcast_hotspot_background);
                BroadcastAdapter adapter=new BroadcastAdapter(ShowBroadcast.this,selecteditems,lat,lng,imageLoader);
                PAGER_NUM=selecteditems.size();
                viewPager.setAdapter(adapter);
            }
        });
        if(!atleastone_history || !success_history) {
            history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ShowBroadcast.this,"附近沒有歷史介紹",Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShowBroadcast.this, ShowHistory.class);
                    startActivity(intent);
                }
            });
        }
    }
    private View.OnClickListener mOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.broadcast_last_btn:
                    if(mCurrentViewID != 0){
                        mCurrentViewID--;
                        viewPager.setCurrentItem(mCurrentViewID, true);
                    }
                    break;
                case R.id.broadcast_next_btn:
                    if(mCurrentViewID != PAGER_NUM-1){
                        mCurrentViewID++;
                        viewPager.setCurrentItem(mCurrentViewID, true);
                    }
                    break;
            }

        }};
    private ViewPager.OnPageChangeListener mOnPageChangeListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mCurrentViewID = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    public void guide(){
        String saddr = "saddr=" + lat + "," + lng;
        String daddr = "daddr=" + cardlat + "," + cardlng;
        String uriString = "http://maps.google.com/maps?" + saddr + "&" + daddr;
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
    public class SearchBroadcast extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            if(!atleastone && success) {
                Toast.makeText(ShowBroadcast.this,"附近目前沒有大聲公呦",Toast.LENGTH_SHORT).show();
            }
            else if(!success){
                Toast.makeText(ShowBroadcast.this,"網路異常，請稍後再試",Toast.LENGTH_SHORT).show();
            }
            else {
                adapter=new BroadcastAdapter(ShowBroadcast.this,BroadcastItems,lat,lng,imageLoader);
                viewPager.setAdapter(adapter);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url=new URL("http://134.208.97.233:80/SearchBroadcast.php");


                JSONObject postDataParams=new JSONObject();

                postDataParams.put("lat",lat);
                postDataParams.put("lng",lng);


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

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    String input;
                    input = sb.toString();
                    if (!input.equals("fail") && !input.equals("zero")) {
                        String[] temp;
                        success=true;
                        atleastone=true;
                        temp = input.split("&&");
                        int n = 0;
                        int len = temp.length;
                        len = len / 7;
                        for (int i = 0; i < len; i++) {
                            String title;
                            String image;
                            String txt;
                            String Category;
                            Double Lat;
                            Double Lng;
                            String Time;
                            title = temp[n].trim();
                            n++;
                            image = "http://134.208.97.233:80/Uploads/Broadcast/" + temp[n];
                            n++;
                            txt = temp[n].trim();
                            txt=txt.replace("|", "\n");
                            n++;
                            Category = temp[n];
                            Category=Category.trim();
                            n++;
                            Lat = Double.parseDouble(temp[n]);
                            n++;
                            Lng = Double.parseDouble(temp[n]);
                            n++;
                            Time=temp[n].trim();
                            n++;
                            BroadcastCard broadcastCard = new BroadcastCard(Lat, Lng, image, title, txt, Category,Time);
                            BroadcastItems.add(broadcastCard);
                        }
                    }
                    else if(input.equals("zero")){
                            success=true;
                        }
                    return sb.toString();
                } else {
                    return new String("false:" + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception:" + e.getMessage());
            }
        }
    }
    public class SearchHistory extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            ShowHistory.historyItems=historyItemList;
            if(!atleastone_history || !success_history) {
                history.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ShowBroadcast.this,"附近沒有歷史介紹",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                history.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ShowBroadcast.this, ShowHistory.class);
                        startActivity(intent);
                    }
                });
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url=new URL("http://134.208.97.233:80/SearchHistory.php");


                JSONObject postDataParams=new JSONObject();

                postDataParams.put("lat",lat);
                postDataParams.put("lng",lng);


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
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    String input;
                    input = sb.toString();
                    if (!input.equals("fail") && !input.equals("zero")) {
                        String[] temp;
                        success_history=true;
                        atleastone_history=true;
                        temp = input.split("&&");
                        int n = 0;
                        int len = temp.length;
                        len = len / 6;
                        for (int i = 0; i < len; i++) {
                            String title;
                            String newpicture;
                            String oldpicture;
                            String txt;
                            Double Lat;
                            Double Lng;
                            title = temp[n];
                            n++;
                            newpicture = "http://134.208.97.233:80/HistoryPicture/" + temp[n];
                            n++;
                            oldpicture = "http://134.208.97.233:80/HistoryPicture/" + temp[n];
                            n++;
                            txt = temp[n];
                            txt=txt.replace("|", "\n");
                            n++;
                            Lat = Double.parseDouble(temp[n]);
                            n++;
                            Lng = Double.parseDouble(temp[n]);
                            n++;
                            HistoryItem historyItem = new HistoryItem(Lat, Lng,newpicture,oldpicture, title, txt);
                            historyItemList.add(historyItem);
                        }
                    }
                    else if(input.equals("zero")){
                        success_history=true;
                    }
                    return sb.toString();
                } else {
                    return new String("false:" + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception:" + e.getMessage());
            }
        }
    }
    public String getPostDataString(JSONObject params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {
            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

}
