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

import org.w3c.dom.Text;


public class Show_Puzzle_Card extends Activity {

    public static String title;
    public static String text;
    public static String url;
    public static String time;
    public static Bitmap bitmap;
    public static double cardlat;
    public static double cardlng;
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
        setContentView(R.layout.activity_show_puzzle_card);
        processViews();
        processControllers();
    }
    private void processViews(){
        Time=(TextView) findViewById(R.id.puzzle_card_time);
        Title =(TextView) findViewById(R.id.puzzle_card_title);
        Text=(TextView) findViewById(R.id.puzzle_card_content);
        Photo=(ImageView) findViewById(R.id.puzzle_card_pic);
        navigation=(Button) findViewById(R.id.puzzle_card_navigation);
        close=(Button) findViewById(R.id.puzzle_card_back);
    }
    private void processControllers(){
        CardShowCabinet.imageLoader.displayImage(url,Photo);
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
                Show_Puzzle_Card.this.finish();
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
