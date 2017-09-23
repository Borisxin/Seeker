package com.sourcey.Seeker;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

public class MainScreen extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button setting;
    private Button addnewcard;
    private Button gallery;
    private Button chatroom;
    private Button broadcast;
    private Button activityscreen;
    private Button reload;
    private String UAC= Login.UAC;
    /**************************************************************/
    private List<MyCard> MyCardList=new ArrayList<>();
    private boolean mycard_Thread=false;
    private String mycardhttp ="http://134.208.97.233:80/Uploads/MyCard/"; //MyCard圖片儲存位置
    /**************************************************************/
    private List<CapsuleCard> CapsuleCardList=new ArrayList<>();
    private boolean capsule_Thread=false;
    private String capsulehttp ="http://134.208.97.233:80/Uploads/CapsuleCard/"; //Capsule圖片儲存位置
    /**************************************************************/
    private List<FriendsCard> GiveCardList=new ArrayList<>();
    private boolean CTF_Thread=false;
    private String cardtofriendhttp ="http://134.208.97.233:80/Uploads/CardToFriend/"; //CTF圖片儲存位置
    /**************************************************************/
    private List<FriendsCard> RecieveCardList=new ArrayList<>();
    private boolean CFF_Thread=false;
    private String cardfromfriendhttp ="http://134.208.97.233:80/Uploads/CardFromFriend/"; //CFF圖片儲存位置
    /**************************************************************/
    public static String latitude;
    public static String longitude;
    private double nowlat;
    private double nowlng;
    private GPSTracker gpsTracker;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS=1;
    /*       測試隱藏wifi   */
    private  List<Marker> wifi=new ArrayList<>();
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(gpsTracker !=null)
        gpsTracker.stopUsingGPS();
        unregisterReceiver(close_myself);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        wifi.clear();
        mycard_Thread=false;
        capsule_Thread=false;
        CFF_Thread=false;
        CTF_Thread=false;
        mapFragment.getMapAsync(this);
        processViews();
        processControllers();
        registerReceiver(close_myself, new IntentFilter("CloseActivities"));
    }

    private void processViews(){
        reload=(Button) findViewById(R.id.reloadmainscreen);
        addnewcard=(Button) findViewById(R.id.addnewcard);
        gallery=(Button) findViewById(R.id.Gallery);
        chatroom=(Button) findViewById(R.id.chatroom);
        activityscreen=(Button) findViewById(R.id.gotoactivity);
        broadcast=(Button) findViewById(R.id.Broadcast);
        setting=(Button) findViewById(R.id.settingbtn);
    }

    private void processControllers(){
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                overridePendingTransition(0, 0);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainScreen.this,Adjust.class);
                startActivity(intent);
                MainScreen.this.finish();
            }
        });
        broadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainScreen.this,ShowBroadcast.class);
                startActivity(intent);
            }
        });
        addnewcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(MainScreen.this, ChooseType.class);
                startActivity(intent);
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(MainScreen.this, CardShowCabinet.class);
                startActivity(intent);
            }
        });
        activityscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(MainScreen.this,popularActivity.class);
                startActivity(intent);
            }
        });
        chatroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainScreen.this,ChatRoom.class);
                startActivity(intent);
            }
        });
    }
    private final BroadcastReceiver close_myself = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
    public boolean onKeyDown(int keyCode,KeyEvent event){

        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){   //確定按下退出鍵and防止重複按下退出鍵

            dialog();

        }

        return false;

    }

    private void dialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainScreen.this); //創建訊息方塊

        builder.setMessage("確定要離開 Seeker？");

        builder.setTitle("離開");

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()  {

            @Override

            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }

        });

        builder.setPositiveButton("確認", new DialogInterface.OnClickListener()  {

            @Override

            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                MainScreen.this.finish();

            }

        });



        builder.create().show();

    }

    private void mycard_makemarker( List<MyCard> myCardList){
        int a = myCardList.size();
        for(MyCard myCard:myCardList) {
            double lat=myCard.getLatitude();
            double lng=myCard.getLongitude();
            if(getDistance(nowlat,nowlng,lat,lng)){
                String str=mycardhttp+myCard.getPicture();
                myCard.setPicture(str);
                myCard.setIsinRange(true);
                CardShowCabinet.MyCardList.add(myCard);
            }
            else{
                myCard.setIsinRange(false);
                CardShowCabinet.N_MyCardList.add(myCard);
            }
            LatLng marker = new LatLng(lat,lng);
            mMap.addMarker(new MarkerOptions().position(marker).icon(BitmapDescriptorFactory.fromResource(R.drawable.final_greenicon)).title(myCard.getTitle()));
        }
    }

    private void capsule_makemarker(List<CapsuleCard> CapsuleCard){
        for(CapsuleCard capsuleCard:CapsuleCard) {
            double lat=capsuleCard.getLatitude();
            double lng=capsuleCard.getLongitude();
            if(getDistance(nowlat,nowlng,lat,lng)){
                String str=capsulehttp+capsuleCard.getPicture();
                capsuleCard.setPicture(str);
                capsuleCard.setIsinRange(true);
                CardShowCabinet.CapsuleList.add(capsuleCard);
            }
            else{
                capsuleCard.setIsinRange(false);
                CardShowCabinet.N_CapsuleList.add(capsuleCard);
            }
            LatLng marker = new LatLng(lat,lng);
            mMap.addMarker(new MarkerOptions().position(marker).icon(BitmapDescriptorFactory.fromResource(R.drawable.final_orangeicon)).title(capsuleCard.getTitle()));
        }
    }

    private void CFF_makemarker(List<FriendsCard> friendsCards){
        for(FriendsCard friendCard:friendsCards) {
            double lat=friendCard.getLatitude();
            double lng=friendCard.getLongitude();
            if(getDistance(nowlat,nowlng,lat,lng)){
                String str=cardfromfriendhttp+friendCard.getPicture();
                friendCard.setPicture(str);
                friendCard.setIsinRange(true);
                CardShowCabinet.RecieveList.add(friendCard);
            }
            else{
                friendCard.setIsinRange(false);
                CardShowCabinet.N_RecieveList.add(friendCard);
            }
            LatLng marker = new LatLng(lat,lng);
            mMap.addMarker(new MarkerOptions().position(marker).icon(BitmapDescriptorFactory.fromResource(R.drawable.final_redicon)).title(friendCard.getTitle()));
        }
    }

    private void CTF_makemarker( List<FriendsCard> friendsCards){
        for(FriendsCard friendCard:friendsCards) {
            double lat=friendCard.getLatitude();
            double lng=friendCard.getLongitude();
            if(getDistance(nowlat,nowlng,lat,lng)){
                String str=cardtofriendhttp+friendCard.getPicture();
                friendCard.setPicture(str);
                friendCard.setIsinRange(true);
                CardShowCabinet.GiveList.add(friendCard);
            }
            else{
                friendCard.setIsinRange(false);
                CardShowCabinet.N_GiveList.add(friendCard);
            }
            LatLng marker = new LatLng(lat,lng);
            mMap.addMarker(new MarkerOptions().position(marker).icon(BitmapDescriptorFactory.fromResource(R.drawable.final_grayicon)).title(friendCard.getTitle()));
        }
    }

    private void wifi_makemarker( List<Double> latitude, List<Double> longitude, List<String> place){
        int a = latitude.size();
        for(int i=0;i<a;i++) {
            double lat=latitude.get(i);
            double lng=longitude.get(i);
            if(getDistance_forwifi(nowlat,nowlng,lat,lng)){
                LatLng marker = new LatLng(lat,lng);
                Marker m=mMap.addMarker(new MarkerOptions().position(marker).icon(BitmapDescriptorFactory.fromResource(R.drawable.final_wifiicon)).title(place.get(i)));
                m.setVisible(false);
                wifi.add(m);
            }

        }
        String checkedwifi=getConfig(MainScreen.this,"wifi","show","");
        if(checkedwifi.equals("1")){
            for(Marker marker:wifi){
                marker.setVisible(true);
            }
        }
        else{
            for(Marker marker:wifi){
                marker.setVisible(false);
            }
        }
    }

    public boolean getDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results=new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        if(results[0]<=500) {return true;}
        else {return false;}
    }
    public boolean getDistance_forwifi(double lat1, double lon1, double lat2, double lon2) {
        float[] results=new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        if(results[0]<=10000) {return true;}
        else {return false;}
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        new SendPostRequest_mycard().execute();
        new SendPostRequest_capsule().execute();
        new SendPostRequest_CTF().execute();
        new SendPostRequest_CFF().execute();
        try {
            int i=0;
            while((!mycard_Thread  || !capsule_Thread || !CFF_Thread || !CTF_Thread) && i!=100) {
                Thread.sleep(30);
                i++;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(MainScreen.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                &ContextCompat.checkSelfPermission(MainScreen.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainScreen.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    &ActivityCompat.shouldShowRequestPermissionRationale(MainScreen.this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                new AlertDialog.Builder(MainScreen.this)
                        .setMessage("Seeker必須要定位才能執行喔!")
                        .setPositiveButton("同意", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainScreen.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
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
                ActivityCompat.requestPermissions(MainScreen.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
        else
        {
            mMap.setMyLocationEnabled(true);
            gpsTracker=new GPSTracker(MainScreen.this);
            Location location=gpsTracker.getLocation();
            if(location == null)
            {
                nowlat = 0;
                nowlng = 0;
                Toast.makeText(MainScreen.this,"請開啟定位功能，系統目前抓取不到您的經緯度",Toast.LENGTH_LONG).show();
            }
            else {
                nowlat = location.getLatitude();
                nowlng = location.getLongitude();
            }
            latitude=String.valueOf(nowlat);
            longitude=String.valueOf(nowlng);
            LatLng now=new LatLng(nowlat,nowlng);
            wifi_makemarker(FreeWifi.wifi_lat,FreeWifi.wifi_lng,FreeWifi.wifi_place);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(now));
            /**A=時光膠囊 B=記憶拼圖 C=收到的悄悄話 D=送出的悄悄話 E=大聲公**/
            int a= CapsuleCardList.size();
            CardShowCabinet.CapsuleList.clear();
            CardShowCabinet.N_CapsuleList.clear();
            if(a>0) {capsule_makemarker(CapsuleCardList);}
            /******************************************/
            /******************************************/
            int b=MyCardList.size();
            CardShowCabinet.MyCardList.clear();
            CardShowCabinet.N_MyCardList.clear();
            if(b>0) {mycard_makemarker(MyCardList);}
            /******************************************/
            /******************************************/
            int c=RecieveCardList.size();
            CardShowCabinet.RecieveList.clear();
            CardShowCabinet.N_RecieveList.clear();
            if(c>0) {CFF_makemarker(RecieveCardList);}
            /******************************************/
            /******************************************/
            int d=GiveCardList.size();
            CardShowCabinet.GiveList.clear();
            CardShowCabinet.N_GiveList.clear();
            if(d>0) {CTF_makemarker(GiveCardList);}
        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = getIntent();
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    MainScreen.this.finish();


                } else {
                }
                return;
            }
        }
    }
    public class SendPostRequest_mycard extends AsyncTask<String,Void,String> {

        protected void onPreExecute(){}
        @Override
        protected void onPostExecute(String result){
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                URL url=new URL("http://134.208.97.233:80/MyCardMarker.php");
                JSONObject postDataParams=new JSONObject();
                postDataParams.put("UAC",UAC);
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
                    String input;
                    input=sb.toString();
                    if(!input.equals("fail") && !input.equals("zero")) {
                        String[] temp;
                        mycard_Thread = true;
                        temp = input.split("&&");
                        int n=0;
                        int len=temp.length;
                        len=len/6;
                        for (int i = 0; i <len; i++) {
                            Double Latitude=Double.parseDouble(temp[n].trim());
                            n++;
                            Double Longitude=Double.parseDouble(temp[n].trim());
                            n++;
                            String Picture=temp[n].trim();
                            n++;
                            String Title=temp[n].trim();
                            n++;
                            String Text=temp[n].trim();
                            Text=Text.replace("|", "\n");
                            n++;
                            String Time=temp[n].trim();
                            n++;
                            MyCard myCard=new MyCard(Latitude,Longitude,Picture,Title,Text,false,Time);
                            MyCardList.add(myCard);
                        }
                    }
                    else{
                        mycard_Thread=true;
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

    public class SendPostRequest_capsule extends AsyncTask<String,Void,String> {

        protected void onPreExecute(){}
        @Override
        protected void onPostExecute(String result){
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                URL url=new URL("http://134.208.97.233:80/CapsuleMarker.php");


                JSONObject postDataParams=new JSONObject();

                postDataParams.put("UAC",UAC);

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
                    String input;
                    input=sb.toString();
                    if(!input.equals("fail") && !input.equals("zero")) {
                        String[] temp;

                        temp = input.split("&&");
                        int n=0;
                        int len=temp.length;
                        len=len/10;
                        for (int i = 0; i <len; i++) {
                            Double Latitude=Double.parseDouble(temp[n].trim());
                            n++;
                            Double Longitude=Double.parseDouble(temp[n].trim());
                            n++;
                            String Picture=temp[n].trim();
                            n++;
                            String Video=temp[n].trim();
                            n++;
                            String Audio=temp[n].trim();
                            n++;
                            String Title=temp[n].trim();
                            n++;
                            String Text=temp[n].trim();
                            Text=Text.replace("|", "\n");
                            n++;
                            String OpenTime=temp[n].trim();
                            n++;
                            Boolean Expired;
                            if(temp[n].trim().equals("1"))
                                Expired=false;
                            else
                                Expired=true;
                            n++;
                            String Time=temp[n].trim();
                            n++;
                            CapsuleCard tempCapsule=new CapsuleCard(Latitude,Longitude,Picture,Video,Audio,Title,Text,OpenTime,false,Expired,Time);
                            CapsuleCardList.add(tempCapsule);
                        }
                        capsule_Thread = true;
                    }
                    else{
                        capsule_Thread=true;
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

    public class SendPostRequest_CTF extends AsyncTask<String,Void,String> {

        protected void onPreExecute(){}
        @Override
        protected void onPostExecute(String result){
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                URL url=new URL("http://134.208.97.233:80/ToFCardMarker.php");


                JSONObject postDataParams=new JSONObject();

                postDataParams.put("UAC",UAC);


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
                    String input;
                    input=sb.toString();
                    if(!input.equals("fail") && !input.equals("zero")) {
                        String[] temp;
                        CTF_Thread = true;
                        temp = input.split("&&");
                        int n=0;
                        int len=temp.length;
                        len=len/7;

                        for (int i = 0; i <len; i++) {
                            Double Latitude=Double.parseDouble(temp[n].trim());
                            n++;
                            Double Longitude=Double.parseDouble(temp[n].trim());
                            n++;
                            String Picture=temp[n].trim();
                            n++;
                            String Title=temp[n].trim();
                            n++;
                            String Text=temp[n].trim();
                            Text=Text.replace("|", "\n");
                            n++;
                            String DisappearTime=temp[n].trim();
                            n++;
                            String Time=temp[n].trim();
                            n++;
                            FriendsCard friendsCard=new FriendsCard(Latitude,Longitude,Picture,Title,Text,DisappearTime,false,Time);
                            GiveCardList.add(friendsCard);

                        }
                    }
                    else{
                        CTF_Thread=true;
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

    public class SendPostRequest_CFF extends AsyncTask<String,Void,String> {

        protected void onPreExecute(){}
        @Override
        protected void onPostExecute(String result){
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                URL url=new URL("http://134.208.97.233:80/FromFCardMarker.php");


                JSONObject postDataParams=new JSONObject();

                postDataParams.put("UAC",UAC);

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
                    String input;
                    input=sb.toString();
                    if(!input.equals("fail") && !input.equals("zero")) {
                        String[] temp;
                        CFF_Thread = true;
                        temp = input.split("&&");
                        int n=0;
                        int len=temp.length;
                        len=len/8;
                        for (int i = 0; i <len; i++) {
                            Double Latitude=Double.parseDouble(temp[n].trim());
                            n++;
                            Double Longitude=Double.parseDouble(temp[n].trim());
                            n++;
                            String Picture=temp[n].trim();
                            n++;
                            String Title=temp[n].trim();
                            n++;
                            String Text=temp[n].trim();
                            Text=Text.replace("|", "\n");
                            n++;
                            String DisappearTime=temp[n].trim();
                            n++;
                            String Sender=temp[n].trim();
                            n++;
                            String Time=temp[n].trim();
                            n++;
                            FriendsCard friendsCard=new FriendsCard(Latitude,Longitude,Picture,Title,Text,DisappearTime,false,Sender,Time);
                            RecieveCardList.add(friendsCard);
                        }
                    }
                    else{
                        CFF_Thread=true;
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

    //設定檔讀取
    public static String getConfig(Context context , String name , String
            key , String def)
    {
        SharedPreferences settings =context.getSharedPreferences(name,0);
        return settings.getString(key, def);
    }
}
