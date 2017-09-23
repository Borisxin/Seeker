package com.sourcey.Seeker;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carl on 2017/5/24.
 */

public class FreeWifi {
    public static List<Double> wifi_lat=new ArrayList<>();
    public static List<Double> wifi_lng=new ArrayList<>();
    public static List<String> wifi_place=new ArrayList<>();
    public static void LoadWifi(Context context){
        wifi_lat.clear();
        wifi_lng.clear();
        AssetManager assetManager=context.getAssets();
        InputStream inputStream1=null;
        InputStream inputStream2=null;
        InputStream inputStream3=null;
        try{
            inputStream1=assetManager.open("Latitude.txt");
            loadTextFile_double(inputStream1,wifi_lat);
            inputStream2=assetManager.open("Longitude.txt");
            loadTextFile_double(inputStream2,wifi_lng);
            inputStream3=assetManager.open("Place.txt");
            loadTextFile_string(inputStream3,wifi_place);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void  loadTextFile_double(InputStream inputStream, List<Double> wifi)throws IOException{
        BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
        String str=null;
        while(true){
            str=reader.readLine();
            if(str!=null) {
                double temp = Double.parseDouble(str);
                wifi.add(temp);
            }
            else
                break;
        }
        inputStream.close();
    }
    private static void  loadTextFile_string(InputStream inputStream, List<String> wifi)throws IOException{
        BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
        String str=null;
        while(true){
            str=reader.readLine();
            if(str!=null) {
                wifi.add(str);
            }
            else
                break;
        }
        inputStream.close();
    }

}
