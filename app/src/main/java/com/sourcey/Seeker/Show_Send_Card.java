package com.sourcey.Seeker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;



public class Show_Send_Card extends Activity {

    public static String title;
    public static String text;
    public static String url;
    public static long send_diff;
    public static String time;
    public static Bitmap bitmap;
    public static double cardlat;
    public static double cardlng;
    private TextView Diff;
    private TextView Time;
    private TextView Title;
    private TextView Text;
    private ImageView Photo;
    private Button navigation;
    private Button close;
    private String lat=MainScreen.latitude;
    private String lng=MainScreen.longitude;
    private double mylng;
    private double mylat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_show_send_card);
        processViews();
        processControllers();
    }
    private void processViews(){
        Diff=(TextView) findViewById(R.id.send_card_diff);
        Time=(TextView) findViewById(R.id.send_card_time);
        Title =(TextView) findViewById(R.id.send_card_title);
        Text=(TextView) findViewById(R.id.send_card_content);
        Photo=(ImageView) findViewById(R.id.send_card_pic);
        navigation=(Button) findViewById(R.id.send_card_navigation);
        close=(Button) findViewById(R.id.send_card_back);
    }
    private void processControllers(){
        CardShowCabinet.imageLoader.displayImage(url,Photo);
        Diff.setText("倒數"+send_diff+"天");
        Time.setText(time);
        Title.setText(title);
        Text.setText(text);
        navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mylat=Double.parseDouble(lat);
                mylng=Double.parseDouble(lng);
                final LatLng mylatlng=new LatLng(mylat,mylng);
                final LatLng cardlatlng=new LatLng(cardlat,cardlng);
                route(mylatlng,cardlatlng);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Show_Send_Card.this.finish();
            }
        });
    }
    public void route(LatLng fromGP, LatLng destGP) {
        String fromGPStr = String.valueOf(fromGP.latitude) + ","
                + String.valueOf(fromGP.longitude);
        String destGPStr = String.valueOf(destGP.latitude) + ","
                + String.valueOf(destGP.longitude);
        Uri uri = Uri.parse("http://maps.google.com/maps?f=d&saddr="
                + fromGPStr + "&daddr=" + destGPStr);
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setData(uri);
        startActivity(intent);
    }
}
