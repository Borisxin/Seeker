package com.sourcey.Seeker;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.widget.BaseAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.List;

/**
 * Created by gn963 on 2017/6/2.
 */

public class GridAdapter_MyCard extends BaseAdapter {
    private Context context;
    private List<MyCard> myCards;
    LayoutInflater inflater;
    ImageLoader imageLoader;
    public GridAdapter_MyCard(Context context,List<MyCard> myCards,ImageLoader imageLoader){
        this.myCards=myCards;
        this.imageLoader=imageLoader;
        this.context=context;
        inflater=LayoutInflater.from(context.getApplicationContext());
    }
    @Override
    public int getCount() {
        return myCards.size();
    }

    @Override
    public Object getItem(int position) {
        return myCards.get(position);
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
            view=inflater.inflate(R.layout.photo_view_mycard,parent,false);
            viewHolder=new ViewHolder(
                    (ImageButton) view.findViewById(R.id.photo_mycard),
                    (TextView) view.findViewById(R.id.photo_mycard_txt)
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
        viewHolder.textView.setText(myCards.get(position).getTitle());
        /*之後更改成範圍內圖片 不在範圍內圖片兩種*/
        if(myCards.get(position).getIsinRange()) {
            viewHolder.imageButton.setEnabled(true);
            viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Show_Puzzle_Card.text=myCards.get(position).getText();
                    Show_Puzzle_Card.title=myCards.get(position).getTitle();
                    Show_Puzzle_Card.url=myCards.get(position).getPicture();
                    Show_Puzzle_Card.cardlat=myCards.get(position).getLatitude();
                    Show_Puzzle_Card.cardlng=myCards.get(position).getLongitude();
                    Show_Puzzle_Card.time=myCards.get(position).getTime();
                    Intent intent=new Intent(context,Show_Puzzle_Card.class);
                    context.startActivity(intent);
                }
            });
             viewHolder.imageButton.setImageResource(R.drawable.final_unlock_puzzle); // 設定能開啟的圖片

        }
        else{
            viewHolder.imageButton.setEnabled(true);
             viewHolder.imageButton.setImageResource(R.drawable.final_lock_puzzle); //設定無法開啟的圖片

        }
        //imageLoader.displayImage(url.get(position),viewHolder.imageButton);

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
                final LatLng cardlatlng=new LatLng(myCards.get(position).getLatitude(),myCards.get(position).getLongitude());
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