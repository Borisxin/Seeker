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

public class NewFriendAdapter extends BaseAdapter {
    private List<NewFriend> newFriendList;
    private ImageLoader imageLoader;
    private Context context;
    private LayoutInflater myInflater;
    private String newfriendaccount="";
    private Boolean isSuccess=false;
    public NewFriendAdapter(Context context,List<NewFriend> newFriendList, ImageLoader imageLoader){
        myInflater= LayoutInflater.from(context.getApplicationContext());
        this.context=context.getApplicationContext();
        this.newFriendList=newFriendList;
        this.imageLoader=imageLoader;
    }
    @Override
    public int getCount() {
        return newFriendList.size();
    }

    @Override
    public Object getItem(int position) {
        return newFriendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        viewHolder holder=null;
        if(convertView == null){
            convertView=myInflater.inflate(R.layout.addfriendlist,null);
            holder=new viewHolder(
                    (TextView) convertView.findViewById(R.id.newfriendaccount),
                    (TextView) convertView.findViewById(R.id.newfriendname),
                    (ImageView) convertView.findViewById(R.id.newfriendphoto),
                    (ImageButton) convertView.findViewById(R.id.addthisfriend)
            );
            convertView.setTag(holder);
        }
        else{
            holder=(viewHolder) convertView.getTag();
        }
        holder.account.setText(newFriendList.get(position).getAccount());
        holder.name.setText(newFriendList.get(position).getNickname());
        if(newFriendList.get(position).getPhoto().equals("http://134.208.97.233:80/Uploads/Profilepicture/no"))
            imageLoader.displayImage("drawable://" +R.drawable.final_ninja,holder.imageView);
        else
             imageLoader.displayImage(newFriendList.get(position).getPhoto(),holder.imageView);
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newfriendaccount=newFriendList.get(position).getAccount();
                isSuccess=false;
                if(!newfriendaccount.equals(Login.UAC)) {
                    if(AddFriendByAccount.friendaccountlist.size()>0 && AddFriendByAccount.checkFriend(newfriendaccount,AddFriendByAccount.friendaccountlist)){
                        Toast.makeText(context, newfriendaccount+"已是您的好友...", Toast.LENGTH_SHORT).show();
                    }
                    else if(AddFriendByAccount.friendaccountlist.size()>0 && !AddFriendByAccount.checkFriend(newfriendaccount,AddFriendByAccount.friendaccountlist)){
                        new SendPostRequest().execute();
                    }
                    else if(AddFriendByAccount.friendaccountlist.size()==0){
                        new SendPostRequest().execute();
                    }
                }
                else{
                    Toast.makeText(context, "這是您自己的帳號", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }

    private class viewHolder{
        TextView account;
        TextView name;
        ImageView imageView;
        ImageButton add;
        public viewHolder(TextView account, TextView name, ImageView imageview, ImageButton add){
            this.account=account;
            this.name=name;
            this.add=add;
            this.imageView=imageview;
        }
    }
    public class SendPostRequest extends AsyncTask<String,Void,String> {

        protected void onPreExecute(){}
        @Override
        protected void onPostExecute(String result){
            if(!isSuccess) {
                Toast.makeText(context, "申請好友失敗...", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(context, "申請成功，等待好友確認中...", Toast.LENGTH_SHORT).show();
                AddFriendByAccount.friendaccountlist.add(newfriendaccount);
            }
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                URL url=new URL("http://134.208.97.233:80/AddFriend.php");


                JSONObject postDataParams=new JSONObject();
                postDataParams.put("ID",Login.UID);
                postDataParams.put("account",Login.UAC);
                postDataParams.put("friendaccount",newfriendaccount);
                postDataParams.put("nickname",Login.NICKNAME);
                postDataParams.put("gender",Login.GENDER);
                postDataParams.put("photo",Login.nohttp_Profilepicture);


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
                    String input=sb.toString();
                    if(!input.equals("fail")) {
                        isSuccess=true;
                    }

                    in.close();
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


