package com.sourcey.Seeker;

import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;


public class ShowHistory extends AppCompatActivity {


    DisplayImageOptions options;
    public static  ImageLoader imageLoader = ImageLoader.getInstance();
    private ViewPager ViewPager;
    private HistoryAdapter adapter;
    private Button back;
    public static  List<HistoryItem> historyItems=null;
    private ReleaseBitmap releaseBitmap=new ReleaseBitmap();
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_history);
        processViews();
        processContorllers();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseBitmap.cleanBitmapList();
    }
    private void processViews(){
        back=(Button) findViewById(R.id.historyback);
        ViewPager=(android.support.v4.view.ViewPager) findViewById(R.id.historyviewpager);
        imageLoader.setDefaultLoadingListener(releaseBitmap);
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
        adapter=new HistoryAdapter(ShowHistory.this,historyItems,imageLoader);
    }
    private void processContorllers(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ViewPager.setAdapter(adapter);
    }
}
