package com.sourcey.Seeker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by carl on 2017/6/2.
 */

public class BroadcastAdapter extends PagerAdapter {
    Context context;
    ImageLoader imageLoader;
    LayoutInflater inflater;
    List<BroadcastCard> broadcastCards;
    String startlat,startlng;
    public BroadcastAdapter (Context context,List<BroadcastCard> broadcastCards,String startlat,String startlng,ImageLoader imageLoader){
        this.context=context.getApplicationContext();
        this.imageLoader=imageLoader;
        this.broadcastCards=broadcastCards;
        this.startlat=startlat;
        this.startlng=startlng;
    }
    @Override
    public int getCount() {
        return broadcastCards.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view== object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position){
        inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView=inflater.inflate(R.layout.broadcastpageritem,container,false);
        ImageView imageView=(ImageView) itemView.findViewById(R.id.boradcastListImage);
        final TextView txt=(TextView) itemView.findViewById(R.id.BroadcastListText);
        TextView title=(TextView)itemView.findViewById(R.id.broadcastListTitle);
        TextView time=(TextView) itemView.findViewById(R.id.broadcastListTime);
        time.setText(broadcastCards.get(position).getTime());
        txt.setText(broadcastCards.get(position).getText());
        txt.setMovementMethod(ScrollingMovementMethod.getInstance());
        title.setText(broadcastCards.get(position).getTitle());
        ShowBroadcast.cardlat=broadcastCards.get(position).getLatitude();
        ShowBroadcast.cardlng=broadcastCards.get(position).getLongitude();



        imageLoader.displayImage(broadcastCards.get(position).getPicture(),imageView);
        container.addView(itemView);
        return itemView;
    }


    @Override
    public void destroyItem(View container,int position, Object object){
        ((ViewPager)container).removeView((View)object);
    }
}
