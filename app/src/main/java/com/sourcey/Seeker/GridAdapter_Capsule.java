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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by gn963 on 2017/6/2.
 */

public class GridAdapter_Capsule extends BaseAdapter {
    private Context context;
    private List<CapsuleCard> capsuleCards;
    LayoutInflater inflater;
    ImageLoader imageLoader;
    private Calendar now=Calendar.getInstance();
    private Calendar cardtime=Calendar.getInstance();
    long diff;
    public GridAdapter_Capsule(Context context,List<CapsuleCard> capsuleCards,ImageLoader imageLoader){
        this.capsuleCards=capsuleCards;
        this.imageLoader=imageLoader;
        this.context=context;
        inflater=LayoutInflater.from(context.getApplicationContext());
    }
    @Override
    public int getCount() {
        return capsuleCards.size();
    }

    @Override
    public Object getItem(int position) {
        return capsuleCards.get(position);
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
            view=inflater.inflate(R.layout.photo_view_capsule,parent,false);
            viewHolder=new ViewHolder(
                    (ImageButton) view.findViewById(R.id.photo_capsule),
                    (TextView) view.findViewById(R.id.photo_capsule_txt)
            );
            view.setTag(viewHolder);
        }
        else{
            viewHolder=(ViewHolder) view.getTag();
        }
        viewHolder.txt.setText(capsuleCards.get(position).getTitle());
        viewHolder.imageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dialog(position);
                return true;
            }
        });
        /*之後更改成範圍內圖片 不在範圍內圖片兩種*/
        if(capsuleCards.get(position).getIsinRange()) {
            if(capsuleCards.get(position).getExpired()) {
                viewHolder.imageButton.setEnabled(true);
                viewHolder.imageButton.setImageResource(R.drawable.final_unlock_capsule); // 設定能開啟的圖片

                viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Show_Capsule_Card.time=capsuleCards.get(position).getTime();
                        Show_Capsule_Card.text=capsuleCards.get(position).getText();
                        Show_Capsule_Card.title=capsuleCards.get(position).getTitle();
                        Show_Capsule_Card.url=capsuleCards.get(position).getPicture();
                        Show_Capsule_Card.cardlat=capsuleCards.get(position).getLatitude();
                        Show_Capsule_Card.cardlng=capsuleCards.get(position).getLongitude();
                        Show_Capsule_Card.video=capsuleCards.get(position).getVideo();
                        Intent intent=new Intent(context,Show_Capsule_Card.class);
                        context.startActivity(intent);
                    }
                });
            }
            else{
                viewHolder.imageButton.setEnabled(true);

                viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            long aDayInMilliSecond = 60 * 60 * 24 * 1000;
                            Date date=format.parse(capsuleCards.get(position).getOpenTime());
                            cardtime.setTime(date);
                            diff=(cardtime.getTimeInMillis()-now.getTimeInMillis())/ aDayInMilliSecond;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
//                        Toast.makeText(context,"距離開啟時間還有："+diff+"天",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(context,Show_Capsule_Time.class);
                        intent.putExtra("diff",diff);
                        context.startActivity(intent);
                    }
                });
                viewHolder.imageButton.setImageResource(R.drawable.final_lock_capsule); //設定範圍內但時間未到的圖片

            }
        }
        else{
            viewHolder.imageButton.setEnabled(true);

            viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        long aDayInMilliSecond = 60 * 60 * 24 * 1000;
                        Date date=format.parse(capsuleCards.get(position).getOpenTime());
                        cardtime.setTime(date);
                        diff=(cardtime.getTimeInMillis()-now.getTimeInMillis())/ aDayInMilliSecond;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(diff>0)
                        Toast.makeText(context,"距離開啟時間還有："+diff+"天",Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context,"已經可以打開囉!",Toast.LENGTH_SHORT).show();
                }
            });
             viewHolder.imageButton.setImageResource(R.drawable.final_lock_capsule); //設定無法開啟的圖片

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
                final LatLng cardlatlng=new LatLng(capsuleCards.get(position).getLatitude(),capsuleCards.get(position).getLongitude());
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
        TextView txt;
        public ViewHolder(ImageButton imageButton,TextView txt){

            this.txt=txt;
            this.imageButton=imageButton;
        }
    }
}