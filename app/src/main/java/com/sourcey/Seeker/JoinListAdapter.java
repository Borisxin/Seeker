package com.sourcey.Seeker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

/**
 * Created by carl on 2017/6/7.
 */

public class JoinListAdapter extends BaseAdapter {
    private List<JoinHistoryItem> joinHistoryItems;
    private Boolean success=false;
    private ImageLoader imageLoader;
    private Context context;
    private LayoutInflater myInflater;
    private String CurrentCardID=null;
    private int CurrentPosition;
    public JoinListAdapter(Context context,List<JoinHistoryItem> joinHistoryItems, ImageLoader imageLoader){
        myInflater= LayoutInflater.from(context.getApplicationContext());
        this.context=context;
        this.imageLoader=imageLoader;
        this.joinHistoryItems=joinHistoryItems;
    }
    @Override
    public int getCount() {
        return joinHistoryItems.size();
    }

    @Override
    public Object getItem(int position) {
        return joinHistoryItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        viewHolder holder=null;
        if(convertView == null){
            convertView=myInflater.inflate(R.layout.joinhistoryitem,null);
            holder=new viewHolder(
                    (TextView) convertView.findViewById(R.id.joinactivityname),
                    (TextView) convertView.findViewById(R.id.joinactivitytime),
                    (ImageButton) convertView.findViewById(R.id.joinactivityimage),
                    (TextView) convertView.findViewById(R.id.isUsed)
            );
            convertView.setTag(holder);
        }
        else{
            holder=(viewHolder) convertView.getTag();
        }
        if(joinHistoryItems.get(position).getIsUsed()){
            holder.isused.setVisibility(View.INVISIBLE);
            holder.imageView.setClickable(true);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog(position);
                }
            });
        }
        else{
            holder.isused.setVisibility(View.VISIBLE);
            holder.imageView.setClickable(false);
        }
        holder.activity_name.setText(joinHistoryItems.get(position).getActivity_name());
        holder.time.setText(joinHistoryItems.get(position).getTime());
        imageLoader.displayImage(joinHistoryItems.get(position).getPicture(),holder.imageView);
        return convertView;
    }

    private void dialog(final int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(context); //創建訊息方塊

        builder.setMessage("是否要使用?");

        builder.setTitle("警告");

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()  {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("確認", new DialogInterface.OnClickListener()  {

            @Override

            public void onClick(DialogInterface dialog, int which) {
                CurrentPosition=position;
                success=false;
                CurrentCardID=joinHistoryItems.get(position).getID();
                new SendPostRequest().execute();
                dialog.dismiss();

            }

        });



        builder.create().show();

    }

    private class viewHolder{
        TextView activity_name;
        TextView time;
        ImageButton imageView;
        TextView isused;
        public viewHolder(TextView activity_name, TextView time, ImageButton imageview,TextView isused){
            this.activity_name=activity_name;
            this.time=time;
            this.imageView=imageview;
            this.isused=isused;
        }
    }


    public class SendPostRequest extends AsyncTask<String,Void,String> {

        protected void onPreExecute(){}
        @Override
        protected void onPostExecute(String result){
            if(success) {
                Toast.makeText(context,"成功使用!",Toast.LENGTH_LONG).show();
                joinHistoryItems.get(CurrentPosition).setIsUsed(false);
                notifyDataSetChanged();
            }
            else{
                Toast.makeText(context,"網路異常，請稍後在試",Toast.LENGTH_LONG).show();
            }
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                URL url=new URL("http://134.208.97.233:80/UseActivityCard.php");
                JSONObject postDataParams=new JSONObject();
                postDataParams.put("CardID",CurrentCardID);
                Log.e("params",postDataParams.toString());

                HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os=conn.getOutputStream();
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if(responseCode==HttpURLConnection.HTTP_OK){
                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb=new StringBuffer("");
                    String line="";

                    while((line=in.readLine())!=null){

                        sb.append(line);
                        break;
                    }
                    in.close();
                    String input; //php回傳值
                    input=sb.toString();
                    Log.e("成功",input);
                    if(!input.equals("false") ) {
                        success = true;
                    }
                    return sb.toString();
                }
                else{
                    return new String("false:"+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception:"+e.getMessage());
            }

        }
    }
    public String getPostDataString(JSONObject params) throws Exception{
        StringBuilder result=new StringBuilder();
        boolean first=true;

        Iterator<String> itr=params.keys();

        while(itr.hasNext()){
            String key=itr.next();
            Object value=params.get(key);

            if(first)
                first=false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key,"UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(),"UTF-8"));
        }
        return  result.toString();
    }
}
