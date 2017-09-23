package com.sourcey.Seeker;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFriendByAccount extends Fragment {

    DisplayImageOptions options;
    public static ImageLoader imageLoader = ImageLoader.getInstance();
    private List<Friend> friendlist=FriendScreen.friends;
    private List<NewFriend> AllMemberList=new ArrayList<>();
    private List<NewFriend> newFriendList=new ArrayList<>();
    public static  List<String> friendaccountlist=new ArrayList<>();
    private EditText friendaccount;
    private ListView listView;
    private NewFriendAdapter adapter;
    private Button search;
    private String searchaccount;
    private ReleaseBitmap releaseBitmap=new ReleaseBitmap();
    public AddFriendByAccount() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseBitmap.cleanBitmapList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_friend_by_account, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AllMemberList.clear();
        new SelectAllAccount().execute();
        processViews();
        processControllers();
        if(friendlist.size()>0){
            friendaccountlist=getFriendList(friendlist);
        }
    }
    private List<String> getFriendList(List<Friend> friendlist){
        List<String> friendaccount=new ArrayList<>();
        for(Friend friend:friendlist){
            friendaccount.add(friend.getAccount());
        }
        return friendaccount;
    }
    public static  boolean checkFriend(String account,List<String> friendaccountlist){
        boolean same=false;
        for(String friend_account :friendaccountlist){
            if(account.equals(friend_account)){
                same=true;
                return same;
            }
        }
        return same;
    }
    private void processViews(){
        imageLoader.setDefaultLoadingListener(releaseBitmap);
        friendaccount=(EditText) getView().findViewById(R.id.newfriendaccount);
        listView=(ListView) getView().findViewById(R.id.addfriendlist);
        search=(Button) getView().findViewById(R.id.searchnewfriendlist);
        options= new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.final_ninja)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .considerExifParams(false)
                .imageScaleType(ImageScaleType.EXACTLY)
                .resetViewBeforeLoading(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoaderConfiguration config=new ImageLoaderConfiguration.Builder(getActivity()).threadPoolSize(3).defaultDisplayImageOptions(options).build();
        imageLoader.init(config);
    }
    private void processControllers(){
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newFriendList.clear();
                searchaccount=friendaccount.getText().toString();
                for(NewFriend newFriend:AllMemberList){
                    if(newFriend.getAccount().contains(searchaccount))
                        newFriendList.add(newFriend);
                }
                adapter=new NewFriendAdapter(getActivity(),newFriendList,imageLoader);
                listView.setAdapter(adapter);
            }
        });
    }
    public class SelectAllAccount extends AsyncTask<String,Void,String> {

        protected void onPreExecute(){}
        @Override
        protected void onPostExecute(String result){
        }
        @Override
        protected String doInBackground(String... params) {
            try{
                URL url=new URL("http://134.208.97.233:80/NewFriends.php");


                JSONObject postDataParams=new JSONObject();


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
                        String []temp;
                        temp=input.split("&&");
                        int n=0;
                        int len=temp.length;
                        len=len/3;
                        for(int i=0;i<len;i++){
                            String account=temp[n];
                            n++;
                            String nickname=temp[n];
                            n++;
                            String photo="http://134.208.97.233:80/Uploads/Profilepicture/"+temp[n];
                            n++;
                            AllMemberList.add(new NewFriend(account,nickname,photo));
                        }

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
