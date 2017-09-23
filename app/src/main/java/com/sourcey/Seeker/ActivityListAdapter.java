package com.sourcey.Seeker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by carl on 2017/6/7.
 */

public class ActivityListAdapter extends BaseAdapter {
    private List<ActivityItem> activityItems;
    private ImageLoader imageLoader;
    private Context context;
    private LayoutInflater myInflater;
    private String lat;
    private String lng;
    public ActivityListAdapter(Context context,List<ActivityItem> activityItems,String lat,String lng, ImageLoader imageLoader){
        myInflater= LayoutInflater.from(context.getApplicationContext());
        this.context=context;
        this.activityItems=activityItems;
        this.imageLoader=imageLoader;
        this.lat=lat;
        this.lng=lng;
    }
    @Override
    public int getCount() {
        return activityItems.size();
    }

    @Override
    public Object getItem(int position) {
        return activityItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        viewHolder holder=null;
        if(convertView == null){
            convertView=myInflater.inflate(R.layout.popularactivity,null);
            holder=new viewHolder(
                    (TextView) convertView.findViewById(R.id.activityname),
                    (TextView) convertView.findViewById(R.id.activityaddress),
                    (ImageView) convertView.findViewById(R.id.activityimage),
                    (Button) convertView.findViewById(R.id.joinactivity),
                    (Button) convertView.findViewById(R.id.moredetail)
            );
            convertView.setTag(holder);
        }
        else{
            holder=(viewHolder) convertView.getTag();
        }

        holder.activity_name.setText(activityItems.get(position).getActivity_Name());
        holder.address.setText(activityItems.get(position).getAddress());
        imageLoader.displayImage(activityItems.get(position).getPicture(),holder.imageView);
        if(activityItems.get(position).getDistance().equals("200m")){
            holder.join.setText("參加活動");
            holder.join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog(activityItems.get(position).getActivity_Name(),activityItems.get(position).getNature());
                }
            });
        }
        else{
            holder.join.setText("導航");
            holder.join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String fromGPStr =lat+ "," + lng;
                    String destGPStr = activityItems.get(position).getLatitude()+ ","
                            + String.valueOf(activityItems.get(position).getLongitude());
                    Uri uri = Uri.parse("http://maps.google.com/maps?f=d&saddr="
                            + fromGPStr + "&daddr=" + destGPStr);
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setData(uri);
                    context.startActivity(intent);
                }
            });
        }

        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_Detail.activityItem=activityItems.get(position);
                Intent intent=new Intent(context,Activity_Detail.class);
                context.startActivity(intent);
            }
        });
        return convertView;
    }
    private void dialog(final String activityName,final String category){

        AlertDialog.Builder builder = new AlertDialog.Builder(context); //創建訊息方塊

        builder.setMessage("將開啟相機，拍一張好看的照片吧!");

        builder.setTitle("參加活動?");

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
                Intent intent=new Intent(context,CallCamera.class);
                intent.putExtra("activityname",activityName);
                intent.putExtra("category",category);
                context.startActivity(intent);
            }

        });



        builder.create().show();

    }
    private class viewHolder{
        TextView activity_name;
        TextView address;
        ImageView imageView;
        Button join,detail;
        public viewHolder(TextView activity_name, TextView address, ImageView imageview, Button join, Button detail){
            this.activity_name=activity_name;
            this.address=address;
            this.join=join;
            this.detail=detail;
            this.imageView=imageview;
        }
    }
}
