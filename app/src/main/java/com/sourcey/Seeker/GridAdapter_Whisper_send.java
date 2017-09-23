package com.sourcey.Seeker;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.BaseAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by gn963 on 2017/6/2.
 */

public class GridAdapter_Whisper_send extends BaseAdapter {
    private Context context;
    private List<FriendsCard> friendsCards;
    LayoutInflater inflater;
    ImageLoader imageLoader;
    private Calendar now=Calendar.getInstance();
    private Calendar cardtime=Calendar.getInstance();
    long diff;
    public GridAdapter_Whisper_send(Context context, List<FriendsCard> friendsCards, ImageLoader imageLoader){
        this.friendsCards=friendsCards;
        this.imageLoader=imageLoader;
        this.context=context;
        inflater=LayoutInflater.from(context.getApplicationContext());
    }
    @Override
    public int getCount() {
        return friendsCards.size();
    }

    @Override
    public Object getItem(int position) {
        return friendsCards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view=convertView;

        final ViewHolder viewHolder;
        if(convertView==null){
            view=inflater.inflate(R.layout.photo_view_whisper,parent,false);
            viewHolder=new ViewHolder(
                    (ImageButton) view.findViewById(R.id.photo_whisper),
                    (TextView) view.findViewById(R.id.photo_whisper_txt)
            );
            view.setTag(viewHolder);
        }
        else{
            viewHolder=(ViewHolder) view.getTag();
        }
        viewHolder.imageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dialog(position);
                return true;
            }
        });
        viewHolder.textView.setBackgroundResource(R.drawable.final_bar_send);
        viewHolder.textView.setText(friendsCards.get(position).getTitle());
        /*之後更改成範圍內圖片 不在範圍內圖片兩種*/
        if(friendsCards.get(position).getIsinRange()) {
            viewHolder.imageButton.setEnabled(true);
            viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        long aDayInMilliSecond = 60 * 60 * 24 * 1000;
                        Date date=format.parse(friendsCards.get(position).getDisappearTime());
                        cardtime.setTime(date);
                        diff=(cardtime.getTimeInMillis()-now.getTimeInMillis())/ aDayInMilliSecond;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Show_Send_Card.send_diff=diff;
                    Show_Send_Card.text=friendsCards.get(position).getText();
                    Show_Send_Card.title=friendsCards.get(position).getTitle();
                    Show_Send_Card.url=friendsCards.get(position).getPicture();
                    Show_Send_Card.cardlat=friendsCards.get(position).getLatitude();
                    Show_Send_Card.cardlng=friendsCards.get(position).getLongitude();
                    Show_Send_Card.time=friendsCards.get(position).getTime();
                    Intent intent=new Intent(context,Show_Send_Card.class);
                    context.startActivity(intent);
                }
            });
            viewHolder.imageButton.setImageResource(R.drawable.final_unlock_send);  //設定能開啟的圖片
        }
        else{
            viewHolder.imageButton.setEnabled(true);
            viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        long aDayInMilliSecond = 60 * 60 * 24 * 1000;
                        Date date=format.parse(friendsCards.get(position).getDisappearTime());
                        cardtime.setTime(date);
                        diff=(cardtime.getTimeInMillis()-now.getTimeInMillis())/ aDayInMilliSecond;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(diff>0)
                        Toast.makeText(context,"距離消失時間還有："+diff+"天\n快點去看吧!",Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context,"距離消失時間還有："+0+"天\n快點去看吧!",Toast.LENGTH_SHORT).show();
                }
            });
            viewHolder.imageButton.setImageResource(R.drawable.final_lock_send); //設定無法開啟的圖片
        }

        return view;
    }
    private void dialog(final int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(context); //創建訊息方塊

        builder.setMessage("開啟導航？");

        builder.setTitle("GO");

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()  {

            @Override

            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }

        });

        builder.setPositiveButton("確定", new DialogInterface.OnClickListener()  {

            @Override

            public void onClick(DialogInterface dialog, int which) {
                String lat=MainScreen.latitude;
                String lng=MainScreen.longitude;
                double mylat,mylng;
                mylat=Double.parseDouble(lat);
                mylng=Double.parseDouble(lng);
                final LatLng mylatlng=new LatLng(mylat,mylng);
                final LatLng cardlatlng=new LatLng(friendsCards.get(position).getLatitude(),friendsCards.get(position).getLongitude());
                route(mylatlng,cardlatlng);
                dialog.dismiss();


            }

        });



        builder.create().show();

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
        context.startActivity(intent);
    }
    static class ViewHolder{
        ImageButton imageButton;
        TextView textView;
        public ViewHolder(ImageButton imageButton,TextView textView){
            this.imageButton=imageButton;
            this.textView=textView;
        }
    }
}