package com.sourcey.Seeker;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService{

    public static  int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage.getData().size()>0){
            Log.e("FCM data","Data Patload:"+remoteMessage.getData().toString());
            try{
                JSONObject jsonObject=new JSONObject(remoteMessage.getData().toString());
                JSONObject dataObject=jsonObject.getJSONObject("data");
                String type=dataObject.getString("type");
                if(type.equals("message")) {
                    String imageURL=dataObject.getString("image");
                    String title=dataObject.getString("title");
                    String time=dataObject.getString("time");
                    String nickname=dataObject.getString("nickname");
                    if (imageURL.equals("http://134.208.97.233:80/Uploads/Profilepicture/no")) {
                        generateNotification(title, time, nickname);
                    } else {
                        Bitmap bitmap = getBitmapFromURL(imageURL);
                        notificationWithImage(bitmap, title, time, nickname);
                    }
                }
                else if(type.equals("capsule")){
                    String title=dataObject.getString("title");
                    String time=dataObject.getString("time");
                    generateCapsuleNotification(title, time);
                }
                else{
                    /*推播活動*/
                    String imageURL=dataObject.getString("image");
                    String title=dataObject.getString("title");
                    String time=dataObject.getString("time");
                    String content=dataObject.getString("content");
                    Bitmap bitmap = getBitmapFromURL(imageURL);
                    Activitynotification(bitmap,title,time,content);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void generateCapsuleNotification(String title,String time){
        Intent intent =new Intent(this,Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.BigTextStyle bigTextStyle=new android.support.v4.app.NotificationCompat.BigTextStyle();
        bigTextStyle.bigText("標題："+title+"\n時光膠囊埋藏時間: "+time );
        NotificationCompat.Builder mNotifyBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.final_unlock_capsulesmall)
                .setContentTitle("時光膠囊可以開啟囉!")
                .setContentText("標題："+title+"\n時光膠囊埋藏時間: "+time )
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setStyle(bigTextStyle);
        NotificationManager notificationManager=
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(NOTIFICATION_ID > 1073741824){
            NOTIFICATION_ID=0;
        }
        notificationManager.notify(NOTIFICATION_ID++,mNotifyBuilder.build());
    }

    private void generateNotification(String title,String time,String nickname){
        Intent intent =new Intent(this,Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mNotifyBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.final_unlock_recievesmall)
                .setContentTitle("收到"+nickname+" 傳給您的卡片")
                .setContentText("卡片到期時間為: "+time )
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager=
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(NOTIFICATION_ID > 1073741824){
            NOTIFICATION_ID=0;
        }
        notificationManager.notify(NOTIFICATION_ID++,mNotifyBuilder.build());
    }

    private void notificationWithImage(Bitmap bitmap,String title,String time,String nickname){
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle("收到"+nickname+" 傳給您的卡片");
        bigPictureStyle.setSummaryText("卡片到期時間為: "+time);
        bigPictureStyle.bigPicture(bitmap);
        NotificationCompat.Builder mNotifyBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Seeker-收到"+nickname+" 傳給您的卡片")
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri)
                .setStyle(bigPictureStyle)
                .setSmallIcon(R.mipmap.final_unlock_recievesmall)
                .setContentText("卡片到期時間為: "+time);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (NOTIFICATION_ID > 1073741824) {
            NOTIFICATION_ID = 0;
        }
        notificationManager.notify(NOTIFICATION_ID++ , mNotifyBuilder.build());
    }

    private void Activitynotification(Bitmap bitmap,String title,String time,String content){
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(content);
        bigPictureStyle.bigPicture(bitmap);
        NotificationCompat.Builder mNotifyBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri)
                .setStyle(bigPictureStyle)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(content);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (NOTIFICATION_ID > 1073741824) {
            NOTIFICATION_ID = 0;
        }
        notificationManager.notify(NOTIFICATION_ID++ , mNotifyBuilder.build());
    }
    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("width,height",String.valueOf(myBitmap.getWidth()+" h:"+myBitmap.getHeight()));
            myBitmap= Bitmap.createScaledBitmap(myBitmap,1024,512,true);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
