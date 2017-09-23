package com.sourcey.Seeker;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by carl on 2017/6/29.
 */

public class HistoryAdapter extends PagerAdapter {
    Context context;
    ImageLoader imageLoader;
    LayoutInflater inflater;
    List<HistoryItem> historyItems;
    public HistoryAdapter(Context context,List<HistoryItem> historyItems,ImageLoader imageLoader){
        this.context=context.getApplicationContext();
        this.imageLoader=imageLoader;
        this.historyItems=historyItems;
    }
    @Override
    public int getCount() {
        return historyItems.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position){
        inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView=inflater.inflate(R.layout.historypage,container,false);
      //  ImageView newpic=(ImageView) itemView.findViewById(R.id.historynewpic);
        ImageView oldpic=(ImageView) itemView.findViewById(R.id.historyoldpic);
        TextView title=(TextView) itemView.findViewById(R.id.historytitle);
        TextView txt=(TextView) itemView.findViewById(R.id.historytxt);
     //   TextView count=(TextView) itemView.findViewById(R.id.historycount);
       // imageLoader.displayImage(historyItems.get(position).getNewPicture(),newpic);
        imageLoader.displayImage(historyItems.get(position).getOldPicture(),oldpic);
      //  count.setText((position+1)+"/"+historyItems.size());
        title.setText(historyItems.get(position).getTitle());
        txt.setText(historyItems.get(position).getText());
        container.addView(itemView);
        return itemView;
    }
    public void destroyItem(View container,int position, Object object){
        ((ViewPager)container).removeView((View)object);
    }
}
