package com.sourcey.Seeker;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class FriendLIstAdapter extends BaseAdapter implements Filterable {
    private LayoutInflater myInflater;
    public static  List<Friend> friends;
    private Context context;
    private List<Friend> originalFriend;
    private MyFilter filter;
    private boolean success=false;
    private String nowaccount="";
    private String ID= Login.UID;
    private String account= Login.UAC;
    private String gender= Login.GENDER;
    private String nickname= Login.NICKNAME;
    private int now;
    ImageLoader imageLoader;
    public FriendLIstAdapter(Context context,List<Friend> friends,ImageLoader imageLoader){
        myInflater=LayoutInflater.from(context.getApplicationContext());
        this.friends=friends;
        this.context=context.getApplicationContext();
        this.imageLoader=imageLoader;
    }
    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Object getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return friends.indexOf(getItem(position));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        viewHolder holder=null;
        if(convertView == null){
            convertView=myInflater.inflate(R.layout.friendlist,null);
            holder=new viewHolder(
                    (TextView) convertView.findViewById(R.id.listfriendaccount),
                    (TextView) convertView.findViewById(R.id.listfriendnickname),
                    (TextView) convertView.findViewById(R.id.wait),
                    (ImageView) convertView.findViewById(R.id.listfriendgender),
                    (ImageButton) convertView.findViewById(R.id.ycheckfriend),
                    (ImageButton) convertView.findViewById(R.id.ncheckfriend)
            );
            convertView.setTag(holder);
        }
        else{
            holder=(viewHolder) convertView.getTag();
        }
        holder.name.setText(friends.get(position).getNickname());
        holder.account.setText(friends.get(position).getAccount());
        if(friends.get(position).getPhoto().equals("http://134.208.97.233:80/Uploads/Profilepicture/no"))
            imageLoader.displayImage("drawable://" +R.drawable.final_ninja,holder.imageView);
        else
            imageLoader.displayImage(friends.get(position).getPhoto(),holder.imageView);
        holder.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowaccount=friends.get(position).getAccount();
                now=position;
                new SendPostRequest_yes().execute();

            }
        });
        holder.no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowaccount=friends.get(position).getAccount();
                now=position;
                new SendPostRequest_no().execute();
            }
        });
        if(friends.get(position).getIscheck().equals("2")){
            holder.yes.setVisibility(View.GONE);
            holder.no.setVisibility(View.GONE);
            holder.wait.setText("");
        }
        else if(friends.get(position).getIscheck().equals("0")){
            holder.yes.setVisibility(View.GONE);
            holder.no.setVisibility(View.GONE);
            holder.wait.setText("等待接受中...");
        }
        else{
            holder.yes.setVisibility(View.VISIBLE);
            holder.no.setVisibility(View.VISIBLE);
            holder.wait.setText("");
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter= new MyFilter();
        }
        return  filter;
    }

    private class viewHolder{
        TextView account;
        TextView name;
        TextView wait;
        ImageView imageView;
        ImageButton yes,no;
        public viewHolder(TextView account, TextView name, TextView wait , ImageView imageview, ImageButton yes, ImageButton no){
            this.account=account;
            this.name=name;
            this.wait=wait;
            this.imageView=imageview;
            this.yes=yes;
            this.no=no;
        }
    }
    public class SendPostRequest_yes extends AsyncTask<String,Void,String> {

        protected void onPreExecute(){}
        @Override
        protected void onPostExecute(String result){
            if(!success) {
                Toast.makeText(context,"確認好友失敗，請稍後再試...", Toast.LENGTH_SHORT).show();
            }
            else {
                friends.get(now).setIscheck("2");
                FriendScreen.AdapterChange();
                Toast.makeText(context,"已和"+nowaccount+"成為好友",Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                URL url=new URL("http://134.208.97.233:80/FriendConfirm.php");


                JSONObject postDataParams=new JSONObject();

                postDataParams.put("account",account);
                postDataParams.put("ID",ID);
                postDataParams.put("friendaccount",nowaccount);
                postDataParams.put("NickName",nickname);
                postDataParams.put("Gender",gender);

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
                    if(!input.equals("fail")) {
                        success=true;
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
    public class SendPostRequest_no extends AsyncTask<String,Void,String> {

        protected void onPreExecute(){}
        @Override
        protected void onPostExecute(String result){
            if(!success) {
                Toast.makeText(context,"刪除好友失敗，請稍後再試...", Toast.LENGTH_SHORT).show();
            }
            else {
                friends.remove(now);
                FriendScreen.AdapterChange();
                Toast.makeText(context,"已拒絕和"+nowaccount+"成為好友",Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                URL url=new URL("http://134.208.97.233:80/FriendDelete.php");


                JSONObject postDataParams=new JSONObject();

                postDataParams.put("account",account);
                postDataParams.put("ID",ID);
                postDataParams.put("friendaccount",nowaccount);


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
                    if(!input.equals("fail")) {
                        success=true;
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
    private class MyFilter extends  Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            constraint=constraint.toString();
            FilterResults results=new FilterResults();
            if(originalFriend == null){
                synchronized (this){
                    originalFriend=new ArrayList<>(friends);
                }
            }
            if(constraint !=null && constraint.toString().length()>0){
                ArrayList<Friend> filterItems=new ArrayList<>();
                for(Friend friend:originalFriend){
                    if(friend.getAccount().contains(constraint)){
                        filterItems.add(friend);
                    }
                }
                results.count=filterItems.size();
                results.values=filterItems;
            }
            else{
                synchronized (this)
                {
                    ArrayList<Friend> list=new ArrayList<>(originalFriend);
                    results.values=list;
                    results.count=list.size();
                }
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            friends=(ArrayList<Friend>)results.values;
            if(results.count>0){
                notifyDataSetChanged();
            }
            else{
                notifyDataSetInvalidated();
            }
        }
    }
}
