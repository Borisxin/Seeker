package com.sourcey.Seeker;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carl on 2017/8/23.
 */

public class ReleaseBitmap implements ImageLoadingListener {

    private List<Bitmap> mBitmaps;

    public ReleaseBitmap(){
        mBitmaps=new ArrayList<>();
    }
    public void cleanBitmapList(){
        if(mBitmaps.size()>0){
            Log.e("在這在這 回收囉",String.valueOf("有"+mBitmaps.size()));
            for(int i=0;i<mBitmaps.size();i++){
                Bitmap b=mBitmaps.get(i);
                if(b != null && !b.isRecycled()){
                    b.recycle();
                }
            }
        }
    }
    @Override
    public void onLoadingStarted(String imageUri, View view) {

    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        mBitmaps.add(loadedImage);
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {

    }
}
